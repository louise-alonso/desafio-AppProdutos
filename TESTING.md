# 🧪 Guia de Testes e Validação - AppProdutos

Este documento descreve o roteiro passo a passo para validar a segurança, autenticação e regras de negócio da API.

> **Pré-requisitos:**
> * A aplicação deve estar rodando (`mvn spring-boot:run`).
> * Ferramenta de API Client instalada (Postman ou Insomnia).
> * Navegador Web (para acesso ao H2 Console).

---

## 🔄 Ciclo 1: Configuração Inicial (Obrigatório a cada reinício)

Como o banco de dados H2 é em memória, ele inicia vazio. O primeiro passo é sempre criar o **Administrador**.

### 1. Gerar Hash da Senha
* **Rota:** `POST http://localhost:8080/encode`
* **Body (JSON):** `{ "password": "senhaadmin" }`
* **Ação:** Copie o hash gerado (ex: `$2a$10$...`).

### 2. Inserir Admin no Banco de Dados
* **Acesse:** `http://localhost:8080/h2-console` (Recomendado usar Janela Anônima).
* **Login:** JDBC URL: `jdbc:h2:mem:produtosdb` | User: `sa` | Password: (vazia).
* **SQL:** Cole e execute o comando abaixo (substituindo o hash):
    ```sql
    INSERT INTO tbl_users (user_id, name, email, password, role, created_at, updated_at) 
    VALUES ('admin-01', 'Chefe Admin', 'admin@email.com', 'COLE_SEU_HASH_AQUI', 'ROLE_ADMIN', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
    ```

---

## 🔑 Ciclo 2: Testes de Autenticação (Login)

### 1. Login como Admin (Sucesso)
* **Rota:** `POST http://localhost:8080/login`
* **Body:**
    ```json
    { "email": "admin@email.com", "password": "senhaadmin" }
    ```
* **Resultado Esperado:** `200 OK`.
* **Ação:** Copie o `token` retornado. Este será o **Token Admin**.

### 2. Login com Senha Errada (Falha)
* **Rota:** `POST http://localhost:8080/login`
* **Body:** `{ "email": "admin@email.com", "password": "errada" }`
* **Resultado Esperado:** `400 Bad Request` ou `401 Unauthorized`.

---

## 🛡️ Ciclo 3: Testes de Autorização (RBAC)

### 1. Criar Usuário Comum (Apenas Admin pode)
* **Rota:** `POST http://localhost:8080/admin/register`
* **Header:** `Authorization: Bearer <TOKEN_DO_ADMIN>`
* **Body:**
    ```json
    {
      "name": "Funcionario João",
      "email": "joao@empresa.com",
      "password": "senha123",
      "role": "ROLE_USER"
    }
    ```
* **Resultado Esperado:** `201 Created`.

### 2. Login como Usuário Comum
* Faça o login (`POST /login`) com o email `joao@empresa.com` e senha `senha123`.
* Copie o novo token. Este será o **Token User**.

### 3. Tentar Criar Usuário sendo Comum (Bloqueio de Segurança)
* **Rota:** `POST http://localhost:8080/admin/register`
* **Header:** `Authorization: Bearer <TOKEN_DO_USER>`
* **Resultado Esperado:** `403 Forbidden`. (O sistema bloqueia corretamente usuários sem permissão de Admin).

### 4. Tentar Deletar Categoria sendo Comum
* **Rota:** `DELETE http://localhost:8080/admin/categorias/1`
* **Header:** `Authorization: Bearer <TOKEN_DO_USER>`
* **Resultado Esperado:** `403 Forbidden`.

### 5. Acessar Rota Pública/Comum (Leitura)
* **Rota:** `GET http://localhost:8080/categorias`
* **Header:** `Authorization: Bearer <TOKEN_DO_USER>`
* **Resultado Esperado:** `200 OK`. (Usuários comuns têm permissão de leitura).

---

## 🧪 Ciclo 4: Validações e Integridade

### 1. Cadastro de Email Duplicado
* Tente criar um usuário com o mesmo email (`joao@empresa.com`) usando o **Token Admin**.
* **Resultado Esperado:** `400 Bad Request` (A aplicação impede duplicação).

### 2. Token Adulterado (Hacker)
* Pegue um token válido. Altere manualmente um caractere no meio dele.
* Tente fazer qualquer requisição autenticada.
* **Resultado Esperado:** `403 Forbidden` (A assinatura digital do token falhou).

---

## 🧹 Limpeza (Opcional)

### Deletar Usuário
* **Rota:** `DELETE http://localhost:8080/admin/users/{id}` (Use o ID do João).
* **Header:** `Authorization: Bearer <TOKEN_DO_ADMIN>`
* **Resultado Esperado:** `204 No Content`.