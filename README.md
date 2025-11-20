# 🛒 AppProdutos - API de E-commerce Segura

Este projeto é uma evolução de uma API REST básica de produtos. O objetivo principal é transformar um CRUD simples em uma aplicação de E-commerce robusta, implementando segurança avançada, regras de negócio complexas e boas práticas de arquitetura com Spring Boot.

---

## 🎯 O Desafio

O projeto original continha apenas um cadastro simples de produtos. O desafio consiste em escalar esta aplicação para suportar um fluxo real de vendas, focado em:
1.  **Segurança:** Implementar autenticação e autorização do zero (JWT).
2.  **Regras de Negócio:** Gestão de estoque, categorias e pedidos.
3.  **Arquitetura:** Uso de DTOs, Services e tratamento de exceções.
4.  **Testes:** Garantir a integridade dos dados e segurança dos endpoints.

---

## 🚀 Status do Projeto

### ✅ Funcionalidades Implementadas

**1. Segurança e Autenticação (Spring Security + JWT)**
* [x] Configuração do Spring Security (`SecurityConfig`) com proteção CSRF desabilitada para API.
* [x] Autenticação Stateless via **Token JWT**.
* [x] Criptografia de senhas com **BCrypt**.
* [x] Controle de Acesso Baseado em Funções (**RBAC**):
    * `ROLE_ADMIN`: Acesso total (criar/deletar usuários, categorias, produtos).
    * `ROLE_USER`: Acesso de leitura (visualizar catálogo).
* [x] Filtro de requisição customizado (`JwtRequestFilter`) para validação de token.

**2. Gestão de Usuários**
* [x] Cadastro de novos usuários (Endpoint protegido para Admin).
* [x] Listagem de usuários.
* [x] Exclusão de usuários.
* [x] Validação de e-mail único no banco de dados.

**3. Gestão de Categorias**
* [x] Criar Categoria (Admin).
* [x] Listar Categorias (Público/User).
* [x] Deletar Categoria (Admin).

**4. Banco de Dados**
* [x] Configuração do **H2 Database** (em memória) para desenvolvimento rápido.
* [x] Modelagem das tabelas `tbl_users` e `tbl_categorias`.

---

### 📝 Próximos Passos (To-Do)

O roadmap para finalizar a aplicação inclui:

* [ ] **Produtos:** Associar Produtos a Categorias (Relacionamento `@ManyToOne`).
* [ ] **Estoque:** Implementar controle de baixa de estoque e transações.
* [ ] **Carrinho de Compras:** Permitir que o usuário adicione itens a um carrinho temporário.
* [ ] **Pedidos (Orders):** Finalizar a compra e gerar um registro de pedido.
* [ ] **Auditoria:** Registrar quem alterou o quê (Logs).

---

## 🛠️ Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3.x**
    * Spring Web
    * Spring Data JPA
    * Spring Security
* **H2 Database** (Banco em memória)
* **JWT (JSON Web Token)** - Biblioteca `jjwt`
* **Lombok**
* **Maven**

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
| `GET` | `/categorias` | Lista todas as categorias disponíveis. |
| `GET` | `/produtos` | (Em breve) Listar produtos. |

### 🛡️ Administrativo (Apenas ROLE_ADMIN)
| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/admin/register` | Cria um novo usuário (Admin ou User). |
| `GET` | `/admin/users` | Lista todos os usuários do sistema. |
| `DELETE` | `/admin/users/{id}` | Remove um usuário. |
| `POST` | `/admin/categorias` | Cria uma nova categoria. |
| `DELETE` | `/admin/categorias/{id}`| Remove uma categoria. |

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
3.  ✅ **Autorização (Falha/Segurança):** Usuário comum (`ROLE_USER`) recebe **403 Forbidden** ao tentar deletar ou criar usuários (Validado).
4.  ✅ **Tratamento de Erro:** Tentativa de deletar registro inexistente retorna **404 Not Found** corretamente.
5.  ✅ **Integridade:** Bloqueio de tokens adulterados ou falsos (403).
6.  ✅ **Validação:** Bloqueio de cadastro de e-mails duplicados (400 Bad Request).

---

## 🧪 Como Testar

Para um guia detalhado de como testar todos os cenários de segurança (Login, Bloqueio de Permissões, Criação de Usuários, etc.) utilizando o Postman e o H2 Console, consulte o meu manual de testes dedicado:

👉 **[Clique aqui para ver o Guia de Testes (TESTING.md)](./TESTING.md)**