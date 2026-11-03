package sv.edu.ues.coreinventory.service;

import lombok.RequiredArgsConstructor;
import sv.edu.ues.coreinventory.dto.LoginRequest;
import sv.edu.ues.coreinventory.dto.LoginResponse;
import sv.edu.ues.coreinventory.entity.Usuario;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

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
        try{
                // Valida email y password usando Argon2 mediante DaoAuthenticationProvider
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
                String jwtToken = jwtService.generateToken(userDetails);

                if(usuario.getIntentosFallidos() > 0){
                        usuario.setIntentosFallidos(0);
                        usuarioRepository.save(usuario);
                }

                return LoginResponse.builder()
                        .token(jwtToken)
                        .email(userDetails.getUsername())
                        .build();
        }catch (BadCredentialsException e){
                // Incrementa el contador de intentos fallidos
                usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
                if(usuario.getIntentosFallidos() + 1 >= 3){
                        usuario.setActivo(false);
                        throw new AppException("Has superado el límite de intentos. Tu cuenta ha sido bloqueada.", HttpStatus.UNAUTHORIZED);
                }
                usuarioRepository.save(usuario);

                throw new AppException("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }
    }
}