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
* **Ação:** Copie o hash gerado (ex: `$2a$10$...`). $2a$10$OVUbAwx3EWtYB5ckgFVBsuCVTfii.pMhzRPE/tC4yuvbwB14H5dOW

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
    { "email": "admin@email.com", "password": "senhaadmin" } eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBlbWFpbC5jb20iLCJyb2xlIjoiUk9MRV9BRE1JTiIsImV4cCI6MTc2MzY1NTA4OCwiaWF0IjoxNzYzNjUxNDg4fQ.QoiBN5oxw2-xPVr-nxEFpLj2nAt6eBAG6NpN_7bThb0
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
* Copie o novo token. Este será o **Token User**. eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2FvQGVtcHJlc2EuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImV4cCI6MTc2MzY1NTI1NSwiaWF0IjoxNzYzNjUxNjU1fQ.Nd0P6YpKHlOUtdaImEIKz0mgON-pIYsaiFlO3NYqvdU

### 3. Tentar Criar Usuário sendo Comum (Bloqueio de Segurança)
* **Rota:** `POST http://localhost:8080/admin/register`
* **Header:** `Authorization: Bearer <TOKEN_DO_USER>`
* **Resultado Esperado:** `403 Forbidden`. (O sistema bloqueia corretamente usuários sem permissão de Admin).

---

---

## 📦 Ciclo 4: Catálogo e Produtos

### 1. Criar Categoria PAI (Eletrônicos)
* **Rota:** `POST http://localhost:8080/admin/categorias`
* **Header:** `Authorization: Bearer <TOKEN_DO_ADMIN>`
* **Body:**
    ```json
    {
        "name": "Eletrônicos",
        "description": "Tudo que liga na tomada"
    }
    ```
* **Resultado Esperado:** `201 Created`.
* **Ação:** Copie o `categoryId` desta resposta (Ex: `e50b47a7...`).

### 2. Criar Categoria FILHO (Hierarquia)
* **Rota:** `POST http://localhost:8080/admin/categorias`
* **Header:** `Authorization: Bearer <TOKEN_DO_ADMIN>`
* **Body:** (Use o ID copiado acima no `parentId`)
    ```json
    {
        "name": "Celulares",
        "description": "Smartphones modernos",
        "parentId": "COLE_O_UUID_DE_ELETRONICOS_AQUI"
    }
    ```
* **Resultado Esperado:** `201 Created`. Verifique se no JSON aparece `"parentName": "Eletrônicos"`.
* **Ação:** Copie o `categoryId` desta categoria filha (Ex: `uuid-filho-222`).

### 3. Criar Produto Completo (Com SKU e Estoque)
* **Rota:** `POST http://localhost:8080/admin/products`
* **Header:** `Authorization: Bearer <TOKEN_DO_ADMIN>`
* **Body:** (Use o ID da categoria "Celulares")
    ```json
    {
        "name": "iPhone 15 Pro Max",
        "description": "Titânio Natural, 256GB",
        "price": 9500.00,
        "sku": "IP15-PRO-MAX-TIT",
        "costPrice": 7000.00,
        "stockQuantity": 10,
        "categoryId": "COLE_O_UUID_DE_CELULARES_AQUI"
    }
    ```
* **Resultado Esperado:** `201 Created`.

### 4. Testar Validação de SKU Duplicado (Erro Esperado)
* **Ação:** Tente criar um produto diferente usando o **mesmo SKU** do passo anterior (`IP15-PRO-MAX-TIT`).
* **Rota:** `POST http://localhost:8080/admin/products`
* **Resultado Esperado:** `400 Bad Request` com mensagem de erro sobre SKU duplicado.

### 5. Listar Produtos (Verificação Final)
* **Rota:** `GET http://localhost:8080/products`
* **Header:** Use Token de Admin ou User.
* **Resultado Esperado:** `200 OK`. Verifique se o produto aparece com os campos novos (`sku`, `stockQuantity`) e o nome da categoria correto.


### 6. Tentar Deletar Categoria sendo Comum (Bloqueio de Segurança)
* **Ação:** Faça login como `ROLE_USER` (crie um se não tiver) e use o **Token User**.
* **Rota:** `DELETE http://localhost:8080/admin/categorias/1`
* **Header:** `Authorization: Bearer <TOKEN_DO_USER>`
* **Resultado Esperado:** `403 Forbidden`.

### 7. Acessar Rota Pública/Comum (Leitura)
* **Rota:** `GET http://localhost:8080/categorias`
* **Header:** `Authorization: Bearer <TOKEN_DO_USER>`
* **Resultado Esperado:** `200 OK`. (Usuários comuns têm permissão de leitura).

---

## 🧪 Ciclo 5: Validações e Integridade

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