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
import sv.edu.ues.coreinventory.entity.EquipoSoftware;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.EquipoSoftwareRepository;

@RestController
@RequestMapping("/api/equipo-software")
@AllArgsConstructor
public class EquipoSoftwareController {

    private final EquipoSoftwareRepository equipoSoftwareRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<EquipoSoftware>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<EquipoSoftware> items = equipoSoftwareRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Instalaciones de software obtenidas con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipoSoftware>> getById(@PathVariable Long id) {
        EquipoSoftware item = equipoSoftwareRepository.findById(id)
                .orElseThrow(() -> new AppException("Instalación de software no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Instalación de software obtenida con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<EquipoSoftware>> create(@RequestBody EquipoSoftware item) {
        EquipoSoftware saved = equipoSoftwareRepository.save(item);
        EquipoSoftware created = equipoSoftwareRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Instalación de software creada exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipoSoftware>> update(@PathVariable Long id, @RequestBody EquipoSoftware item) {
        equipoSoftwareRepository.findById(id)
                .orElseThrow(() -> new AppException("Instalación de software no encontrada con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        equipoSoftwareRepository.save(item);
        EquipoSoftware updated = equipoSoftwareRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Instalación de software actualizada exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipoSoftware>> delete(@PathVariable Long id) {
        equipoSoftwareRepository.findById(id)
                .orElseThrow(() -> new AppException("Instalación de software no encontrada con id: " + id, HttpStatus.NOT_FOUND));
        try {
            equipoSoftwareRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar la instalación de software con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Instalación de software eliminada exitosamente", null));
    }
}
