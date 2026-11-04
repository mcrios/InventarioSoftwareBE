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
import sv.edu.ues.coreinventory.entity.TipoSoftware;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.TipoSoftwareRepository;

@RestController
@RequestMapping("/api/tipos-software")
@AllArgsConstructor
public class TipoSoftwareController {

    private final TipoSoftwareRepository tipoSoftwareRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<TipoSoftware>>> getAll() {
        List<TipoSoftware> items = tipoSoftwareRepository.findAll();

        return ResponseEntity.ok(ApiResponse.success("Tipos de software obtenidos con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoSoftware>> getById(@PathVariable Long id) {
        TipoSoftware item = tipoSoftwareRepository.findById(id)
                .orElseThrow(() -> new AppException("Tipo de software no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Tipo de software obtenido con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<TipoSoftware>> create(@RequestBody TipoSoftware item) {
        TipoSoftware saved = tipoSoftwareRepository.save(item);
        TipoSoftware created = tipoSoftwareRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Tipo de software creado exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoSoftware>> update(@PathVariable Long id, @RequestBody TipoSoftware item) {
        tipoSoftwareRepository.findById(id)
                .orElseThrow(() -> new AppException("Tipo de software no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        tipoSoftwareRepository.save(item);
        TipoSoftware updated = tipoSoftwareRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Tipo de software actualizado exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoSoftware>> delete(@PathVariable Long id) {
        tipoSoftwareRepository.findById(id)
                .orElseThrow(() -> new AppException("Tipo de software no encontrado con id: " + id, HttpStatus.NOT_FOUND));
        try {
            tipoSoftwareRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar el tipo de software con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Tipo de software eliminado exitosamente", null));
    }
}
