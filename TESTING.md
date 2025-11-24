# Estratégia de Testes - AppProdutos

Este documento descreve a abordagem de testes automatizados utilizada no projeto **AppProdutos**. O objetivo é garantir a integridade das regras de negócio, a segurança dos endpoints e a estabilidade do sistema como um todo.

## 🛠 Tecnologias e Ferramentas

* **JUnit 5:** Framework base para a execução dos testes.
* **Mockito:** Framework de mocking para isolar componentes e simular comportamentos de dependências (Service/Repository).
* **Spring Boot Test:** Suporte integrado para testes de contexto (`@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`).
* **MockMvc:** Ferramenta para simular requisições HTTP e validar respostas de Controllers sem subir um servidor real.
* **H2 Database:** Banco de dados em memória utilizado para testes de repositório, garantindo isolamento e rapidez.

## 🏗 Estrutura dos Testes

Os testes seguem a mesma estrutura de pacotes do código-fonte principal (`src/main/java`), localizados em `src/test/java`:

```text
src/test/java/br/com/louise/AppProdutos
├── controller   # Testes de Integração da Camada Web (REST)
├── service      # Testes Unitários da Lógica de Negócio
└── repository   # Testes de Integração com Banco de Dados

Tipos de Testes Implementados1. Testes Unitários (Service Layer)Foco: Validar a lógica de negócio isolada.Técnica: Utilizamos @ExtendWith(MockitoExtension.class) para injetar mocks. Nenhuma conexão com banco de dados ou contexto Spring é carregada aqui, tornando os testes extremamente rápidos.Cenários Cobertos:Cálculo de descontos de cupons (Percentual e Fixo).Validação de estoque insuficiente e fluxo de movimentação (Entrada/Saída).Regras de checkout (Carrinho vazio, Usuário inexistente).Lógica de Média de Avaliações (Reviews).Segurança: Validação se o usuário (Seller) é dono do produto.2. Testes de Controlador (Controller Layer)Foco: Validar o contrato da API (Status Code, JSON de resposta e Segurança).Técnica: Utilizamos @WebMvcTest para carregar apenas a camada web.Segurança: Simulamos a autenticação e autorização com @WithMockUser e mocks do TokenService e UserDetailsService.Cenários Cobertos:POST /orders: Deve retornar 201 Created para payload válido.POST /reviews: Deve retornar 400 Bad Request se faltar o ID do pedido.GET /reports: Deve retornar 403 Forbidden se o usuário não for ADMIN.3. Testes de Repositório (Data Layer)Foco: Validar queries customizadas (JPQL/Native SQL) e mapeamento de entidades.Técnica: Utilizamos @DataJpaTest, que configura automaticamente um banco H2 em memória.Cenários Cobertos:Relatório de Vendas: Agrupamento por data e soma de valores.Histórico de Estoque: Ordenação correta por data de criação (Decrescente).🚀 Como Executar os TestesVia Linha de Comando (Maven)Para rodar a suíte completa de testes:Bashmvn test
Para rodar apenas um teste específico (ex: apenas os de Pedido):Bashmvn -Dtest=OrderServiceImplTest test
Via IDE (IntelliJ / Eclipse)Navegue até a pasta src/test/java.Clique com o botão direito na pasta ou em um arquivo específico.Selecione "Run Tests" ou "Run 'All Tests'".✅ Resumo da CoberturaMóduloCamadaStatusO que é testado?OrdersService✅Criação, Cancelamento, Estorno de Estoque e Integração com Cupom.InventoryService/Repo✅Movimentações (ENTRY/EXIT), Exceção de saldo insuficiente e Queries de histórico.CartService✅Adição de itens, soma de quantidades e limpeza pós-venda.ReviewsController/Service✅Endpoint de criação, regra de compra verificada e cálculo de rating.ReportsController✅Endpoints administrativos e parâmetros de data.CouponsUnit✅Lógica de expiração, limite de uso e valor mínimo.
### Próximo Passo
Rode o comando para adicionar o arquivo ao Git:
