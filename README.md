Sistema de Login - Spring Boot com Docker Compose

Este é um sistema de login desenvolvido em Java com Spring Boot, que permite o cadastro, listagem, atualização e login de usuários. O sistema utiliza Docker Compose para orquestrar a aplicação e o banco de dados MySQL.

Funcionalidades:
Criar Usuário: Permite o cadastro de novos usuários.
Listar Usuários: Retorna todos os usuários cadastrados.
Atualizar Usuário: Atualiza informações de um usuário existente.
Login: Autentica o usuário com base nas credenciais fornecidas.

Tecnologias Utilizadas:
Java 17
Spring Boot 3
MySQL 8
Docker e Docker Compose
Maven

Requisitos:
Docker e Docker Compose
JDK 17
Maven

Configuração do Projeto:
1. Clone o repositório
2. Compile o projeto
3. Faça o build do projeto com o docker
Após iniciar a aplicação Spring Boot e o banco de dados MySQL. A aplicação estará disponível em http://localhost:8080

Testar a API:
   1. Criar usuário 
curl --location 'http://localhost:8080/users' \
--header 'Content-Type: application/json' \
--data-raw '    {
        "name": "Bob Esponja",
        "email": "Bob.esponja@gmail.com",
         "password": "password123",
          "phone": "48940028922"
    }'

   2. Realizar login
curl --location 'http://localhost:8080/users/login' \
--header 'Content-Type: application/json' \
--data-raw '    {
    "name": "Bob Esponja",
    "email": "bob@gmail.com",
    "password": "password123"
    }'

   3. Listar Usuários
curl --location 'http://localhost:8080/users' \
--header 'Authorization: Bearer {{SEU_TOKEN_AQUI}}'

   4. Atualizar Usuário
curl --location 'http://localhost:8080/users' \
--header 'Authorization: Bearer {{SEU_TOKEN_AQUI}}'

   5. Deteletar usuário
curl --location --request DELETE 'http://localhost:8080/users/1' \
--header 'Authorization: Bearer {{SEU_TOKEN_AQUI}}' \
--header 'Cookie: JSESSIONID=848750E0828E779DC62C0BD333C90D9A' \
--data ''

Para encerrar o ambiente, basta usar o comendo: 
docker-compose down


    
