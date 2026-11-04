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
import sv.edu.ues.coreinventory.entity.Software;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.SoftwareRepository;

@RestController
@RequestMapping("/api/software")
@AllArgsConstructor
public class SoftwareController {

    private final SoftwareRepository softwareRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<Software>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Software> items = softwareRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Registros de software obtenidos con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Software>> getById(@PathVariable Long id) {
        Software item = softwareRepository.findById(id)
                .orElseThrow(() -> new AppException("Registro de software no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Registro de software obtenido con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Software>> create(@RequestBody Software item) {
        Software saved = softwareRepository.save(item);
        Software created = softwareRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Registro de software creado exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Software>> update(@PathVariable Long id, @RequestBody Software item) {
        softwareRepository.findById(id)
                .orElseThrow(() -> new AppException("Registro de software no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        softwareRepository.save(item);
        Software updated = softwareRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Registro de software actualizado exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Software>> delete(@PathVariable Long id) {
        softwareRepository.findById(id)
                .orElseThrow(() -> new AppException("Registro de software no encontrado con id: " + id, HttpStatus.NOT_FOUND));
        try {
            softwareRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar el registro de software con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Registro de software eliminado exitosamente", null));
    }
}
