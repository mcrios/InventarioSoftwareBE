package sv.edu.ues.coreinventory.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sv.edu.ues.coreinventory.dto.ApiResponse;
import sv.edu.ues.coreinventory.dto.LoginRequest;
import sv.edu.ues.coreinventory.dto.LoginResponse;
import sv.edu.ues.coreinventory.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        
        ApiResponse<LoginResponse> apiResponse = ApiResponse.success("Inicio de sesión exitoso", response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<Object>> verificarRoles(Authentication authentication) {
        // Extraemos la información del usuario autenticado a través del token JWT
        Map<String, Object> details = Map.of(
                "email", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );

        ApiResponse<Object> response = ApiResponse.success("¡Token validado con éxito! Tienes acceso al recurso.", details);
        return ResponseEntity.ok(response);
    }
}