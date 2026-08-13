# login-service

Serviço de autenticação para reaproveitar entre projetos, em vez de escrever login de novo em cada um. A ideia é ser um Auth0/Firebase Auth caseiro.

Cada cliente cadastra a aplicação dele, recebe uma `apiKey` e passa a usar o serviço para cadastro, confirmação por e-mail, login, sessão e recuperação de senha.

## Como funciona

O front do cliente não fala com este serviço. Quem fala é o backend dele, que guarda a `apiKey`.

```
front do cliente  ->  backend do cliente  ->  login-service  ->  MySQL
                       (guarda a apiKey)                          Redis
                                                                  SMTP (Brevo)
```

A `apiKey` identifica a aplicação inteira, não um usuário, por isso ela fica só no servidor. Como o consumo é backend a backend, não uso CORS, chave pública nem validação de `Origin`.

Cada aplicação é uma fronteira de isolamento: todo acesso é escopado por `application_id`, e usuários de aplicações diferentes são pessoas separadas.

| Entidade | Notas |
|---|---|
| `Application` | `apiKey` salva como SHA-256 e mostrada só uma vez, na ativação. Fica `enabled=false` até confirmar o e-mail |
| `User` | senha em BCrypt, força medida com zxcvbn (score mínimo 3). Nasce `PENDING` e só loga como `ACTIVE` |
| `VerificationCode` | OTP de 6 dígitos com `SecureRandom`, válido 15 min, marcado como usado ao consumir |
| `Session` | refresh token de 30 dias, salvo como SHA-256, rotacionado a cada refresh e revogável |

### Fluxos

- **Cliente novo:** `create` gera OTP por e-mail, `confirm-account` ativa e devolve a `apiKey`. Ela não volta depois, porque só guardo o hash.
- **Usuário novo:** `register` cria como `PENDING` e manda o OTP (envio assíncrono), `verify-code` ativa.
- **Login:** devolve access token de 15 min e refresh token de 30 dias. O `refresh` gera um par novo e invalida o anterior.
- **Senha:** `forgot-password` sempre responde igual, exista ou não o e-mail, para não permitir enumerar cadastro. `reset-password` troca a senha e revoga todas as sessões do usuário.

## Autenticação

O header `Authorization` é usado de duas formas:

| Uso | Formato |
|---|---|
| `apiKey`, identifica a aplicação | `Authorization: sk_live_...` sem `Bearer` |
| access token, identifica o usuário | `Authorization: Bearer eyJhbG...` |

Atenção no primeiro: comparo o SHA-256 do header inteiro, então prefixo ou espaço a mais devolve 401.

O access token é JWT HS256, expira em 15 min e leva o e-mail no `sub`. Ele é entregue para o backend do cliente usar na sessão dele. Nenhuma rota deste serviço exige access token hoje, tudo é autenticado por `apiKey`.

## Endpoints

Base `/api`, tudo `POST` e JSON. Os `/api/auth/*` exigem a `apiKey` no header.

| Endpoint | Body | Resposta |
|---|---|---|
| `/application/create` | `name`, `email`, `html` | 201 `msg`, `name`, `email`, `api_key` nulo |
| `/application/confirm-account` | `email`, `code` | 201 com a `api_key` |
| `/auth/register` | `name`, `email`, `password` | 201 `userId`, `email`, `msg` |
| `/auth/verify-code` | `email`, `code` | 201 `msg` |
| `/auth/login` | `email`, `password` | 200 `accessToken`, `refreshToken` |
| `/auth/refresh` | `refresh_token` | 200 `access_token`, `refresh_token` |
| `/auth/forgot-password` | `email` | 200 `msg` |
| `/auth/reset-password` | `email`, `code`, `newPassword` | 200 `msg` |

Dois detalhes do contrato: o `login` devolve os campos em camelCase e o `refresh` em snake_case, ainda quero padronizar. E o `html` não tem validação no DTO, mas a coluna é `NOT NULL`, então precisa ser enviado.

```bash
curl -X POST https://seu-dominio/api/auth/login -H 'Authorization: sk_live_SUA_CHAVE' -H 'Content-Type: application/json' -d '{"email":"maria@gmail.com","password":"senha-forte"}'
```

## Erros

Resposta padrão: `{ "msg": "..." }`

| HTTP | Quando |
|---|---|
| 400 | senha fraca |
| 401 | credencial, `apiKey`, aplicação desativada ou e-mail já em uso |
| 404 | código de verificação inválido ou expirado |
| 429 | rate limit |

