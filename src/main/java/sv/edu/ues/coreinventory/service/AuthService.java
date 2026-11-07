package sv.edu.ues.coreinventory.service;

import lombok.RequiredArgsConstructor;
import sv.edu.ues.coreinventory.dto.LoginRequest;
import sv.edu.ues.coreinventory.dto.LoginResponse;
import sv.edu.ues.coreinventory.entity.Acceso;
import sv.edu.ues.coreinventory.entity.Rol;
import sv.edu.ues.coreinventory.entity.Usuario;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.UsuarioRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByEmailWithRolesAndAccesos(request.getEmail())
                .orElseThrow(() -> new AppException("El usuario no existe", HttpStatus.UNAUTHORIZED));
        if(!usuario.getActivo()){
            throw new AppException("Tu usuario se encuentra bloqueado.",
                        HttpStatus.UNAUTHORIZED);
        }
        try {
            // Valida email y password usando Argon2 mediante DaoAuthenticationProvider
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            if (usuario.getIntentosFallidos() > 0) {
                usuario.setIntentosFallidos(0);
                usuarioRepository.save(usuario);
            }

            return construirRespuesta(usuario);
        } catch (BadCredentialsException e) {
            // Incrementa el contador de intentos fallidos
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            if (usuario.getIntentosFallidos() + 1 >= 3) {
                usuario.setActivo(false);
                usuarioRepository.save(usuario);
                throw new AppException("Has superado el límite de intentos. Tu cuenta ha sido bloqueada.",HttpStatus.UNAUTHORIZED);
            }
            usuarioRepository.save(usuario);

            throw new AppException("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Renueva el access token a partir de un refresh token válido.
     * Los roles, accesos y empresas se vuelven a consultar en base de datos,
     * de modo que cualquier cambio de permisos se refleje en el nuevo token.
     */
    public LoginResponse refreshToken(String refreshToken) {
        try {
            String email = jwtService.extractUsername(refreshToken);

            Usuario usuario = usuarioRepository.findByEmailWithRolesAndAccesos(email)
                    .orElseThrow(() -> new AppException("El usuario no existe", HttpStatus.UNAUTHORIZED));

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
                throw new AppException("El token proporcionado no es un refresh token válido", HttpStatus.UNAUTHORIZED);
            }

            return construirRespuesta(usuario);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AppException("Refresh token inválido o expirado", HttpStatus.UNAUTHORIZED);
        }
    }

    private LoginResponse construirRespuesta(Usuario usuario) {
        List<Map<String, String>> accesos = usuario.getRoles().stream()
            .flatMap(rol -> rol.getAccesos().stream())
            .collect(Collectors.toMap(
                Acceso::getId, 
                acceso -> Map.of(
                    "path", acceso.getPath(),
                    "descripcion", acceso.getDescripcion()
                ),
                (existente, nuevo) -> existente
            )).values().stream().toList();
        
        return LoginResponse.builder()
                .token(jwtService.generateAccessToken(usuario))
                .refreshToken(jwtService.generateRefreshToken(usuario))
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationSeconds())
                .refreshExpiresIn(jwtService.getRefreshTokenExpirationSeconds())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .roles(usuario.getRoles().stream().map(Rol::getNombre).toList())
                .accesos(accesos)
                .empresas(usuario.getEmpresas().stream().toList())
                .build();
    }
}
