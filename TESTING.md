#  Guia de Testes e Validação - AppProdutos

Este documento descreve o roteiro passo a passo para validar a segurança, autenticação e regras de negócio da API.

> **NOTA:** Estes testes cobrem cenários de **Regressão de Segurança (RBAC)** e **Regras de Negócio Críticas**, devendo ser executados a cada nova versão.

**Pré-requisitos:**
- Aplicação rodando (`mvn spring-boot:run`)
- Postman, Insomnia ou outro API Client

---

## Ciclo 1: Configuração Inicial e Autenticação

### 1. Criar Usuário Admin no Banco

**1.1 Obter HASH de Senha**  
`POST /auth/encode`  
Body: `{"password": "senhaadmin"}`  
→ Copie o hash gerado.

**1.2 Inserir no H2 Console**  
Use o hash para criar o Admin com `ROLE_ADMIN`.

---

### 2. Login e Criação de Perfis

**2.1 Token Admin**  
`POST /auth/login`  
→ Copie o **TOKEN_ADMIN**.

**2.2 Criar Perfis**  
Usando o **TOKEN_ADMIN**:
- Crie `ROLE_SELLER`
- Crie `ROLE_CUSTOMER`

**2.3 Obter Tokens Individuais**  
Faça login como SELLER e CUSTOMER.

| Perfil | Permissão |
|-------|-----------|
| Admin | Total |
| Seller | CRUD de produtos próprios |
| Customer | Apenas leitura |

---

##  Ciclo 2: Testes de Usuário e Segurança (CRUD Geral)

**Token necessário: ADMIN**

| Ação | Rota | Cenário | Esperado | Regra |
|------|------|----------|-----------|--------|
| Criar usuário | POST /admin/register | Criar SELLER | 201 | Criação liberada |
| Listar usuários | GET /admin/users | Listar todos | 200 | Admin pode listar |
| Atualizar | PUT /admin/users/{id} | Trocar role | 200 | PUT funcionando |
| Atualizar (negativo) | PUT /admin/users/{id} | E-mail duplicado | 400 | E-mail único |
| Deletar | DELETE /admin/users/{id} | Remoção | 204 | DELETE funcionando |

---

##  Ciclo 3: Testes de Categoria (Integridade e Estrutura)

**Token necessário: ADMIN**

| Ação | Rota | Cenário | Esperado | Regra |
|------|------|----------|-----------|--------|
| Criar categoria | POST /admin/categories | Criar raiz "Eletrônicos" | 201 | Nó raiz válido |
| Atualizar | PUT /admin/categories/{id} | Alterar nome/descrição | 200 | PUT funcionando |
| Bloqueio hierarquia | POST /admin/categories | Enviar `parentId` | 400 | Catálogo plano |
| Bloqueio delete | DELETE /admin/categories/{id} | Categoria com produtos | 400 | Integridade FK |
| Self-parenting | PUT /admin/categories/{id} | Categoria apontando para si mesma | 400 | Proibir loops |

---

## Ciclo 4: Testes de Produto e Propriedade

**Setup:**
- Seller 1 cria produto (P1).
- Seller 2 tenta operações restritas.

| Ação | Rota | Token | Cenário | Esperado | Regra |
|------|------|--------|----------|-----------|---------|
| Criar produto | POST /admin/products | SELLER 1 | Criar P1 | 201 | Seller pode criar |
| SKU duplicado | POST /admin/products | ADMIN | SKU já usado | 400 | SKU único |
| Atualizar (negativo) | PUT /admin/products/{id} | SELLER 2 | Alterar P1 | 403 | Owner lock |
| Atualizar (positivo) | PUT /admin/products/{id} | SELLER 1 | Alterar preço | 200 | Dono pode editar |
| Deletar | DELETE /admin/products/{id} | ADMIN | Remover P1 | 204 | Admin pode tudo |

---

## Ciclo 5: Testes do Perfil CUSTOMER

**Token necessário: CUSTOMER**

| Ação | Rota | Esperado | Regra |
|------|------|-----------|--------|
| Ler catálogo | GET /products | 200 | Leitura liberada |
| Bloqueio CRUD | POST /admin/products | 403 | Customer não cria |
| Bloqueio admin | GET /admin/users | 403 | Sem acesso a /admin |

---

## Ciclo 6: Testes Finais de Segurança

| Ação | Cenário | Esperado |
|------|----------|-----------|
| Token adulterado | Token inválido | 403 Forbidden |
| Login inválido | Senha errada | 400 Bad Request |
