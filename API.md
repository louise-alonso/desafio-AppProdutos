# API.md - Guia de Referência da API

Este documento lista e descreve todos os endpoints da **AppProdutos API**, incluindo rotas, métodos, bodies esperados, respostas e requisitos de permissão (RBAC).

Prefixos:
- Rotas públicas → sem prefixo
- Rotas de autenticação → `/auth`
- Rotas administrativas → `/admin`

---------------------------------------------------------------------

## AUTENTICAÇÃO (DUAL TOKEN)

### POST /auth/login
Autentica usuário e inicia sessão.

Body (JSON):
{
"email": "string",
"password": "string"
}

Resposta (200):
{
"accessToken": "string (JWT - 1 hora)",
"refreshToken": "string (UUID - 30 dias)",
"email": "string",
"role": "string"
}

Permissão: Público
---

### POST /auth/refresh
Renova o Access Token usando um Refresh Token válido.

Body (JSON):
{
"refreshToken": "string (UUID)"
}

Resposta (200):
{
"accessToken": "string (Novo JWT)",
"refreshToken": "string (UUID mantido)",
"email": "string",
"role": "string"
}

Permissão: Público

---

### GET /auth/me
Retorna os dados do usuário logado (para validação de token).

Resposta (200):
"Usuário autenticado: {email}"

Permissão: Autenticado (Qualquer perfil)
---------------------------------------------------------------------

## USUÁRIOS E CADASTRO

### POST /admin/register
Cria um novo usuário no sistema.
*Nota: Endpoint público para permitir o primeiro acesso (Bootstrap).*

Body (JSON):
{
"name": "string",
"email": "string",
"password": "string",
"role": "ADMIN | SELLER | CUSTOMER"
}

Resposta (201):
{
"userId": "string (UUID)",
"name": "string",
"email": "string",
"role": "string"
}

Permissão: Público

---

### GET /admin/users
Lista todos os usuários cadastrados.

Resposta (200):
[
{
"userId": "string (UUID)",
"name": "string",
"email": "string",
"role": "string"
}
]

Permissão: ROLE_ADMIN

---

### PUT /admin/users/{userId}
Atualiza dados de um usuário existente.

Body (JSON):
{
"name": "string",
"email": "string",
"password": "string (opcional)",
"role": "ADMIN | SELLER | CUSTOMER"
}

Resposta (200):
{
"userId": "string (UUID)",
"name": "string",
"email": "string",
"role": "string"
}

Permissão: ROLE_ADMIN

---

### DELETE /admin/users/{userId}
Remove um usuário do sistema.

Resposta: 204 No Content  
Permissão: ROLE_ADMIN

---------------------------------------------------------------------

## CATEGORIAS (CATÁLOGO PLANO)

### POST /admin/categories
Cria categoria.

Body (JSON):
{
"name": "string",
"description": "string"
}

Regras:
- `parentId` é proibido (categoria é sempre raiz)

Resposta (201):
{
"categoryId": "string (UUID)",
"name": "string",
"description": "string"
}

Permissão: ROLE_ADMIN

---

### GET /categories
Lista todas as categorias.

Resposta (200):
[
{
"categoryId": "string (UUID)",
"name": "string",
"description": "string"
}
]

Permissão: Público

---

### GET /categories/{categoryId}
Retorna categoria por ID.

Resposta (200):
{
"categoryId": "string (UUID)",
"name": "string",
"description": "string"
}

Permissão: Público

---

### PUT /admin/categories/{categoryId}
Atualiza categoria.

Body (JSON):
{
"name": "string",
"description": "string"
}

Resposta (200):
{
"categoryId": "string (UUID)",
"name": "string",
"description": "string"
}

Permissão: ROLE_ADMIN

---

### DELETE /admin/categories/{categoryId}
Remove categoria.

Regras:
- Retorna 400 Bad Request se houver produtos vinculados.

Resposta: 204 No Content  
Permissão: ROLE_ADMIN

---------------------------------------------------------------------

## PRODUTOS (SKU ÚNICO + OWNER)

### POST /admin/products
Cria produto.

Body (JSON):
{
"name": "string",
"sku": "string",
"price": number,
"stockQuantity": number,
"categoryId": "string (UUID)"
}

Regras:
- SKU deve ser único
- owner = usuário autenticado

Resposta (201):
{
"productId": "string (UUID)",
"name": "string",
"sku": "string",
"price": number,
"stockQuantity": number,
"ownerId": "string (UUID)",
"categoryId": "string (UUID)"
}

Permissão: ROLE_ADMIN ou ROLE_SELLER

---

### GET /products
Lista todos os produtos públicos.

Resposta (200):
[
{
"productId": "string (UUID)",
"name": "string",
"sku": "string",
"price": number
}
]

Permissão: Público

---

### GET /products/{productId}
Detalhes de um produto.

Resposta (200):
{
"productId": "string (UUID)",
"name": "string",
"sku": "string",
"price": number,
"stockQuantity": number,
"categoryId": "string (UUID)",
"ownerId": "string (UUID)"
}

Permissão: Público

---

### PUT /admin/products/{productId}
Atualiza produto.

Body (JSON):
{
"name": "string",
"sku": "string",
"price": number,
"stockQuantity": number,
"categoryId": "string (UUID)"
}

Regras:
- ADMIN pode editar qualquer produto
- SELLER só pode editar produtos onde é owner

Resposta (200):
{
"productId": "string (UUID)",
"name": "string",
"price": number,
"stockQuantity": number
}

Permissão: ROLE_ADMIN ou ROLE_SELLER (se owner)

---

### DELETE /admin/products/{productId}
Remove produto.

Regras:
- SELLER só pode remover se for owner

Resposta: 204 No Content  
Permissão: ROLE_ADMIN ou ROLE_SELLER (se owner)

---------------------------------------------------------------------

## CÓDIGOS DE STATUS (PADRÃO)

200 OK → Sucesso geral  
201 Created → Recurso criado  
204 No Content → Exclusão bem-sucedida  
400 Bad Request → Violação de regra (SKU duplicado, email duplicado, parentId enviado, violação de integridade)  
401 Unauthorized → Token ausente ou inválido  
403 Forbidden → Sem permissão para acessar o recurso  
404 Not Found → ID não encontrado

---------------------------------------------------------------------
