package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.model.AuditLogEntity;
import br.com.louise.AppProdutos.repository.AuditRepository;
import br.com.louise.AppProdutos.service.AuditService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    // ObjectMapper que ignora métodos toString() problemáticos
    private final ObjectMapper objectMapper = createSafeObjectMapper();

    private ObjectMapper createSafeObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Configurações para evitar loops
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Ignora métodos toString() durante a serialização
        mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
        mapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, false);

        return mapper;
    }

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
            // Usa serialização segura
            String oldJson = serializeSafely(oldObj);
            String newJson = serializeSafely(newObj);

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
            // Log simplificado para debugging
            e.printStackTrace();
        }
    }

    private String serializeSafely(Object obj) {
        if (obj == null) return null;

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            // Fallback: serializa apenas informações básicas
            return "{\"class\":\"" + obj.getClass().getSimpleName() +
                    "\", \"id\":\"" + getObjectId(obj) + "\"}";
        }
    }

    private String getObjectId(Object obj) {
        try {
            // Tenta obter o ID via reflection
            java.lang.reflect.Method getId = obj.getClass().getMethod("getId");
            Object idValue = getId.invoke(obj);
            return idValue != null ? idValue.toString() : "null";
        } catch (Exception e) {
            return "unknown";
        }
    }

    @Override
    public List<AuditLogEntity> getLogsByEntity(String entityName) {
        return auditRepository.findByEntityNameOrderByChangedAtDesc(entityName);
    }
}