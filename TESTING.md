# Estratégia de Testes - AppProdutos

Este documento descreve a abordagem completa de testes automatizados utilizada no projeto **AppProdutos**, garantindo a integridade das regras de negócio, segurança dos endpoints e estabilidade do sistema.

## Tecnologias e Ferramentas

- **JUnit 5**: Framework base para execução de testes
- **Mockito**: Framework de mocking para isolar componentes
- **Spring Boot Test**: Suporte integrado para testes de contexto (`@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`)
- **MockMvc**: Ferramenta para simular requisições HTTP
- **H2 Database**: Banco em memória para testes de repositório
- **Redis**: Cache em memória com Docker
- **Mailtrap**: Teste de notificações por e-mail
- **Swagger/OpenAPI 3**: Documentação interativa

## Mapa de Endpoints Testáveis

### 01. Autenticação e Usuários
| Método | Endpoint | Descrição | Permissão |
|--------|-----------|-------------|------------|
| `POST` | `/admin/register` | Registro de usuários (Admin, Seller, Customer) | ADMIN |
| `POST` | `/auth/login` | Autenticação e geração de Tokens JWT | Público |
| `POST` | `/auth/refresh` | Renovação do Access Token | Autenticado |
| `GET` | `/auth/me` | Dados do usuário logado | Autenticado |
| `GET` | `/admin/users` | Lista todos os usuários | ADMIN |
| `PUT` | `/admin/users/{id}` | Atualiza dados de usuário | ADMIN |
| `DELETE` | `/admin/users/{id}` | Remove usuário do sistema | ADMIN |

### 02. Categorias
| Método | Endpoint | Descrição | Permissão |
|--------|-----------|-------------|------------|
| `GET` | `/categories` | Lista todas as categorias | Público |
| `GET` | `/categories/{id}` | Detalhes de categoria específica | Público |
| `POST` | `/categories` | Criação de nova categoria | ADMIN |
| `PUT` | `/categories/{id}` | Atualização de categoria | ADMIN |
| `DELETE` | `/categories/{id}` | Remoção de categoria | ADMIN |

### 03. Produtos
| Método | Endpoint | Descrição | Permissão |
|--------|-----------|-------------|------------|
| `GET` | `/products` | Catálogo público de produtos | Público |
| `GET` | `/products/{id}` | Detalhes do produto | Público |
| `POST` | `/admin/products` | Cadastro de produto | ADMIN/SELLER |
| `PUT` | `/admin/products/{id}` | Atualização de produto | Seller dono ou ADMIN |
| `DELETE` | `/admin/products/{id}` | Remoção de produto | ADMIN/SELLER |

### 04. Cupons
| Método | Endpoint | Descrição | Permissão |
|--------|-----------|-------------|------------|
| `POST` | `/coupons` | Criação de regras de desconto | ADMIN |

### 05. Estoque (Inventory)
| Método | Endpoint | Descrição | Permissão |
|--------|-----------|-------------|------------|
| `POST` | `/inventory/adjust` | Ajuste manual de saldo | ADMIN |
| `GET` | `/inventory/product/{id}` | Histórico de movimentações | ADMIN |

### 06. Carrinho de Compras
| Método | Endpoint | Descrição | Permissão |
|--------|-----------|-------------|------------|
| `GET` | `/cart` | Visualiza carrinho atual | Customer |
| `POST` | `/cart/add` | Adiciona item ao carrinho | Customer |
| `DELETE` | `/cart/remove/{id}` | Remove item específico | Customer |
| `DELETE` | `/cart/clear` | Esvazia carrinho | Customer |

### 07. Pedidos (Orders)
| Método | Endpoint | Descrição | Permissão |
|--------|-----------|-------------|------------|
| `POST` | `/orders` | Checkout: fecha pedido e baixa estoque | Customer |
| `GET` | `/orders/{id}` | Detalhes do pedido | Owner/ADMIN |
| `GET` | `/orders/latest` | Lista últimos pedidos | ADMIN |
| `POST` | `/orders/{id}/cancel` | Cancela pedido e estorna estoque | Owner/ADMIN |
| `PUT` | `/orders/{id}/status` | Atualiza status (SHIPPED, DELIVERED) | ADMIN |

### 08. Avaliações (Reviews)
| Método | Endpoint | Descrição | Permissão |
|--------|-----------|-------------|------------|
| `POST` | `/reviews` | Cria avaliação (compra verificada) | Customer |
| `GET` | `/reviews/product/{id}` | Lista avaliações de produto | Público |

