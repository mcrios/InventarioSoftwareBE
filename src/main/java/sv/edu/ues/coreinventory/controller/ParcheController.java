package sv.edu.ues.coreinventory.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import sv.edu.ues.coreinventory.dto.ApiResponse;
import sv.edu.ues.coreinventory.dto.ParcheCreateDto;
import sv.edu.ues.coreinventory.dto.ParcheDto;
import sv.edu.ues.coreinventory.entity.Parche;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.service.ParcheService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parches")
@AllArgsConstructor
@Slf4j
public class ParcheController {

    private final ParcheService parcheService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<Parche>>> getAll(Pageable pageable) {
        Page<Parche> page = parcheService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Parches obtenidos", page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Parche>> getById(@PathVariable Long id) {
        Parche parche = parcheService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Parche encontrado", parche));
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Parche>> create(@RequestBody ParcheCreateDto dto,
                                                       @RequestHeader("X-User-Id") Long userId) {
        Parche creado = parcheService.create(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Parche creado", creado));
    }

    @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<ApiResponse<Parche>> approve(@PathVariable Long id,
                                                        @RequestHeader("X-User-Id") Long gerenteId) {
        Parche aprobado = parcheService.approve(id, gerenteId);
        return ResponseEntity.ok(ApiResponse.success("Parche aprobado", aprobado));
    }

    @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<ApiResponse<Parche>> reject(@PathVariable Long id,
                                                       @RequestHeader("X-User-Id") Long gerenteId) {
        Parche rechazado = parcheService.reject(id, gerenteId);
        return ResponseEntity.ok(ApiResponse.success("Parche rechazado", rechazado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        parcheService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Parche eliminado", null));
    }
}

