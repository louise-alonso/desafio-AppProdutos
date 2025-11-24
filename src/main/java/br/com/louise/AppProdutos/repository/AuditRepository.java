package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.model.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditLogEntity, Long> {
    // Permite ao Admin filtrar logs por entidade (ex: ver só mudanças de Produto)
    List<AuditLogEntity> findByEntityNameOrderByChangedAtDesc(String entityName);
}