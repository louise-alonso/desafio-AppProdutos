# AppProdutos - API de E-commerce Backend

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green)
![Security](https://img.shields.io/badge/JWT-Auth-blue)
![Swagger](https://img.shields.io/badge/Swagger-UI-brightgreen)
![Flyway](https://img.shields.io/badge/Flyway-Migration-red)

API RESTful completa para gerenciamento de um sistema de E-commerce. O projeto foi desenvolvido com foco em **boas práticas, arquitetura limpa, segurança robusta e cobertura de testes**, atendendo a um conjunto rigoroso de regras de negócio.

## Funcionalidades e Regras de Negócio

O sistema implementa todas as regras de negócio propostas, garantindo consistência e segurança nas operações.

### 1. Autenticação e Autorização (Segurança)
* **Login JWT:** Implementação de Access Token (curta duração) e Refresh Token (longa duração).
* **Proteção de Rotas:** Endpoints protegidos via `@PreAuthorize`.
* **Perfis de Acesso (Roles):**
    * **ADMIN:** Acesso total (criar/editar/deletar produtos, categorias, cupons e ver relatórios).
    * **SELLER:** Pode cadastrar produtos, mas só pode editar/deletar os produtos que **ele mesmo criou**.
    * **CUSTOMER:** Acesso limitado a visualizar catálogo, gerenciar próprio carrinho, fechar pedidos e avaliar compras.

### 2. Categorias e Catálogo
* **Hierarquia:** Suporte a categorias com estrutura de árvore (Pai → Filho).
* **Unicidade:** O nome da categoria é único no sistema para evitar duplicidade.
* **Vínculo:** Todo produto deve obrigatoriamente pertencer a uma categoria existente.

### 3. Controle de Estoque (Inventário)
* **Auditoria de Movimentação:** Cada alteração de saldo gera um registro imutável em `InventoryTransaction` com tipo (ENTRADA, SAÍDA, AJUSTE, DEVOLUÇÃO) e responsável.
* **Travamento de Venda:** O sistema impede vendas se o estoque for insuficiente.
* **Atualização Automática:** A venda (Pedido) baixa o estoque; o cancelamento (Pedido) estorna o estoque automaticamente.

### 4. Carrinho de Compras
* **Carrinho Único:** Cada usuário autenticado possui apenas um carrinho ativo.
* **Snapshot de Preço:** O preço é salvo no momento da adição ao carrinho.
* **Cálculo Dinâmico:** Atualizações de quantidade recalcula automaticamente os subtotais e totais.
* **Validação de Estoque:** Não é possível adicionar ao carrinho mais itens do que o disponível em estoque.

### 5. Pedidos (Orders)
* **Fluxo de Status:** O pedido transita por estados controlados: `CREATED` → `PAID` → `SHIPPED` → `DELIVERED` ou `CANCELLED`.
* **Regra de Cancelamento:** O cancelamento só é permitido se o status for `CREATED` ou `PAID`. Pedidos enviados não podem ser cancelados via API.
* **Checkout:** Transforma os itens do carrinho em um pedido fechado e limpa o carrinho.

### 6. Promoções e Cupons
* **Tipos de Desconto:** Suporte a desconto Percentual (%) ou Fixo (R$).
* **Regras de Validação:** O sistema rejeita o cupom se:
    * Estiver expirado (Data de validade).
    * Atingiu o limite global de usos.
    * O usuário já atingiu seu limite pessoal de uso daquele cupom.
    * O valor do pedido for menor que o valor mínimo exigido.

### 7. Reviews e Avaliações
* **Compra Verificada:** Apenas usuários que compraram o produto podem avaliá-lo.
* **Status do Pedido:** A avaliação só é liberada após o pedido ser `PAID` ou `DELIVERED`.
* **Unicidade:** Limite de 1 avaliação por produto por pedido (evita spam).
* **Média Automática:** A nota média do produto é recalculada a cada nova avaliação recebida.

### 8. Auditoria (Audit Log)
* **Rastreabilidade:** Registro automático de quem criou, alterou ou deletou entidades críticas (ex: Produtos).
* **Snapshot:** Armazena o estado "Antes" e "Depois" da alteração em formato JSON.
* **Imutabilidade:** Os logs de auditoria não podem ser alterados ou excluídos via API.

### 9. Relatórios Gerenciais
* **Vendas:** Relatório de faturamento agrupado por período.
* **Top Produtos:** Lista dos produtos mais vendidos.
* **Estoque Crítico:** Alerta de produtos com quantidade abaixo do nível de segurança.

---

## Documentação Interativa (Swagger)

A API possui uma documentação completa e interativa.
* **Guia Passo a Passo:** Tutorial integrado na página inicial do Swagger.
* **Exemplos de JSON:** Payloads de requisição pré-preenchidos.
* **Teste Real:** Botão "Try it out" para executar requisições.

**Como acessar:**
1. Rode a aplicação.
2. Abra no navegador: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Tecnologias Utilizadas

* **Java 21** & **Spring Boot 3**
* **Spring Security** (JWT + OAuth2 Resource Server pattern)
* **Spring Data JPA** & **H2 Database** (Memória/Dev)
* **Flyway** (Versionamento de Banco de Dados)
* **SpringDoc OpenAPI** (Swagger UI)
* **Lombok** & **Bean Validation**
* **JUnit 5** & **Mockito** (Testes Unitários e de Integração)

---

## ️ Como Rodar o Projeto

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

3.  **Acesse os recursos:**
    * **Swagger (API):** `http://localhost:8080/swagger-ui.html`
    * **H2 Console (Banco):** `http://localhost:8080/h2-console`

## Testes Automatizados

O projeto conta com uma suíte robusta de testes cobrindo Controllers, Services e Repositories, garantindo que todas as regras de negócio acima estejam funcionando.

Para rodar os testes:
```bash
mvn test