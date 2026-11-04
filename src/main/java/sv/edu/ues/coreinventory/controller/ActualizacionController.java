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
import sv.edu.ues.coreinventory.entity.Actualizacion;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.ActualizacionRepository;

@RestController
@RequestMapping("/api/actualizaciones")
@AllArgsConstructor
public class ActualizacionController {

    private final ActualizacionRepository actualizacionRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<Actualizacion>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Actualizacion> items = actualizacionRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Actualizaciones obtenidas con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Actualizacion>> getById(@PathVariable Long id) {
        Actualizacion item = actualizacionRepository.findById(id)
                .orElseThrow(() -> new AppException("Actualización no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Actualización obtenida con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Actualizacion>> create(@RequestBody Actualizacion item) {
        Actualizacion saved = actualizacionRepository.save(item);
        Actualizacion created = actualizacionRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Actualización creada exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Actualizacion>> update(@PathVariable Long id, @RequestBody Actualizacion item) {
        actualizacionRepository.findById(id)
                .orElseThrow(() -> new AppException("Actualización no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        actualizacionRepository.save(item);
        Actualizacion updated = actualizacionRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Actualización modificada exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Actualizacion>> delete(@PathVariable Long id) {
        actualizacionRepository.findById(id)
                .orElseThrow(() -> new AppException("Actualización no encontrada con id: " + id, HttpStatus.NOT_FOUND));
        try {
            actualizacionRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar la actualización con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Actualización eliminada exitosamente", null));
    }
}
