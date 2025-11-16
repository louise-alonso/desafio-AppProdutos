# 🎓 Desafio de Evolução do Projeto AppProdutos

Olá! Meu nome é **[Louise Afonso Lemos ALonso]** e este repositório contém o trabalho que desenvolvi como parte do desafio prático para evoluir o sistema **AppProdutos**.

Este projeto visa aprofundar conhecimentos em **Spring Boot**, **Modelagem de Dados**, **Regras de Negócio**, **Autenticação (JWT/Spring Security)** e **Boas Práticas de Desenvolvimento Backend**.

---

## 🎯 Requisitos Implementados

O projeto original (AppProdutos) foi expandido com as seguintes funcionalidades e melhorias, seguindo as diretrizes do desafio.

### 1. Organização do Catálogo (Categorias)
Implementação completa da funcionalidade de Categorias, permitindo a organização dos produtos.

* ✅ **Entidade `Categoria`**: Criada com campos de auditoria (`createdAt`, `updatedAt`) e validação de unicidade (`nome`, `categoriaId`).
* ✅ **DTOs Específicos**: Uso de `CategoriaRequest` e `CategoriaResponse` para controle de entrada e saída de dados.
* ✅ **Mapeamento de Produto**: *(A ser implementado)* Associação de `Produtos` com a nova entidade `Categoria`.
* ✅ **Endpoints de CRUD**: *(A ser implementado)* Controladores para criar, listar, atualizar e deletar categorias.

### 2. Controle de Estoque/Inventário
Revisão e expansão da entidade de Estoque, focando no controle de inventário.

### 3. (Próxima Prioridade) Autenticação e Autorização (Spring Security/JWT)
Baseado no exemplo `Login-BE`, a próxima fase será a implementação da segurança.

* 🚧 **Configuração do Spring Security**: Configuração inicial para proteção de rotas.
* 🚧 **Criação de Papéis (Roles)**: Implementação dos perfis `ADMIN`, `SELLER` e `CUSTOMER`.
* 🚧 **Proteção de Endpoints**: Uso de `@PreAuthorize` nas rotas do `ProdutoController` e `CategoriaController`.

---

## 🛠️ Detalhes Técnicos e Boas Práticas

| Aspecto | Status / Implementação |
| :--- | :--- |
| **Linguagem/Framework** | Java 21 / Spring Boot 3.5.7 |
| **Persistência** | Spring Data JPA |
| **Banco de Dados** | H2 Database (em memória) |
| **Modelagem** | Uso de **Java Records** (`ProdutoDto`) e DTOs dedicados. |
| **Auditoria** | Uso de `@CreationTimestamp` e `@UpdateTimestamp` na entidade `Categoria`. |
| **Build Tool** | Maven (`pom.xml` configurado) |

---

## 📝 Contribuições e Versionamento

Seguindo as sugestões, este desenvolvimento foi realizado adotando boas práticas de versionamento (Git Flow simplificado):

* **Branching**: Utilização de branches separadas para cada funcionalidade (`feature/categorias`, `fix/atualizacao-service`).
* **Commits**: Mensagens claras e atômicas, refletindo a tarefa executada.
* **[Opcional: Se for em equipe]** **Pull Requests**: Utilizados para revisão de código entre os membros da equipe antes do merge para `main`/`develop`.

---

## ⏭️ Próximos Passos no Desenvolvimento

1.  Finalizar o `CategoriaController` (CRUD) e o respectivo `CategoriaService`.
2.  Associar `Produtos` com `Categoria`.
3.  Iniciar a configuração do **Spring Security e JWT**.
4.  Implementar o fluxo de **InventoryTransaction** (Transações de Estoque).

Qualquer feedback ou sugestão é bem-vindo!