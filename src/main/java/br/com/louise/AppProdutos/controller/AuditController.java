package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.model.AuditLogEntity;
import br.com.louise.AppProdutos.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogEntity> getAuditLogs(@RequestParam(defaultValue = "Product") String entity) {
        return auditService.getLogsByEntity(entity);
    }
}