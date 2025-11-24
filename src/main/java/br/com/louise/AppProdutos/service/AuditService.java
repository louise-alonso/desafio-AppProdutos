package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.model.AuditLogEntity;
import java.util.List;

public interface AuditService {
    void log(String action, String entityName, String entityId, Object oldObj, Object newObj);
    List<AuditLogEntity> getLogsByEntity(String entityName);
}