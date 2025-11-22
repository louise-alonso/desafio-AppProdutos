package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("orderPermissionService")
@RequiredArgsConstructor
public class OrderPermissionService {

    private final OrderEntityRepository orderRepository;

    public boolean isOrderOwner(String orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentEmail = authentication.getName();

        return orderRepository.findByOrderId(orderId)
                .map(order -> order.getCustomer().getEmail().equals(currentEmail))
                .orElse(false);
    }
}