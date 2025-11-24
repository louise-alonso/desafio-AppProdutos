package br.com.louise.AppProdutos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_audit_logs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // "CREATE", "UPDATE", "DELETE"

    @Column(nullable = false)
    private String entityName; // Ex: "Product", "Order"

    @Column(nullable = false)
    private String entityId; // O ID do objeto alterado

    // Guardamos o JSON do estado anterior e do novo
    @Column(length = 4000)
    private String oldState;

    @Column(length = 4000)
    private String newState;

    @Column(nullable = false)
    private String changedBy; // Email do usuário que fez a ação

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime changedAt;
}