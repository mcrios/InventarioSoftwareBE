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
import sv.edu.ues.coreinventory.entity.Empresa;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.EmpresaRepository;

@RestController
@RequestMapping("/api/empresas")
@AllArgsConstructor
public class EmpresaController {

    private final EmpresaRepository empresaRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<Empresa>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Empresa> items = empresaRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Empresas obtenidas con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Empresa>> getById(@PathVariable Long id) {
        Empresa item = empresaRepository.findById(id)
                .orElseThrow(() -> new AppException("Empresa no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Empresa obtenida con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Empresa>> create(@RequestBody Empresa item) {
        Empresa saved = empresaRepository.save(item);
        Empresa created = empresaRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Empresa creada exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Empresa>> update(@PathVariable Long id, @RequestBody Empresa item) {
        empresaRepository.findById(id)
                .orElseThrow(() -> new AppException("Empresa no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        empresaRepository.save(item);
        Empresa updated = empresaRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Empresa actualizada exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Empresa>> delete(@PathVariable Long id) {
        empresaRepository.findById(id)
                .orElseThrow(() -> new AppException("Empresa no encontrada con id: " + id, HttpStatus.NOT_FOUND));
        try {
            empresaRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar la empresa con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Empresa eliminada exitosamente", null));
    }
}