O 401 é genérico de propósito: e-mail inexistente, senha errada e conta não confirmada respondem igual, para o login não servir de consulta de cadastro. O 429 da camada de IP sai como `{"error":"RATE_LIMIT_EXCEEDED"}`, porque vem do filtro e não passa pelo handler.

Erros fora do handler (validação de DTO, constraint, infra) saem no formato padrão do Spring.

## Rate limit

Bucket4j sobre Redis, então a contagem é compartilhada entre instâncias.

| Camada | Limites | Chave |
|---|---|---|
| IP, filtro em `/api/**` | 5/10s e 20/15min | `rate:login:ip:<ip>`, usa `CF-Connecting-IP` |
| E-mail, na entrada de cada operação | 10/15min | `rate:login:email:<email>` |

A camada de e-mail é a que segura brute force de senha e de OTP, já que trocar de IP não ajuda. A de IP precisa de folga porque no modelo backend a backend todos os usuários de um cliente chegam pelo mesmo IP.

## Banco

| Tabela | Papel |
|---|---|
| `applications` | clientes, `api_key` e `owner_email` únicos |
| `users` | pessoas, escopadas por `application_id` |
| `verification_codes` | OTPs, ligados a `user_id` ou a `application_id` |
| `sessions` | refresh tokens, com `revoked_at`, `expires_at` e `ip` |

Schema gerado pelo Hibernate a partir das entidades, sem Flyway. Local usa `ddl-auto=create` e recria a cada boot, produção tem properties próprio.

## Stack

Java 17, Spring Boot 3.2.5, MySQL 8, Redis 7 com bucket4j, Spring Security stateless, jjwt, BCrypt, zxcvbn, Spring Mail com SMTP da Brevo, Docker Compose, Nginx e Cloudflare Tunnel.

```
src/main/java/br/com/loginService/
├── controller/                 AuthController, ApplicationController
├── service/                    auth, application, security, email
├── repository/                 Spring Data JPA
├── model/                      User, Application, Session, VerificationCode
├── dto/                        external (contrato HTTP), internal
├── infrastructure/security/    config, filtros, OTP, ratelimit
└── exception/                  ErrorEnum, GlobalExceptionHandler
```

Security em modo stateless, com CSRF, form login e HTTP Basic desligados. A sessão fica na tabela `sessions`, não no servlet.

## Rodando local

```bash
cp .env.exemple .env
```

Preencha o `.env`, que está no `.gitignore`. Depois:

```bash
docker compose up -d --build
```

Sobem `nginx-proxy` na porta 80, `app`, `db` e `redis-auth`. A API responde em `http://localhost/api/...`, a 8080 não é publicada no host.

Para rodar a aplicação pela IDE, sobe só as dependências:

```bash
docker compose up -d db redis-auth
```

## Variáveis de ambiente

| Variável | Para quê |
|---|---|
| `SPRING_DATASOURCE_URL`, `_USERNAME`, `_PASSWORD` | MySQL |
| `MYSQL_ROOT_PASSWORD` | root do container do MySQL |
| `JWT_SECRET` | assinatura HS256, mínimo 32 caracteres |
| `MAIL_HOST`, `MAIL_PORT`, `EMAIL_LOGIN`, `SMTP_KEY`, `EMAIL_FROM` | SMTP da Brevo |
| `REDIS_HOST`, `REDIS_PORT` | Redis, padrão `redis-auth:6379` |

O `JWT_SECRET` é lido na inicialização do `AccessTokenService`. Se faltar ou for curto, a aplicação não sobe e o erro aparece como falha de inicialização de classe, então vale conferir essa variável primeiro. Trocar o segredo invalida os access tokens em circulação, os refresh tokens continuam valendo.

## Deploy

```
internet -> Cloudflare -> Tunnel -> nginx:80 -> app:8080
```

Uso Cloudflare Tunnel para publicar sem abrir porta de entrada no host, e é de lá que vem o `CF-Connecting-IP` usado no rate limit. O nginx repassa `X-Forwarded-For` e `X-Forwarded-Proto`, lidos com `server.forward-headers-strategy=framework`, que é o que faz o IP real chegar no rate limit.

Deploy manual por enquanto: `git` e `docker compose up -d --build`.

## Próximos passos

- Trocar o unique global de `users.email` por `(email, application_id)`, para a mesma pessoa poder existir em aplicações diferentes
- Permitir que o cliente valide o access token, com endpoint de introspecção ou assinatura assimétrica com JWKS
- Aplicar o `templateHtml` de cada aplicação no e-mail, que hoje sai de um template fixo
- Endpoint para rotacionar e revogar `apiKey`
- Padronizar camelCase nas respostas
- Testes dos fluxos de autenticação