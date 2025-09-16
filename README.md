# 🔐 Sistema de Login - Spring Boot com Docker Compose

Este é um sistema de login desenvolvido em **Java com Spring Boot**, que permite o cadastro, listagem, atualização, login e exclusão de usuários.  
O sistema utiliza **Docker Compose** para orquestrar a aplicação e o banco de dados **MySQL**.

---

## ✨ Funcionalidades
- Criar usuário  
- Listar usuários  
- Atualizar usuário  
- Login  
- Deletar usuário  

---

## 🛠 Tecnologias
- Java 17  
- Spring Boot 3  
- MySQL 8  
- Docker Compose  
- Maven  

---

## 📋 Requisitos
- Docker e Docker Compose  
- JDK 17  
- Maven  

---

## 🚀 Como rodar o projeto
1. Clone o repositório  
2. Faça o build do projeto com o Docker  
3. Inicie a aplicação Spring Boot e o banco de dados MySQL com o Docker Compose  

A aplicação estará disponível em:  
👉 [http://localhost:8080](http://localhost:8080)

---

## 📌 Endpoints (cURL)
```bash
curl --location 'http://localhost:8080/users' \
--header 'Content-Type: application/json' \
--data-raw '{"name":"Bob Esponja","email":"Bob.esponja@gmail.com","password":"password123","phone":"48940028922"}'

curl --location 'http://localhost:8080/users/login' \
--header 'Content-Type: application/json' \
--data-raw '{"name":"Bob Esponja","email":"bob@gmail.com","password":"password123"}'

curl --location 'http://localhost:8080/users' \
--header 'Authorization: Bearer {{SEU_TOKEN_AQUI}}'

curl --location --request PUT 'http://localhost:8080/users' \
--header 'Authorization: Bearer {{SEU_TOKEN_AQUI}}' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=6D41D7CD01787833399FA33ECEEFE04D' \
--data-raw '{"id":1,"name":"Bob Esponja calça quadrada","email":"Bob.esponja@gmail.com","password":"password125","phone":"48940028922"}'

curl --location --request DELETE 'http://localhost:8080/users/1' \
--header 'Authorization: Bearer {{SEU_TOKEN_AQUI}}' \
--header 'Cookie: JSESSIONID=848750E0828E779DC62C0BD333C90D9A'
