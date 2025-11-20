# 🛒 AppProdutos - E-commerce API (Evolution Project)

Este projeto é a evolução de uma API REST de produtos, transformando-a em um sistema de E-commerce completo. O objetivo é aplicar conceitos avançados de **Java 21**, **Spring Boot 3**, **Segurança (JWT)** e **Regras de Negócio Complexas**.

Projeto desenvolvido com base nos requisitos de evolução propostos no desafio acadêmico.

---

## 🚀 Status do Desenvolvimento

### ✅ Funcionalidades Já Implementadas (Done)

**1. Módulo de Autenticação & Segurança (Nível: Intermediário)**
* [x] Implementação de **Spring Security** com configuração stateless.
* [x] Autenticação via **Token JWT** (Geração e Validação via Filtro customizado).
* [x] Criptografia de senhas com **BCrypt**.
* [x] Controle de Acesso Baseado em Funções (**RBAC**) para rotas de `ADMIN` e `USER`.
* [x] Correção de vulnerabilidades (CSRF desabilitado para API, proteção de endpoints sensíveis).
* [x] Acesso seguro ao **H2 Console** em ambiente de desenvolvimento.

**2. Módulo de Usuários**
* [x] Cadastro de usuários com validação de e-mail único.
* [x] Listagem e exclusão de usuários (Exclusivo Admin).
* [x] Endpoint utilitário para hash de senhas (`/encode`).

**3. Módulo de Categorias (Completo)**
* [x] CRUD de Categorias.
* [x] Validação de unicidade de nome.
* [x] **Hierarquia de Categorias:** Implementação de auto-relacionamento (Categoria Pai/Filho) permitindo subcategorias.

**4. Módulo de Produtos (Catálogo)**
* [x] CRUD de Produtos.
* [x] **Relacionamento:** Associação de Produto com Categoria (`@ManyToOne`).
* [x] **Novos Campos:** Implementação de `sku` (código único), `costPrice`, `stockQuantity` e `active`.
* [x] **Validações:** Bloqueio de cadastro de produtos com SKU duplicado.

---

## 📋 Roadmap de Evolução (Próximos Passos)

Baseado nos requisitos do desafio, estas são as próximas implementações priorizadas:

### 🚧 Prioridade 1: Estoque e Transações
* [ ] **Transações de Inventário:** Criar entidade `InventoryTransaction` para registrar histórico de entradas, saídas, ajustes e devoluções.
* [ ] **Regra de Negócio:** Impedir vendas com estoque insuficiente.
* [ ] Atualizar quantidade automaticamente ao confirmar pedido.

### 🚧 Prioridade 2: Fluxo de Vendas
* [ ] **Carrinho de Compras:** Implementar carrinho persistente por usuário (Redis ou Banco) com `priceSnapshot`.
* [ ] **Pedidos (Orders) e avaliações:** Fluxo de checkout (Carrinho -> Pedido) com status (`CREATED`, `PAID`, `SHIPPED`, `DELIVERED`, `CANCELLED`).

### 🔮 Futuro (Bônus)
* [ ] **Promoções e Cupons:** Lógica de desconto percentual/fixo e validade.
* [ ] **Auditoria:** Logar quem alterou o quê (Entity Listeners).
* [ ] **Relatórios:** Endpoints para métricas de vendas e estoque baixo.

---

## 🛠️ Tecnologias e Arquitetura

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.5.7
* **Banco de Dados:** H2 Database (Em memória)
* **Segurança:** Spring Security + JJWT (0.9.1 Legacy adapter)
* **Documentação:** SpringDoc OpenAPI (Swagger)
* **Arquitetura:** Camadas (Controller -> Service -> Repository) com uso de DTOs (Data Transfer Objects).

---

## ✅ Critérios de Aceite

* [x] Endpoints sensíveis protegidos com roles.
* [x] Código organizado (Controller, Service, Repository, DTO).
* [x] Validações retornam mensagens claras (Ex: 400 Bad Request para e-mail ou SKU duplicados).
* [ ] Testes unitários cobrindo regras principais (Pendente).
* [ ] Tabelas criadas com migrations (Usando H2 auto-ddl por enquanto).
* [ ] Configuração do Swagger: Habilitar e configurar o `springdoc-openapi` para documentação automática visual (`/swagger-ui.html`)(Pendente).

---

## 🔌 Endpoints da API

### 🔓 Público / Utilitários
| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/login` | Autentica o usuário e retorna o Token JWT. |
| `POST` | `/encode` | Utilitário para gerar hash de senha (para testes). |
| `GET` | `/h2-console` | Acesso ao banco de dados (Requer navegador). |

### 👤 Usuário (ROLE_USER & ADMIN)
| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `GET` | `/categorias` | Lista todas as categorias (com indicação de hierarquia). |
| `GET` | `/products` | Lista todos os produtos do catálogo. |

### 🛡️ Administrativo (Apenas ROLE_ADMIN)
| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/admin/register` | Cria um novo usuário (Admin ou User). |
| `GET` | `/admin/users` | Lista todos os usuários do sistema. |
| `DELETE` | `/admin/users/{id}` | Remove um usuário. |
| `POST` | `/admin/categorias` | Cria uma nova categoria (suporta `parentId`). |
| `DELETE` | `/admin/categorias/{id}`| Remove uma categoria. |
| `POST` | `/admin/products` | Cadastra um novo produto (com SKU e Estoque). |
| `DELETE` | `/admin/products/{id}`| Remove um produto. |

---

## ▶️ Como Rodar

1.  Clone o repositório.
2.  Execute o comando Maven para baixar as dependências:
    ```bash
    mvn clean install
    ```
3.  Rode a aplicação:
    ```bash
    mvn spring-boot:run
    ```
4.  **Configuração Inicial (H2):**
    * Como o banco é em memória, ao iniciar, crie o primeiro ADMIN via SQL no `/h2-console` ou use o endpoint de setup (se implementado).
    * Url JDBC: `jdbc:h2:mem:produtosdb`
    * User: `sa`
    * Password: (vazia)

---

## 🧪 Testes Realizados

O sistema passou por cenários de teste manuais rigorosos via Postman:

1.  ✅ **Autenticação:** Login com credenciais válidas gera Token JWT corretamente.
2.  ✅ **Autorização (Sucesso):** Admin consegue acessar rotas protegidas e criar/deletar registros.
3.  ✅ **Autorização (Falha/Segurança):** Usuário comum (`ROLE_USER`) recebe **403 Forbidden** ao tentar deletar ou criar usuários/produtos.
4.  ✅ **Integridade:** Bloqueio de tokens adulterados ou falsos (403).
5.  ✅ **Validação de Dados:**
    * Bloqueio de cadastro de e-mails duplicados (400 Bad Request).
    * Bloqueio de cadastro de **SKU duplicado** em produtos (400 Bad Request).
6.  ✅ **Relacionamentos:** Criação bem sucedida de Categorias Pai/Filho e Produtos associados a Categorias.

---

## 🧪 Como Testar

Para um guia detalhado de como testar todos os cenários de segurança (Login, Bloqueio de Permissões, Criação de Usuários, etc.) utilizando o Postman e o H2 Console, consulte o manual de testes dedicado:

👉 **[Clique aqui para ver o Guia de Testes (TESTING.md)](./TESTING.md)**