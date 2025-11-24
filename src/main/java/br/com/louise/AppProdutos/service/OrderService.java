package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.order.DTOOrderRequest;
import br.com.louise.AppProdutos.dto.order.DTOOrderResponse;
import br.com.louise.AppProdutos.dto.order.DTOOrderStatusRequest;

import java.util.List;

public interface OrderService {

    DTOOrderResponse createOrder(DTOOrderRequest request);

    DTOOrderResponse updateOrderStatus(String orderId, DTOOrderStatusRequest request);

    List<DTOOrderResponse> getLatestOrders();

    void deleteOrder(String orderId);

    DTOOrderResponse getOrderById(String orderId);

    void cancelOrder(String orderId);

}