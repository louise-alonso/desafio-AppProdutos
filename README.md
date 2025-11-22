# AppProdutos - E-commerce API (Evolution Project)

Este projeto é a evolução de uma API REST de produtos, transformando-a em um sistema de E-commerce completo. O foco principal é a aplicação de conceitos avançados de **Java 21**, **Spring Boot 3**, **Segurança (JWT) com RBAC granular** e **Regras de Negócio Complexas**.

---

## Visão Geral e Arquitetura

O **AppProdutos** utiliza uma arquitetura em camadas e segue rigorosamente os princípios RESTful, com validação de dados e controle de acesso em nível de método.

### Tecnologias Principais

| Categoria | Tecnologia | Versão | Notas |
| :--- | :--- | :--- | :--- |
| **Linguagem** | Java | 21 | Foco em features modernas da linguagem. |
| **Framework** | Spring Boot | 3.5.7 | Utilizando as especificações Jakarta EE. |
| **Segurança** | Spring Security + **Auth0 JWT** | 6.x / 4.4.0 | Autenticação via **Access Token** e **Refresh Token**. |
| **Persistência** | Spring Data JPA | N/A | Abstração da camada de dados. |
| **Banco de Dados** | H2 Database | N/A | Banco em memória para ambiente de desenvolvimento. |

---

## Como Configurar e Rodar

### 1. Configuração Local

1.  Clone o repositório.
2.  Execute o comando Maven para baixar as dependências:
    ```bash
    mvn clean install
    ```
3.  Rode a aplicação:
    ```bash
    mvn spring-boot:run
    ```

### 2. Acesso e Credenciais Iniciais

O banco H2 é volátil (reinicia vazio). Siga este fluxo para o primeiro acesso:

1.  **Console do Banco:** `http://localhost:8080/h2-console`
    * URL JDBC: `jdbc:h2:mem:produtosdb`
    * User: `sa` / Password: (vazia)
2.  **Criar o Primeiro Admin (Via API):**
    * Faça um `POST` em `/admin/register` com os dados do usuário e role `ADMIN`.
3.  **Login:**
    * Faça um `POST` em `/auth/login` para receber o par de chaves (`accessToken` e `refreshToken`).
    
---

## Status do Desenvolvimento (Core Business Logic)

### Funcionalidades Implementadas

| Módulo | Funcionalidade Chave | Regras de Negócio |
| :--- | :--- | :--- |
| **Segurança & RBAC** | Autenticação **Dual-Token** (Access + Refresh). | **Access Token:** Validade curta (1h). **Refresh Token:** Validade longa (30 dias), persistido no banco e revogável. |
| **Módulo Usuários** | Cadastro Público e Gestão de Usuários. | Validação de e-mail/login via Regex. O endpoint de registro é público para facilitar o setup inicial ("Ovo e a Galinha"). |
| **Módulo Categorias** | CRUD Completo de Categorias. | **Hierarquia Plana Crítica:** Proibição de associação Pai/Filho entre categorias. |
| **Módulo Produtos** | CRUD Completo de Produtos (`/admin/products`). | Bloqueio de **SKU Duplicado**. Produto obrigatoriamente associado a uma Categoria e um Proprietário (Seller). |

### Perfis de Usuário e Permissões

| Perfil | Role | Permissão Chave |
| :--- | :--- | :--- |
| **Administrador** | `ROLE_ADMIN` | Acesso total (CRUD) a todos os módulos (Usuário, Categoria, Produto, etc.). |
| **Vendedor** | `ROLE_SELLER` | Acesso para Criar, Atualizar e Deletar **APENAS seus produtos** (Regra de Dono). |
| **Cliente** | `ROLE_CUSTOMER` | Acesso somente a rotas de Leitura (Catálogo). |

---

## Roadmap de Evolução (Próximos Passos)

O foco agora é na implementação dos módulos transacionais (Vendas e Inventário).

### Prioridade 1: Módulo de Transações e Estoque
* [ ] Implementação da entidade **`InventoryTransaction`** (Histórico de Movimentação).
* [ ] Regra: Impedir vendas com estoque insuficiente.
* [ ] Atualização automática do `stockQuantity` após movimentação.

### Prioridade 2: Módulo de Vendas e Pedidos
* [ ] Implementação de **Carrinho de Compras** persistente por usuário.
* [ ] Fluxo completo de **Pedidos (Orders)** com gestão de status (`PAID`, `SHIPPED`, etc.).

---

## Endpoints Principais da API

### Módulo de Autenticação e Usuários

| Método | Rota | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/admin/register` | **Público** | Criação de novos usuários (Admin, Seller, Customer). |
| `POST` | `/auth/login` | **Público** | Login. Retorna `accessToken` (1h) e `refreshToken` (30d). |
| `POST` | `/auth/refresh` | **Público** | Envia um `refreshToken` válido para receber um novo `accessToken`. |
| `GET` | `/auth/me` | Autenticado | Retorna detalhes do usuário logado (para validação rápida). |

### Módulo de Produtos e Categorias

| Método | Rota | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/products` | Público | Lista todos os produtos (Catálogo). |
| `GET` | `/categories` | Público | Lista todas as categorias. |
| `GET` | `/products/{id}` | Público | Detalhes de um produto por ID. |

### Módulo de Gestão (Rotas Protegidas)

| Recurso | Método | Rota | Permissões |
| :--- | :--- | :--- | :--- |
| **Produto** | `POST` | `/admin/products` | `ROLE_ADMIN`, `ROLE_SELLER` |
| **Produto** | `PUT`/`DELETE` | `/admin/products/{id}` | `ROLE_ADMIN` **OU** Dono do Produto |
| **Categoria** | `POST`/`PUT`/`DELETE` | `/admin/categories` | `ROLE_ADMIN` |

---

##  Guias

 **[Guia de Testes e Validação (TESTING.md)](./TESTING.md)**
 
 **[Guia de Referência da API (API.md)](./API.md)**
