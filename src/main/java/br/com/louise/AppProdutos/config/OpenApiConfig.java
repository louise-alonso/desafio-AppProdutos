package br.com.louise.AppProdutos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AppProdutos - API de E-commerce",
                version = "1.0",
                contact = @Contact(name = "Suporte", email = "suporte@appprodutos.com"),
                description = """
                        # Descrição Geral – AppProdutos
                        
                        Bem-vindo(a) à API **AppProdutos**.
                        Este Swagger contém *exemplos prontos em todos os endpoints*, permitindo testar facilmente todos os fluxos.
                        
                        ---
                        
                        ##  1. Regras Gerais da API
                        
                        ###  Autenticação
                        - Feita via **Token JWT**.
                        - Para endpoints protegidos, faça login, copie o token, clique no botão **Authorize** (cadeado 🔓) e informe o token na caixa de texto.
                        
                        ###  Perfis de Acesso
                        | Perfil     | Permissões principais                                      |
                        |------------|------------------------------------------------------------|
                        | **ADMIN** | Acesso total ao sistema. CRUD geral e Relatórios.          |
                        | **SELLER** | Gerencia apenas os produtos que ele mesmo criou.           |
                        | **CUSTOMER**| Usa carrinho, aplica cupom e finaliza pedidos.            |
                        
                        ---
                        
                        ##  Regras de Negócio Essenciais
                        
                        ### ️ Produtos
                        - Pertencem a **categorias**.
                        - Criados apenas por **ADMIN** ou **SELLER**.
                        - Estoque sempre >= 0.
                        
                        ###  Carrinho
                        - Um cliente possui **apenas 1 carrinho ativo**.
                        - Itens são atualizados se repetidos.
                        - Valida estoque e preço no momento da adição.
                        
                        ###  Cupons
                        - Só válidos se dentro da data, não expirados e respeitando limites de uso.
                        
                        ###  Pedidos
                        Ao finalizar:
                        - Estoque é descontado automaticamente.
                        - Carrinho é limpo.
                        - Sistema calcula subtotal, descontos e total final.
                        
                        ---
                        
                        ##  Passo a Passo Rápido para Testes
                        
                        ### 1. Criar usuário
                        `POST /admin/register`
                        
                        ### 2. Fazer login
                        `POST /auth/login`
                        ➡ Salve o **token JWT** e use em **Authorize**.
                        
                        ### 3. Criar categoria
                        `POST /categories` (Requer ADMIN)
                        ➡ Salve o **ID da categoria**.
                        
                        ### 4. Criar produto
                        `POST /admin/products`
                        ➡ Salve o **ID do produto**.
                        
                        ### 5. Encher Carrinho
                        `POST /cart/add` (Requer CUSTOMER)
                        ➡ Adicione produtos ao carrinho.
                        
                        ### 6. Criar Cupom (Opcional)
                        `POST /coupons` (Requer ADMIN)
                        
                        ### 7. Finalizar pedido
                        `POST /orders`
                        ➡ Envie o método de pagamento e código do cupom. O sistema gera o pedido.
                        
                        ---
                        
                        ##  O Que Deve Ser Salvo Durante os Testes
                        - Token JWT (Admin e Customer)
                        - ID da categoria
                        - ID do produto
                        - ID do pedido (para avaliar depois)
                        
                        ---
                        
                        ##  Observações Importantes
                        - Todos os endpoints possuem exemplos (JSON) prontos aqui no Swagger.
                        - Erro **403** = falta de permissão (Verifique seu Token no Authorize).
                        - Sempre aperte **Authorize** antes de testar rotas fechadas.
                        """
        ),
        tags = {
                @Tag(name = "01. Autenticação e Usuários", description = "Login e Registro."),
                @Tag(name = "02. Categorias", description = "Gestão do catálogo."),
                @Tag(name = "03. Produtos", description = "Gestão de itens de venda."),
                @Tag(name = "04. Cupons", description = "Descontos e promoções."),
                @Tag(name = "05. Estoque (Inventory)", description = "Ajustes manuais."),
                @Tag(name = "06. Carrinho de Compras", description = "Operações do cliente."),
                @Tag(name = "07. Pedidos e Pagamentos", description = "Checkout."),
                @Tag(name = "08. Avaliações (Reviews)", description = "Feedback."),
                @Tag(name = "09. Relatórios e Auditoria", description = "Área Admin.")
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Cole seu token JWT aqui.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}