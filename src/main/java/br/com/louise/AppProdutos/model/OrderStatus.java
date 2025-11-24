package br.com.louise.AppProdutos.model;

public enum OrderStatus {
    CREATED,    // Criado (aguardando pagto)
    PAID,       // Pago
    SHIPPED,    // Enviado
    DELIVERED,  // Entregue
    CANCELLED   // Cancelado
}