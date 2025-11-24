package br.com.louise.AppProdutos.security;

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
        String currentEmail = authentication.getName();

        return orderRepository.findByOrderId(orderId)
                .map(order -> order.getCustomer().getEmail().equals(currentEmail))
                .orElse(false);
    }
}