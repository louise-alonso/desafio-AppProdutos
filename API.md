# API.md - Guia de Referência da API

---------------------------------------------------------------------

## AUTENTICAÇÃO (DUAL TOKEN)

### POST /auth/login
Autentica usuário e inicia sessão. Retorna `accessToken` e `refreshToken`.

---------------------------------------------------------------------

## PROMOÇÕES E CUPONS 🆕

### POST /coupons
Cria um novo cupom de desconto.

Body (JSON):
{
"code": "string (ex: NATAL10)",
"type": "PERCENTAGE | FIXED",
"value": number,
"expirationDate": "YYYY-MM-DD",
"globalUsageLimit": integer (opcional),
"usageLimitPerUser": integer (opcional),
"minOrderValue": number (opcional),
"targetProductId": "string (UUID) (opcional)"
}

Resposta (201):
{
"id": number,
"code": "NATAL10",
"active": true
}

Permissão: ROLE_ADMIN

---------------------------------------------------------------------

## PEDIDOS (CHECKOUT)

### POST /orders
Finaliza a compra. Transforma os itens do Carrinho em um Pedido.

Body (JSON):
{
"customerName": "string (opcional)",
"phoneNumber": "string",
"paymentMethod": "PIX | BOLETO",
"couponCode": "string (opcional)"  <-- CAMPO NOVO
}

Regras:
- O carrinho não pode estar vazio.
- Se `couponCode` for enviado, valida validade, limites e aplica desconto.
- Baixa o estoque atomicamente.

Resposta (201):
{
"orderId": "string (UUID)",
"status": "CREATED | PAID",
"grandTotal": number,
"products": [...]
}

Permissão: ROLE_CUSTOMER

---------------------------------------------------------------------

## AVALIAÇÕES (REVIEWS) 🆕

### POST /reviews
Cria uma avaliação para um produto comprado.

Body (JSON):
{
"productId": "string (UUID)",
"orderId": "string (UUID)",
"rating": integer (1-5),
"comment": "string (max 500 chars)"
}

Regras:
- O usuário deve ter comprado o produto.
- O pedido deve estar com status `PAID`, `SHIPPED` ou `DELIVERED`.
- Limite de 1 avaliação por produto por pedido.

Resposta (201):
{
"id": number,
"userName": "string",
"rating": number,
"comment": "string",
"createdAt": "timestamp"
}

Permissão: ROLE_CUSTOMER

---

### GET /reviews/product/{productId}
Lista todas as avaliações de um produto específico.

Resposta (200):
[
{
"id": number,
"userName": "string",
"rating": number,
"comment": "string",
"createdAt": "timestamp"
}
]

Permissão: Público

---------------------------------------------------------------------