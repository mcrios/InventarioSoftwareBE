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
import sv.edu.ues.coreinventory.entity.Rol;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.RolRepository;

@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
public class RolController {

    private final RolRepository rolRepository;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<Rol>>> getRols() {
        List<Rol> roles = rolRepository.findAll();

        return ResponseEntity.ok(ApiResponse.success("Roles obtenidos con éxito", roles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Rol>> getRolById(@PathVariable Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new AppException("Rol no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Rol obtenido con éxito", rol));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Rol>> createRol(@RequestBody Rol rol) {
        Rol saved = rolRepository.save(rol);
        Rol created = rolRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Rol creado exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Rol>> updateRol(@PathVariable Long id, @RequestBody Rol rolDto) {
        rolRepository.findById(id)
                .orElseThrow(() -> new AppException("Rol no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        rolDto.setId(id);
        rolRepository.save(rolDto);
        Rol updated = rolRepository.findById(id).orElse(rolDto);

        return ResponseEntity.ok().body(ApiResponse.success("Rol actualizado exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Rol>> deleteRol(@PathVariable Long id) {
        rolRepository.findById(id)
                .orElseThrow(() -> new AppException("Rol no encontrado con id: " + id, HttpStatus.NOT_FOUND));
        try {
            rolRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar el rol con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Rol eliminado exitosamente", null));
    }
}
