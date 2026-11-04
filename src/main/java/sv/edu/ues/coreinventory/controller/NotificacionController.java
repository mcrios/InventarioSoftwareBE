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
import sv.edu.ues.coreinventory.entity.Notificacion;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.NotificacionRepository;

@RestController
@RequestMapping("/api/notificaciones")
@AllArgsConstructor
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<Notificacion>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Notificacion> items = notificacionRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Notificaciones obtenidas con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Notificacion>> getById(@PathVariable Long id) {
        Notificacion item = notificacionRepository.findById(id)
                .orElseThrow(() -> new AppException("Notificación no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Notificación obtenida con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Notificacion>> create(@RequestBody Notificacion item) {
        Notificacion saved = notificacionRepository.save(item);
        Notificacion created = notificacionRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Notificación creada exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Notificacion>> update(@PathVariable Long id, @RequestBody Notificacion item) {
        notificacionRepository.findById(id)
                .orElseThrow(() -> new AppException("Notificación no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        notificacionRepository.save(item);
        Notificacion updated = notificacionRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Notificación actualizada exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Notificacion>> delete(@PathVariable Long id) {
        notificacionRepository.findById(id)
                .orElseThrow(() -> new AppException("Notificación no encontrada con id: " + id, HttpStatus.NOT_FOUND));
        try {
            notificacionRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar la notificación con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Notificación eliminada exitosamente", null));
    }
}
