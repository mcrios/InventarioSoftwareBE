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
import sv.edu.ues.coreinventory.entity.Licencia;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.LicenciaRepository;

@RestController
@RequestMapping("/api/licencias")
@AllArgsConstructor
public class LicenciaController {

    private final LicenciaRepository licenciaRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<Licencia>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Licencia> items = licenciaRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Licencias obtenidas con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Licencia>> getById(@PathVariable Long id) {
        Licencia item = licenciaRepository.findById(id)
                .orElseThrow(() -> new AppException("Licencia no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Licencia obtenida con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Licencia>> create(@RequestBody Licencia item) {
        Licencia saved = licenciaRepository.save(item);
        Licencia created = licenciaRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Licencia creada exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Licencia>> update(@PathVariable Long id, @RequestBody Licencia item) {
        licenciaRepository.findById(id)
                .orElseThrow(() -> new AppException("Licencia no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        licenciaRepository.save(item);
        Licencia updated = licenciaRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Licencia actualizada exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Licencia>> delete(@PathVariable Long id) {
        licenciaRepository.findById(id)
                .orElseThrow(() -> new AppException("Licencia no encontrada con id: " + id, HttpStatus.NOT_FOUND));
        try {
            licenciaRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar la licencia con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Licencia eliminada exitosamente", null));
    }
}
