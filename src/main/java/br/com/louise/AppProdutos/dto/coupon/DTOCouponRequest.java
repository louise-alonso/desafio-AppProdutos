package br.com.louise.AppProdutos.dto.coupon;

import br.com.louise.AppProdutos.model.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DTOCouponRequest {

    @Schema(description = "Código do cupom", example = "PRIMEIRACOMPRA")
    private String code;

    @Schema(description = "Tipo (PERCENTAGE ou FIXED)", example = "PERCENTAGE")
    private DiscountType type;

    @Schema(description = "Valor do desconto", example = "10.00")
    private BigDecimal value;

    @Schema(description = "Data de validade", example = "2030-12-31")
    private LocalDate expirationDate;

    @Schema(description = "Limite global de usos", example = "100")
    private Integer globalUsageLimit;

    @Schema(description = "Limite por usuário", example = "1")
    private Integer usageLimitPerUser;

    @Schema(description = "Valor mínimo do pedido", example = "50.00")
    private BigDecimal minOrderValue;

    @Schema(description = "ID da Categoria alvo (opcional)", example = "")
    private String targetCategoryId;

    @Schema(description = "ID do Produto alvo (opcional)", example = "")
    private String targetProductId;
}