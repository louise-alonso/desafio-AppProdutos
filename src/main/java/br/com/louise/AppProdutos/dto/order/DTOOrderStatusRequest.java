package br.com.louise.AppProdutos.dto;

import br.com.louise.AppProdutos.model.OrderStatus;
import lombok.Data;

@Data
public class DTOOrderStatusRequest {
    private OrderStatus status;
}