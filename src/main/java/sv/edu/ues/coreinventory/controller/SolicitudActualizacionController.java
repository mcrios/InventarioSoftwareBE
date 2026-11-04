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
import sv.edu.ues.coreinventory.entity.SolicitudActualizacion;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.SolicitudActualizacionRepository;

@RestController
@RequestMapping("/api/solicitudes-actualizacion")
@AllArgsConstructor
public class SolicitudActualizacionController {

    private final SolicitudActualizacionRepository solicitudActualizacionRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<SolicitudActualizacion>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<SolicitudActualizacion> items = solicitudActualizacionRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Solicitudes de actualización obtenidas con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SolicitudActualizacion>> getById(@PathVariable Long id) {
        SolicitudActualizacion item = solicitudActualizacionRepository.findById(id)
                .orElseThrow(() -> new AppException("Solicitud de actualización no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Solicitud de actualización obtenida con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<SolicitudActualizacion>> create(@RequestBody SolicitudActualizacion item) {
        SolicitudActualizacion saved = solicitudActualizacionRepository.save(item);
        SolicitudActualizacion created = solicitudActualizacionRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Solicitud de actualización creada exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SolicitudActualizacion>> update(@PathVariable Long id, @RequestBody SolicitudActualizacion item) {
        solicitudActualizacionRepository.findById(id)
                .orElseThrow(() -> new AppException("Solicitud de actualización no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        solicitudActualizacionRepository.save(item);
        SolicitudActualizacion updated = solicitudActualizacionRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Solicitud de actualización modificada exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<SolicitudActualizacion>> delete(@PathVariable Long id) {
        solicitudActualizacionRepository.findById(id)
                .orElseThrow(() -> new AppException("Solicitud de actualización no encontrada con id: " + id, HttpStatus.NOT_FOUND));
        try {
            solicitudActualizacionRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar la solicitud de actualización con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Solicitud de actualización eliminada exitosamente", null));
    }
}
