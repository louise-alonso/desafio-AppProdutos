#  Guia de Testes e Validação - AppProdutos

Este documento descreve o roteiro passo a passo para validar a segurança, autenticação e regras de negócio da API.

> **NOTA:** Estes testes cobrem cenários de **Regressão de Segurança (RBAC)** e **Regras de Negócio Críticas**, devendo ser executados a cada nova versão.

**Pré-requisitos:**
- Aplicação rodando (`mvn spring-boot:run`)
- Postman, Insomnia ou outro API Client
## Ciclo 1: Configuração Inicial (Bootstrap)

Como o banco H2 é volátil (reinicia vazio), o primeiro passo é criar os usuários via API, já que o endpoint de registro foi configurado como **público**.

### 1. Criar Usuários (Admin, Seller e Customer)

**1.1 Registrar Admin**
* **Rota:** `POST /admin/register`
* **Body:** `{"name": "Admin Master", "email": "admin@email.com", "password": "123", "role": "ADMIN"}`
* **Esperado:** `201 Created`

**1.2 Registrar Vendedor**
* **Rota:** `POST /admin/register`
* **Body:** `{"name": "Joao Vendedor", "email": "joao@seller.com", "password": "123", "role": "SELLER"}`
* **Esperado:** `201 Created`

**1.3 Registrar Cliente**
* **Rota:** `POST /admin/register`
* **Body:** `{"name": "Maria Cliente", "email": "maria@customer.com", "password": "123", "role": "CUSTOMER"}`
* **Esperado:** `201 Created`
---

### 2. Autenticação (Login e Tokens)

Agora que os usuários existem, obtenha as credenciais de acesso.

**2.1 Login (Obter Tokens)**
* **Rota:** `POST /auth/login`
* **Body:** `{"email": "admin@email.com", "password": "123"}`
* **Resposta:**
    ```json
    {
        "accessToken": "eyJhbGciOiJIUz...",  // <-- Copie este para usar nas requisições
        "refreshToken": "uuid-b839-..."      // <-- Guarde este para testar renovação
    }
    ```

> **DICA:** Repita o processo de login para o **Vendedor** e o **Cliente** e guarde os tokens de cada um para os testes de permissão abaixo.
---

## Ciclo 2: Gestão de Usuários e Segurança (RBAC)

Teste se as permissões de acesso às rotas administrativas estão funcionando.

**Token Ativo:** Use o `accessToken` do **ADMIN** (salvo no passo anterior), exceto onde indicado.

| Ação | Rota | Token Usado | Cenário | Esperado |
|------|------|-------------|----------|-----------|
| **Listar Usuários** | `GET /admin/users` | **ADMIN** | Ver lista completa | **200 OK** |
| **Bloqueio de Listagem** | `GET /admin/users` | **SELLER** | Vendedor tenta ver lista | **403 Forbidden** |
| **Atualizar Usuário** | `PUT /admin/users/{id}` | **ADMIN** | Alterar nome de um usuário | **200 OK** |
| **Erro de Validação** | `PUT /admin/users/{id}` | **ADMIN** | Tentar usar e-mail já existente | **400 Bad Request** |
| **Deletar Usuário** | `DELETE /admin/users/{id}` | **ADMIN** | Remover um usuário | **204 No Content** |
| **Renovar Token** | `POST /auth/refresh` | *(Público)* | Body: `{"refreshToken": "uuid..."}` | **200 OK** (Novo Token) |
---

##  Ciclo 3: Testes de Categoria (Integridade e Estrutura)

**Token necessário: ADMIN**

| Ação | Rota | Cenário | Esperado | Regra |
|------|------|----------|-----------|--------|
| Criar categoria | POST /admin/categories | Criar raiz "Eletrônicos" | 201 | Nó raiz válido |
| Atualizar | PUT /admin/categories/{id} | Alterar nome/descrição | 200 | PUT funcionando |
| Bloqueio hierarquia | POST /admin/categories | Enviar `parentId` | 400 | Catálogo plano |
| Bloqueio delete | DELETE /admin/categories/{id} | Categoria com produtos | 400 | Integridade FK |
| Self-parenting | PUT /admin/categories/{id} | Categoria apontando para si mesma | 400 | Proibir loops |

---

## Ciclo 4: Testes de Produto e Propriedade

**Setup:**
- Seller 1 cria produto (P1).
- Seller 2 tenta operações restritas.

| Ação | Rota | Token | Cenário | Esperado | Regra |
|------|------|--------|----------|-----------|---------|
| Criar produto | POST /admin/products | SELLER 1 | Criar P1 | 201 | Seller pode criar |
| SKU duplicado | POST /admin/products | ADMIN | SKU já usado | 400 | SKU único |
| Atualizar (negativo) | PUT /admin/products/{id} | SELLER 2 | Alterar P1 | 403 | Owner lock |
| Atualizar (positivo) | PUT /admin/products/{id} | SELLER 1 | Alterar preço | 200 | Dono pode editar |
| Deletar | DELETE /admin/products/{id} | ADMIN | Remover P1 | 204 | Admin pode tudo |

---

## Ciclo 5: Testes do Perfil CUSTOMER

**Token necessário: CUSTOMER**

| Ação | Rota | Esperado | Regra |
|------|------|-----------|--------|
| Ler catálogo | GET /products | 200 | Leitura liberada |
| Bloqueio CRUD | POST /admin/products | 403 | Customer não cria |
| Bloqueio admin | GET /admin/users | 403 | Sem acesso a /admin |

---

## Ciclo 6: Testes Finais de Segurança

| Ação | Cenário | Esperado |
|------|----------|-----------|
| Token adulterado | Token inválido | 403 Forbidden |
| Login inválido | Senha errada | 400 Bad Request |
