package br.com.louise.AppProdutos.model;

import br.com.louise.AppProdutos.dto.payment.DTOPaymentDetails;
import br.com.louise.AppProdutos.dto.payment.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tbl_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Builder.Default
    private String orderId = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    private String customerName;
    private String phoneNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal grandTotal;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name ="order_id")
    private List<OrderProductEntity> products= new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "status", column = @Column(name = "payment_status_detail"))
    })
    private DTOPaymentDetails paymentDetails;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // --- NOVOS CAMPOS PARA O MÓDULO DE PROMOÇÕES ---

    @Column(precision = 10, scale = 2)
    private BigDecimal discount; // Valor monetário descontado

    private String appliedCoupon; // Código do cupom usado (ex: "NATAL10")
}