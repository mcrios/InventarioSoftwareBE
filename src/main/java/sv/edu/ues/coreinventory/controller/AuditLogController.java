package sv.edu.ues.coreinventory.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import sv.edu.ues.coreinventory.dto.ApiResponse;
import sv.edu.ues.coreinventory.entity.AuditLog;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.AuditLogRepository;

@RestController
@RequestMapping("/api/audit-logs")
@AllArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<AuditLog> items = auditLogRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Registros de auditoría obtenidos con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditLog>> getById(@PathVariable Long id) {
        AuditLog item = auditLogRepository.findById(id)
                .orElseThrow(() -> new AppException("Registro de auditoría no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Registro de auditoría obtenido con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<AuditLog>> create(@RequestBody AuditLog item) {
        AuditLog saved = auditLogRepository.save(item);
        AuditLog created = auditLogRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Registro de auditoría creado exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditLog>> update(@PathVariable Long id, @RequestBody AuditLog item) {
        auditLogRepository.findById(id)
                .orElseThrow(() -> new AppException("Registro de auditoría no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        auditLogRepository.save(item);
        AuditLog updated = auditLogRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Registro de auditoría actualizado exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditLog>> delete(@PathVariable Long id) {
        auditLogRepository.findById(id)
                .orElseThrow(() -> new AppException("Registro de auditoría no encontrado con id: " + id, HttpStatus.NOT_FOUND));
        try {
            auditLogRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar el registro de auditoría con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Registro de auditoría eliminado exitosamente", null));
    }
}
