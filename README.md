# AppProdutos - API de E-commerce Backend

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green)
![Security](https://img.shields.io/badge/JWT-Auth-blue)
![Swagger](https://img.shields.io/badge/Swagger-UI-brightgreen)

API RESTful completa para gerenciamento de um sistema de E-commerce, incluindo controle de estoque, fluxo de pedidos, sistema de cupons, avaliações e relatórios gerenciais.

## Funcionalidades Principais

* **Autenticação e Segurança:** Login via JWT (Access e Refresh Token), Criptografia de senhas (BCrypt) e Controle de Acesso baseado em Roles (ADMIN, SELLER, CUSTOMER).
* **Gestão de Produtos:** CRUD completo, Hierarquia de Categorias e upload de imagens (placeholder).
* **Controle de Estoque:** Auditoria de movimentações (Entrada/Saída/Ajuste) e prevenção de venda sem estoque.
* **Carrinho de Compras:** Gestão de itens, cálculo de subtotal e limpeza automática pós-venda.
* **Fluxo de Pedidos:** Checkout, Integração simulada com gateways (Razorpay), Cancelamento com estorno de estoque.
* **Cupons de Desconto:** Validação complexa (validade, limite global, limite por usuário, valor mínimo, categorias específicas).
* **Avaliações (Reviews):** Apenas usuários que compraram e receberam o produto podem avaliar (Verified Purchase).
* **Auditoria e Relatórios:** Logs de alterações em entidades e relatórios de vendas/produtos mais vendidos.

## Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3** (Web, Data JPA, Security, Validation)
* **Banco de Dados:** H2 Database (Memória/Dev)
* **Migrations:** Flyway
* **Documentação:** OpenAPI / Swagger UI
* **Utils:** Lombok, JWT (Auth0)
* **Testes:** JUnit 5, Mockito

##  Como Rodar o Projeto

### Pré-requisitos
* Java JDK 21 instalado
* Maven instalado

### Passos
1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/seu-usuario/AppProdutos.git](https://github.com/seu-usuario/AppProdutos.git)
    cd AppProdutos
    ```

2.  **Execute a aplicação:**
    ```bash
    mvn spring-boot:run
    ```

3.  **Acesse a Documentação (Swagger):**
    Abra o navegador em: `http://localhost:8080/swagger-ui.html`

4.  **Acesse o Banco de Dados (H2 Console):**
    * URL: `http://localhost:8080/h2-console`
    * JDBC URL: `jdbc:h2:mem:produtosdb`
    * User: `sa`
    * Password: (deixe em branco)

## Como Testar (Fluxo Básico)

1.  **Criar Usuário Admin:**
    * POST `/admin/register`
    * Body: `{"email": "admin@test.com", "password": "123", "role": "ADMIN", "name": "Admin"}`

2.  **Login:**
    * POST `/auth/login`
    * Copie o `accessToken` da resposta.

3.  **Autorizar no Swagger:**
    * Clique no botão **Authorize** (cadeado) no topo do Swagger.
    * Cole o token. Agora você pode acessar rotas protegidas.

4.  **Criar Categoria e Produto:**
    * POST `/categories`
    * POST `/admin/products`

## Documentação da API

Principais Endpoints:

| Módulo | Método | Endpoint | Descrição |
| :--- | :--- | :--- | :--- |
| **Auth** | POST | `/auth/login` | Realiza login e retorna tokens |
| **Auth** | POST | `/auth/refresh` | Renova o token de acesso |
| **Users** | POST | `/admin/register` | Cria novo usuário |
| **Products** | GET | `/products` | Lista produtos (Público) |
| **Cart** | POST | `/cart/add` | Adiciona item ao carrinho |
| **Orders** | POST | `/orders` | Finaliza compra (Checkout) |
| **Inventory**| POST | `/inventory/adjust`| Ajuste manual de estoque |
| **Reports** | GET | `/reports/sales` | Relatório de vendas (Admin) |

## Estrutura do Banco de Dados

O projeto utiliza **Flyway** para versionamento. O Schema inicial inclui tabelas para:
* `tbl_users`, `tbl_refresh_tokens`
* `tbl_products`, `tbl_categories`
* `tbl_inventory_transactions`, `tbl_audit_logs`
* `tbl_orders`, `tbl_order_items`, `tbl_coupons`
* `tbl_reviews`, `tbl_carts`

---

## Guias

**[Guia de Testes e Validação (TESTING.md)](./TESTING.md)**  
**[Guia de Referência da API (API.md)](./API.md)**