### 09. Relatórios e Auditoria
| Método | Endpoint | Descrição | Permissão |
|--------|-----------|-------------|------------|
| `GET` | `/reports/sales` | Relatório de vendas por período | ADMIN |
| `GET` | `/reports/top-products` | Ranking de produtos mais vendidos | ADMIN |
| `GET` | `/reports/low-stock` | Alerta de estoque baixo | ADMIN |
| `GET` | `/audit` | Logs de auditoria de alterações | ADMIN |

## Estrutura dos Testes

```text
src/test/java/br/com/louise/AppProdutos
├── controller   # Testes de Integração da Camada Web (REST)
├── service      # Testes Unitários da Lógica de Negócio
└── repository   # Testes de Integração com Banco de Dados
```

## Tipos de Testes Implementados

### 1. Testes Unitários (Service Layer)
**Foco**: Validar lógica de negócio isolada  
**Técnica**: `@ExtendWith(MockitoExtension.class)` - sem contexto Spring

**Cenários Cobertos**:
- Cálculo de descontos de cupons (Percentual e Fixo)
- Validação de estoque insuficiente e fluxo de movimentação
- Regras de checkout (Carrinho vazio, Usuário inexistente)
- Lógica de Média de Avaliações (Reviews)
- Segurança: Validação se usuário (Seller) é dono do produto
- Integridade: Impedir deleção de Categoria com dependências

### 2. Testes de Controlador (Controller Layer)
**Foco**: Validar contrato da API (Status Code, JSON, Segurança)  
**Técnica**: `@WebMvcTest` com `@WithMockUser`

**Cenários Cobertos**:
- `POST /orders`: Retorna **201 Created** para payload válido
- `POST /reviews`: Retorna **400 Bad Request** sem ID do pedido
- `GET /reports`: Retorna **403 Forbidden** para usuário não ADMIN

### 3. Testes de Repositório (Data Layer)
**Foco**: Validar queries customizadas e mapeamento de entidades  
**Técnica**: `@DataJpaTest` com H2 em memória

**Cenários Cobertos**:
- Relatório de Vendas: Agrupamento por data e soma de valores
- Histórico de Estoque: Ordenação por data de criação (Decrescente)

## Como Executar os Testes

### Via Linha de Comando (Maven)

```bash
# Rodar toda a suíte
mvn test

# Apenas testes unitários
mvn test -Dtest=*ServiceTest

# Apenas testes de integração
mvn test -Dtest=*ControllerTest

# Testes de pacote específico
mvn test -Dtest="br.com.louise.AppProdutos.service.*"
```

### Via IDE (IntelliJ/Eclipse)
- Clique direito em `src/test/java` → "Run All Tests"
- Execute classes individuais com ▶️

## Testando Notificações e Emails

**Configuração Mailtrap**:
1. Crie conta em https://mailtrap.io
2. Configure credenciais SMTP no `application.properties`
3. Execute ações que disparam e-mails:
    - Alterar status de pedido para `DELIVERED`
    - Alertas de estoque baixo
4. Verifique a caixa do Mailtrap para validar formatação

## Infraestrutura com Docker

```yaml
# docker-compose.yml
services:
  redis:
    image: redis:alpine
    ports:
      - "6379:6379"
  
  redis-commander:
    image: rediscommander/redis-commander:latest
    ports:
      - "8081:8081"
    environment:
      - REDIS_HOSTS=local:redis:6379
```

**Verificar Cache Redis**:
```bash
# Primeira requisição (Cache MISS - log SQL)
GET /products

# Segunda requisição (Cache HIT - sem SQL, resposta rápida)
GET /products
```

## Documentação e Suporte

**Swagger UI**: `http://localhost:8080/swagger-ui.html`
- Documentação interativa completa
- Testar endpoints diretamente na UI
- Exemplos de requests/responses
- Esquemas de todos os DTOs

**Estrutura da API**:
- `/auth/**` - Autenticação JWT
- `/admin/**` - Gestão de usuários (ADMIN only)
- `/products/**` - Catálogo de produtos
- `/categories/**` - Gestão de categorias
- `/cart/**` - Carrinho de compras
- `/orders/**` - Pedidos e checkout
- `/reviews/**` - Avaliações de produtos
- `/inventory/**` - Controle de estoque
- `/reports/**` - Relatórios (ADMIN only)
- `/audit/**` - Logs de auditoria (ADMIN only)

## Estrutura do Projeto com Collections

```text
AppProdutos/
├── src/
├── postman-collections/
│   ├── AppProdutos - Testes Negativos (Permissões).postman_collection.json
│   └── AppProdutos - Reviews, Auditoria e Relatórios (CORRIGIDA).postman_collection.json
├── docker-compose.yml
├── README.md
└── TESTING.md
```

**Problemas Comuns**:
- Verificar se Redis está rodando: `docker ps`
- Reiniciar aplicação entre collections
- Consultar Swagger UI para documentação atualizada
- Verificar logs da aplicação para detalhes de erro  
