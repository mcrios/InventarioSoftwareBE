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
import sv.edu.ues.coreinventory.entity.SolicitudActualizacionEquipo;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.SolicitudActualizacionEquipoRepository;

@RestController
@RequestMapping("/api/solicitudes-actualizacion-equipo")
@AllArgsConstructor
public class SolicitudActualizacionEquipoController {

    private final SolicitudActualizacionEquipoRepository solicitudActualizacionEquipoRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<SolicitudActualizacionEquipo>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<SolicitudActualizacionEquipo> items = solicitudActualizacionEquipoRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Asignaciones de equipos a solicitudes obtenidas con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SolicitudActualizacionEquipo>> getById(@PathVariable Long id) {
        SolicitudActualizacionEquipo item = solicitudActualizacionEquipoRepository.findById(id)
                .orElseThrow(() -> new AppException("Asignación de equipo a solicitud no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Asignación de equipo a solicitud obtenida con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<SolicitudActualizacionEquipo>> create(@RequestBody SolicitudActualizacionEquipo item) {
        SolicitudActualizacionEquipo saved = solicitudActualizacionEquipoRepository.save(item);
        SolicitudActualizacionEquipo created = solicitudActualizacionEquipoRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Asignación de equipo a solicitud creada exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SolicitudActualizacionEquipo>> update(@PathVariable Long id, @RequestBody SolicitudActualizacionEquipo item) {
        solicitudActualizacionEquipoRepository.findById(id)
                .orElseThrow(() -> new AppException("Asignación de equipo a solicitud no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        solicitudActualizacionEquipoRepository.save(item);
        SolicitudActualizacionEquipo updated = solicitudActualizacionEquipoRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Asignación de equipo a solicitud actualizada exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<SolicitudActualizacionEquipo>> delete(@PathVariable Long id) {
        solicitudActualizacionEquipoRepository.findById(id)
                .orElseThrow(() -> new AppException("Asignación de equipo a solicitud no encontrada con id: " + id, HttpStatus.NOT_FOUND));
        try {
            solicitudActualizacionEquipoRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar la asignación de equipo a solicitud con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Asignación de equipo a solicitud eliminada exitosamente", null));
    }
}
