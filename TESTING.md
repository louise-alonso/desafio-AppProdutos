# Estratégia de Testes - AppProdutos

Este documento descreve a abordagem de testes automatizados utilizada no projeto **AppProdutos**. O objetivo é garantir a integridade das regras de negócio, a segurança dos endpoints e a estabilidade do sistema como um todo.

## Tecnologias e Ferramentas

* **JUnit 5:** Framework base para a execução dos testes.
* **Mockito:** Framework de mocking para isolar componentes e simular comportamentos de dependências (Service/Repository).
* **Spring Boot Test:** Suporte integrado para testes de contexto (`@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`).
* **MockMvc:** Ferramenta para simular requisições HTTP e validar respostas de Controllers sem subir um servidor real.
* **H2 Database:** Banco de dados em memória utilizado para testes de repositório, garantindo isolamento e rapidez.

---

## Mapa de Endpoints Testáveis

Abaixo estão listados todos os endpoints da aplicação que são alvo dos testes de integração e manuais.

### 01. Autenticação e Usuários
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/admin/register` | Registro de novos usuários (Admin, Seller, Customer). |
| `POST` | `/auth/login` | Autenticação e geração de Tokens JWT. |
| `POST` | `/auth/refresh` | Renovação do Access Token via Refresh Token. |
| `GET` | `/auth/me` | Retorna dados do usuário logado. |
| `GET` | `/admin/users` | Lista todos os usuários (Admin). |
| `PUT` | `/admin/users/{id}` | Atualiza dados de usuário. |
| `DELETE` | `/admin/users/{id}` | Remove um usuário do sistema. |

### 02. Categorias
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/categories` | Lista todas as categorias. |
| `GET` | `/categories/{id}` | Detalhes de uma categoria específica. |
| `POST` | `/categories` | Criação de nova categoria. |
| `PUT` | `/categories/{id}` | Atualização de categoria. |
| `DELETE` | `/categories/{id}` | Remoção de categoria. |

### 03. Produtos
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/products` | Catálogo público de produtos. |
| `GET` | `/products/{id}` | Detalhes do produto. |
| `POST` | `/admin/products` | Cadastro de produto (Admin/Seller). |
| `PUT` | `/admin/products/{id}` | Atualização de produto (Seller dono ou Admin). |
| `DELETE` | `/admin/products/{id}` | Remoção de produto. |

### 04. Cupons
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/coupons` | Criação de regras de desconto (Percentual/Fixo). |

### 05. Estoque (Inventory)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/inventory/adjust` | Ajuste manual de saldo (Entrada/Saída/Perda). |
| `GET` | `/inventory/product/{id}` | Histórico de movimentações do produto. |

### 06. Carrinho de Compras
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/cart` | Visualiza o carrinho atual do cliente. |
| `POST` | `/cart/add` | Adiciona item ao carrinho (valida estoque). |
| `DELETE` | `/cart/remove/{id}` | Remove item específico. |
| `DELETE` | `/cart/clear` | Esvazia o carrinho completamente. |

### 07. Pedidos (Orders)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/orders` | **Checkout:** Fecha o pedido e baixa estoque. |
| `GET` | `/orders/{id}` | Detalhes do pedido. |
| `GET` | `/orders/latest` | Lista últimos pedidos (Admin). |
| `POST` | `/orders/{id}/cancel` | Cancela pedido e estorna estoque. |
| `PUT` | `/orders/{id}/status` | Atualiza status (ex: SHIPPED, DELIVERED). |

### 08. Avaliações (Reviews)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/reviews` | Cria avaliação (apenas compra verificada). |
| `GET` | `/reviews/product/{id}` | Lista avaliações de um produto. |

### 09. Relatórios e Auditoria
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/reports/sales` | Relatório de vendas por período. |
| `GET` | `/reports/top-products` | Ranking de produtos mais vendidos. |
| `GET` | `/reports/low-stock` | Alerta de estoque baixo. |
| `GET` | `/audit` | Logs de auditoria de alterações no sistema. |

---

## Estrutura dos Testes

Os testes seguem a mesma estrutura de pacotes do código-fonte principal (`src/main/java`), localizados em `src/test/java`:


src/test/java/br/com/louise/AppProdutos
├── controller   # Testes de Integração da Camada Web (REST)
├── service      # Testes Unitários da Lógica de Negócio
└── repository   # Testes de Integração com Banco de Dados

# Tipos de Testes Implementados

## 1. Testes Unitários (Service Layer)

**Foco:** Validar a lógica de negócio isolada.  
**Técnica:** Utilizamos `@ExtendWith(MockitoExtension.class)` para injetar mocks. Nenhuma conexão com banco de dados ou contexto Spring é carregada aqui, tornando os testes extremamente rápidos.

### Cenários Cobertos:
- Cálculo de descontos de cupons (Percentual e Fixo).
- Validação de estoque insuficiente e fluxo de movimentação (Entrada/Saída).
- Regras de checkout (Carrinho vazio, Usuário inexistente).
- Lógica de Média de Avaliações (Reviews).
- Segurança: Validação se o usuário (Seller) é dono do produto.

---

## 2. Testes de Controlador (Controller Layer)

**Foco:** Validar o contrato da API (Status Code, JSON de resposta e Segurança).  
**Técnica:** Utilizamos `@WebMvcTest` para carregar apenas a camada web.  
**Segurança:** Simulamos autenticação/autorização com `@WithMockUser` e mocks do TokenService e UserDetailsService.

### Cenários Cobertos:
- `POST /orders`: Deve retornar **201 Created** para payload válido.
- `POST /reviews`: Deve retornar **400 Bad Request** se faltar o ID do pedido.
- `GET /reports`: Deve retornar **403 Forbidden** se o usuário não for ADMIN.

---

## 3. Testes de Repositório (Data Layer)

**Foco:** Validar queries customizadas (JPQL/Native SQL) e mapeamento de entidades.  
**Técnica:** Utilizamos `@DataJpaTest`, que configura automaticamente um banco **H2 em memória**.

### Cenários Cobertos:
- Relatório de Vendas: Agrupamento por data e soma de valores.
- Histórico de Estoque: Ordenação correta por data de criação (Decrescente).

---

##  Como Executar os Testes

### Via Linha de Comando (Maven)

Rodar toda a suíte:
```bash
mvn test