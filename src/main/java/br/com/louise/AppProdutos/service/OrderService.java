package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.DTOOrderStatusRequest;
import br.com.louise.AppProdutos.dto.OrderRequest;
import br.com.louise.AppProdutos.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    OrderResponse updateOrderStatus(String orderId, DTOOrderStatusRequest request);

    List<OrderResponse> getLatestOrders();

    void deleteOrder(String orderId);

    OrderResponse getOrderById(String orderId);

    void cancelOrder(String orderId);

}