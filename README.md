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
| **Segurança** | Spring Security + JWT | 3.x / 0.9.1 | Controle de acesso via `@PreAuthorize`. |
| **Persistência** | Spring Data JPA | N/A | Abstração da camada de dados. |
| **Banco de Dados** | H2 Database | N/A | Banco em memória para ambiente de desenvolvimento. |

---

## Status do Desenvolvimento (Core Business Logic)

### Funcionalidades Implementadas

| Módulo | Funcionalidade Chave | Regras de Negócio |
| :--- | :--- | :--- |
| **Segurança & RBAC** | Implementação **Stateless** com JWT e BCrypt. | **Três Perfis:** `ADMIN`, `SELLER`, `CUSTOMER`. Controle de acesso por propriedade (`isOwner`). |
| **Módulo Usuários** | CRUD Completo de Usuários (`/admin/users`). | Validação de e-mail único. |
| **Módulo Categorias** | CRUD Completo de Categorias. | **Hierarquia Plana Crítica:** Proibição de associação Pai/Filho entre categorias. |
| **Módulo Produtos** | CRUD Completo de Produtos (`/admin/products`). | Bloqueio de **SKU Duplicado**. Produto obrigatoriamente associado a uma Categoria e um Proprietário. |

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

* **Console do Banco:** Acesse `http://localhost:8080/h2-console`
    * URL JDBC: `jdbc:h2:mem:produtosdb`
    * User: `sa` / Password: (vazia)
* **Primeiro Admin:** Use o endpoint `/auth/encode` para gerar o hash de `senhaadmin` e insira o usuário `admin@email.com` no H2 Console.

---

##  Endpoints Principais da API

###  Módulo de Autenticação e Catálogo

| Método | Rota | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Público | Autenticação e emissão do Token JWT. |
| `GET` | `/products` | Público | Lista todos os produtos (Catálogo). |
| `GET` | `/categories` | Público | Lista todas as categorias. |
| `GET` | `/products/{id}` | Público | Detalhes de um produto por ID. |

###  Módulo de Gestão (Rotas Protegidas)

| Recurso | Método | Rota | Permissões |
| :--- | :--- | :--- | :--- |
| **Usuário** | `POST` | `/admin/register` | `ROLE_ADMIN` |
| **Produto** | `POST` | `/admin/products` | `ROLE_ADMIN`, `ROLE_SELLER` |
| **Produto** | `PUT`/`DELETE` | `/admin/products/{id}` | `ROLE_ADMIN` **OU** Dono do Produto |
| **Categoria** | `POST`/`PUT`/`DELETE` | `/admin/categories` | `ROLE_ADMIN` |

---

##  Testes e Qualidade

Todos os endpoints e regras de segurança foram validados através de testes manuais. Detalhes completos sobre os cenários de teste (incluindo falhas de propriedade e violações de SKU) estão disponíveis no **Guia de Testes**.

 **[Guia de Testes e Validação (TESTING.md)](./TESTING.md)**
 **[Guia de Referência da API (API.md)](./API.md)**
