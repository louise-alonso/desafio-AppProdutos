package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.model.AuditLogEntity;
import br.com.louise.AppProdutos.repository.AuditRepository;
import br.com.louise.AppProdutos.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void log(String action, String entityName, String entityId, Object oldObj, Object newObj) {
        String currentUser = "SYSTEM";
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            }
        } catch (Exception e) {
            // Ignora erro de contexto
        }

        try {
            String oldJson = oldObj != null ? objectMapper.writeValueAsString(oldObj) : null;
            String newJson = newObj != null ? objectMapper.writeValueAsString(newObj) : null;

            AuditLogEntity log = AuditLogEntity.builder()
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .oldState(oldJson)
                    .newState(newJson)
                    .changedBy(currentUser)
                    .build();

            auditRepository.save(log);
        } catch (Exception e) {
            System.err.println("Erro ao salvar auditoria: " + e.getMessage());
        }
    }

    @Override
    public List<AuditLogEntity> getLogsByEntity(String entityName) {
        return auditRepository.findByEntityNameOrderByChangedAtDesc(entityName);
    }
}