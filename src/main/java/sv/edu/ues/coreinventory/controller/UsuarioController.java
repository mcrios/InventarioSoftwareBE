package sv.edu.ues.coreinventory.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import sv.edu.ues.coreinventory.entity.Usuario;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<Usuario>>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Usuario> items = usuarioRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success("Usuarios obtenidos con éxito", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> getById(@PathVariable Long id) {
        Usuario item = usuarioRepository.findById(id)
                .orElseThrow(() -> new AppException("Usuario no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("Usuario obtenido con éxito", item));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Usuario>> create(@RequestBody Usuario item) {
        if (item.getPassword() == null || item.getPassword().isBlank()) {
            throw new AppException("La contraseña es obligatoria", HttpStatus.BAD_REQUEST);
        }
        item.setPassword(passwordEncoder.encode(item.getPassword()));

        Usuario saved = usuarioRepository.save(item);
        Usuario created = usuarioRepository.findById(saved.getId()).orElse(saved);

        return ResponseEntity.ok().body(ApiResponse.success("Usuario creado exitosamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> update(@PathVariable Long id, @RequestBody Usuario item) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new AppException("Usuario no encontrado con id: " + id, HttpStatus.NOT_FOUND));

        item.setId(id);
        if (item.getPassword() == null || item.getPassword().isBlank()) {
            // Si no se envía una nueva contraseña, se conserva la actual
            item.setPassword(existente.getPassword());
        } else {
            item.setPassword(passwordEncoder.encode(item.getPassword()));
        }

        usuarioRepository.save(item);
        Usuario updated = usuarioRepository.findById(id).orElse(item);

        return ResponseEntity.ok().body(ApiResponse.success("Usuario actualizado exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> delete(@PathVariable Long id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new AppException("Usuario no encontrado con id: " + id, HttpStatus.NOT_FOUND));
        try {
            usuarioRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException("Error al eliminar el usuario con id: " + id, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Usuario eliminado exitosamente", null));
    }
}
