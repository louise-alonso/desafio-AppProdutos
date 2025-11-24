# Guia de Testes e Validação - AppProdutos

Este documento descreve o roteiro passo a passo para validar a segurança, autenticação, regras de negócio e o fluxo de pedidos da API.

**Pré-requisitos:**
- Aplicação rodando (`mvn spring-boot:run`)
- Postman, Insomnia ou outro API Client

---

## Ciclo 1: Configuração Inicial (Bootstrap)

**1.1 Registrar Admin, Vendedor e Cliente**
* Use a rota `POST /admin/register` para criar os 3 perfis.

**1.2 Login (Obter Tokens)**
* Use a rota `POST /auth/login` e guarde os tokens: `TOKEN_ADMIN`, `TOKEN_SELLER`, `TOKEN_CLIENT`.

---

## Ciclo 2: Gestão de Usuários e Segurança (RBAC)

**Token Ativo:** `TOKEN_ADMIN`

| Ação | Rota | Token | Esperado |
|------|------|-------|----------|
| Listar Usuários | `GET /admin/users` | ADMIN | **200 OK** |
| Bloqueio | `GET /admin/users` | SELLER | **403 Forbidden** |
| Renovar Token | `POST /auth/refresh` | (Público) | **200 OK** |

---

## Ciclo 3: Categorias e Produtos (Setup)

1.  **Criar Categoria** (`TOKEN_ADMIN`): `POST /admin/categories`. Guarde o ID.
2.  **Criar Produto** (`TOKEN_SELLER`): `POST /admin/products`.
    * Preço: **100.00**
    * Estoque: **10**
    * Guarde o ID do Produto.

---

## Ciclo 4: Gestão de Estoque

**Token Ativo:** `TOKEN_SELLER`

1.  **Entrada Manual:** `POST /inventory/adjust` (Type: ENTRY, Qtd: 5). Estoque vai para 15.
2.  **Histórico:** `GET /inventory/product/{id}`. Deve listar a criação e o ajuste.

---

## Ciclo 5: Carrinho de Compras

**Token Ativo:** `TOKEN_CLIENT`

1.  **Adicionar:** `POST /cart/add` (Qtd: 2).
2.  **Conferir:** `GET /cart`. Total deve ser **200.00**.

---

## Ciclo 6: Cupons e Promoções 🆕

Vamos testar o novo módulo.

**Token Ativo:** `TOKEN_ADMIN`

1.  **Criar Cupom (10% OFF)**
    * **Rota:** `POST /coupons`
    * **Body:**
        ```json
        {
          "code": "QUERO10",
          "type": "PERCENTAGE",
          "value": 10,
          "expirationDate": "2030-12-31"
        }
        ```
    * **Esperado:** `201 Created`.

2.  **Criar Cupom (Fixo R$ 50)**
    * **Body:** `{"code": "MEGA50", "type": "FIXED", "value": 50, ...}`

---

## Ciclo 7: Fluxo de Pedido com Desconto 🆕

**Token Ativo:** `TOKEN_CLIENT`

1.  **Checkout com Cupom**
    * **Rota:** `POST /orders`
    * **Body:**
        ```json
        {
          "paymentMethod": "PIX",
          "phoneNumber": "11999999999",
          "couponCode": "QUERO10"
        }
        ```
2.  **Validação Financeira**
    * Subtotal (2 itens x 100): **200.00**
    * Desconto (10%): **-20.00**
    * Taxa (10% do subtotal): **+20.00**
    * **Grand Total Esperado:** 200 - 20 + 20 = **200.00**
    * **Status:** `PAID`.

---

## Ciclo 8: Testes de Erro (Cupons) 🆕

| Ação | Cenário | Esperado |
|------|----------|-----------|
| **Cupom Inválido** | Enviar `couponCode: "NAOEXISTE"` no checkout | **404 Not Found** |
| **Cupom Vencido** | Criar cupom com data passada e tentar usar | **400 Bad Request** |
| **Valor Mínimo** | Tentar usar cupom de "Mínimo 500" em compra de 200 | **400 Bad Request** |

---

## Ciclo 9: Avaliações e Reviews (Engajamento) 🆕

Teste a regra de "Compra Verificada".

**Pré-requisito:** Ter realizado o Ciclo 7 (Compra) com o `TOKEN_CLIENT`.

**Token Ativo:** `TOKEN_CLIENT` (Dono do Pedido)

1.  **Preparar Status (Via Admin)**
    * O pedido criado no Ciclo 7 provavelmente está `PAID` (se foi PIX). Se estiver `CREATED`, use o endpoint de admin para mudar para `PAID` ou `DELIVERED`.
    * **Rota:** `PUT /orders/{orderId}/status` (Use `TOKEN_ADMIN`).
    * **Body:** `{"status": "DELIVERED"}`.

2.  **Criar Avaliação (Sucesso)**
    * **Rota:** `POST /reviews`
    * **Body:**
        ```json
        {
          "productId": "COLE_O_ID_DO_PRODUTO",
          "orderId": "COLE_O_ID_DO_PEDIDO",
          "rating": 5,
          "comment": "Produto excelente! Chegou rápido."
        }
        ```
    * **Esperado:** `201 Created`.

3.  **Verificar Média do Produto**
    * **Rota:** `GET /products/{productId}` (Público).
    * **Esperado:** No JSON do produto, `averageRating` deve ser **5.0** e `reviewCount` deve ser **1**.

4.  **Tentar Avaliar Duplicado (Erro)**
    * Repita a requisição do passo 2.
    * **Esperado:** `400 Bad Request` ("Você já avaliou este produto...").

5.  **Ler Avaliações**
    * **Rota:** `GET /reviews/product/{productId}`.
    * **Esperado:** Lista contendo o comentário "Produto excelente!".