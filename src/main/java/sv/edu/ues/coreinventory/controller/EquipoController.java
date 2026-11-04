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
import sv.edu.ues.coreinventory.entity.Equipo;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.EquipoRepository;

@RestController
@RequestMapping("/api/equipos")
@AllArgsConstructor
public class EquipoController {

    private final EquipoRepository equipoRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<Equipo>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Equipo> items = equipoRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Equipos obtenidos con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Equipo>> getById(@PathVariable Long id) {
        Equipo item = equipoRepository.findById(id)
                .orElseThrow(() -> new AppException("Equipo no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Equipo obtenido con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Equipo>> create(@RequestBody Equipo item) {
        Equipo saved = equipoRepository.save(item);
        Equipo created = equipoRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Equipo creado exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Equipo>> update(@PathVariable Long id, @RequestBody Equipo item) {
        equipoRepository.findById(id)
                .orElseThrow(() -> new AppException("Equipo no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        equipoRepository.save(item);
        Equipo updated = equipoRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Equipo actualizado exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Equipo>> delete(@PathVariable Long id) {
        equipoRepository.findById(id)
                .orElseThrow(() -> new AppException("Equipo no encontrado con id: " + id, HttpStatus.NOT_FOUND));
        try {
            equipoRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar el equipo con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Equipo eliminado exitosamente", null));
    }
}
