package br.com.louise.AppProdutos.model;

public enum TransactionType {
    ENTRY,      // Entrada (Compra de fornecedor) -> Aumenta Estoque
    EXIT,       // Saída (Venda/Pedido) -> Diminui Estoque
    ADJUSTMENT, // Ajuste (Correção de contagem) -> Pode aumentar ou diminuir
    RETURN      // Devolução de Cliente -> Aumenta Estoque
}