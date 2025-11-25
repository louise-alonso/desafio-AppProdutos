# AppProdutos - API de E-commerce Backend

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green)
![Security](https://img.shields.io/badge/JWT-Auth-blue)
![H2 Database](https://img.shields.io/badge/Database-H2-blue)
![Redis](https://img.shields.io/badge/Redis-Cache-red)
![Docker](https://img.shields.io/badge/Docker-Support-blue)
![Swagger](https://img.shields.io/badge/Swagger-UI-brightgreen)
![Flyway](https://img.shields.io/badge/Flyway-Migration-red)

API RESTful completa para gerenciamento de um sistema de E-commerce. O projeto foi desenvolvido com foco em **boas práticas, arquitetura limpa, segurança robusta e cobertura de testes**, atendendo a um conjunto rigoroso de regras de negócio.

##  Funcionalidades e Regras de Negócio

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
* * **Integridade de Deleção:** Para preservar o histórico e consistência, **não é permitido deletar uma Categoria** se ela possuir produtos vinculados ou subcategorias filhas. É necessário esvaziá-la antes.

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
## Status de Implementação do Projeto

Abaixo, o resumo do que foi desenvolvido com base nos requisitos solicitados.

| Requisito / Funcionalidade      | Status  | Observação                                               |
|:--------------------------------|:-------:|:---------------------------------------------------------|
| **1. Autenticação e Segurança** | ✅ Feito | JWT, Roles, BCrypt, Refresh Token.                       |
| **2. Categorias (Hierarquia)**  | ✅ Feito | Árvore Pai/Filho e Integridade Referencial.              |
| **3. Controle de Estoque**      | ✅ Feito | Histórico de transações e travas de saldo.               |
| **4. Carrinho de Compras**      | ✅ Feito | Validação de preço e estoque em tempo real.              |
| **5. Pedidos (Checkout)**       | ✅ Feito | Fluxo completo com estorno em cancelamento.              |
| **6. Promoções e Cupons**       | ✅ Feito | Validações complexas de uso e expiração.                 |
| **7. Reviews e Avaliações**     | ✅ Feito | Regra de "Compra Verificada" implementada.               |
| **8. Auditoria (Logs)**         | ✅ Feito | Snapshots JSON de alterações.                            |
| **9. Relatórios Gerenciais**    | ✅ Feito | Vendas, Top Produtos e Estoque Baixo.                    |
| **Documentação (Swagger)**      | ✅ Feito | Guia passo a passo e exemplos interativos.               |
| **Testes Automatizados**        | ✅ Feito | Unitários e Integração (JUnit/Mockito).                  |
| **Migrations (Flyway)**         | ✅ Feito | Versionamento de banco de dados.                         |
| *Notificações por E-mail*       | ✅ Feito | Desafio Bônus                                            |
| *Caching (Redis)*               | ✅ Feito | Desafio Bônus                                            |
| *Agendamento (Scheduler)*       | ✅ Feito | Desafio Bônus                                            |
| *Multi-seller*                  | ✅ Feito | Desafio Bônus                                            |

---
## 🐳 Infraestrutura e Desafio Bônus (Redis)

Embora a aplicação principal e o banco de dados (H2) rodem na JVM localmente, utilizamos o **Docker Compose** para atender ao **requisito bônus de performance (Caching)**.

**Por que Docker?**
O Docker é utilizado neste projeto exclusivamente para subir o container do **Redis** e expor a porta `6379`. Isso permite que a aplicação Java conecte-se ao serviço de cache sem a necessidade de instalar o servidor Redis manualmente no sistema operacional.

**Serviços do Docker Compose:**
1.  **Redis:** Banco NoSQL em memória para cache.
2.  **Redis Commander:** Interface web para visualizar as chaves salvas no cache (Porta 8081).

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos
* Java 21 e Maven instalados.
* Docker Desktop instalado (para o Redis).

### Passos
1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/seu-usuario/AppProdutos.git](https://github.com/seu-usuario/AppProdutos.git)
    cd AppProdutos
    ```

2.  **Execute a aplicação:**
    ABRA O DOCKER DESKTOP
    ```bash
    cd .\src\main\java\br\com\louise\AppProdutos\
    ```

    ```bash
    docker-compose up -d
    ```
Isso iniciará o Redis na porta 6379 e o Redis Commander na porta 8081.    

Com o Redis ativo, inicie a aplicação Spring Boot clicendo em application.properties

    



## Passo 3: Acessar

Swagger (API): http://localhost:8080/swagger-ui.html  
H2 Console (Banco): http://localhost:8080/h2-console  
Redis Commander (Cache Visual): http://localhost:8081

## Como Testar o Bônus (Redis Caching)

Para verificar se o Caching com Redis está funcionando corretamente:

### Abra o Console da Aplicação
Fique de olho no terminal onde o Java está rodando.

### Faça a Primeira Requisição (Cache Miss)
Vá no Swagger e execute GET /products.

Resultado:  
Você verá no log uma consulta SQL (Hibernate: select ...) buscando no H2.

### Faça a Segunda Requisição (Cache Hit)
Execute GET /products novamente.

Resultado:  
Não haverá consulta SQL no log.  
O tempo de resposta será muito mais rápido (dado vindo do Redis).



##  Testes Automatizados

O projeto conta com uma suíte robusta de testes cobrindo Controllers, Services e Repositories, garantindo que todas as regras de negócio acima estejam funcionando.

**Para detalhes técnicos sobre a estratégia de testes e mapa de endpoints, consulte o arquivo [TESTING.md](TESTING.md).**

Para rodar os testes:
```bash
mvn test

## Configuração de E-mail (Opcional)

Para testar o envio de notificações (Scheduler de estoque baixo), o projeto utiliza o **Mailtrap** (servidor SMTP fake).

1. Crie uma conta gratuita em [Mailtrap.io](https://mailtrap.io/).
2. No painel, vá em **Inboxes** > **SMTP Settings**.
3. Configure as variáveis no `application.properties` ou passe como argumentos ao rodar a aplicação:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.mail.username=SEU_USER --spring.mail.password=SUA_SENHA"