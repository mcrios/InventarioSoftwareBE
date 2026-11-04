package sv.edu.ues.coreinventory.controller;

import java.util.List;

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
import sv.edu.ues.coreinventory.entity.Acceso;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.AccesoRepository;

@RestController
@RequestMapping("/api/accesos")
@AllArgsConstructor
public class AccesoController {

    private final AccesoRepository accesoRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<Acceso>>> getAll() {
        List<Acceso> items = accesoRepository.findAll();

        return ResponseEntity.ok(ApiResponse.success("Accesos obtenidos con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Acceso>> getById(@PathVariable Long id) {
        Acceso item = accesoRepository.findById(id)
                .orElseThrow(() -> new AppException("Acceso no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Acceso obtenido con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Acceso>> create(@RequestBody Acceso item) {
        Acceso saved = accesoRepository.save(item);
        Acceso created = accesoRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Acceso creado exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Acceso>> update(@PathVariable Long id, @RequestBody Acceso item) {
        accesoRepository.findById(id)
                .orElseThrow(() -> new AppException("Acceso no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        accesoRepository.save(item);
        Acceso updated = accesoRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Acceso actualizado exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Acceso>> delete(@PathVariable Long id) {
        accesoRepository.findById(id)
                .orElseThrow(() -> new AppException("Acceso no encontrado con id: " + id, HttpStatus.NOT_FOUND));
        try {
            accesoRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar el acceso con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Acceso eliminado exitosamente", null));
    }
}
