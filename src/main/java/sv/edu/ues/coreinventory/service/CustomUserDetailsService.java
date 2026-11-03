package sv.edu.ues.coreinventory.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sv.edu.ues.coreinventory.entity.Usuario;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository.findByEmailWithRolesAndAccesos(email)
                .orElseThrow(() -> new AppException("Usuario no encontrado con el email: " + email, HttpStatus.NOT_FOUND));

        if (!usuario.getActivo()) {
            throw new AppException("El usuario se encuentra inactivo.", HttpStatus.UNAUTHORIZED);
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Mapear Roles
        if (usuario.getRoles() != null) {
            usuario.getRoles().forEach(rol -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
                
                // Mapear Accesos (Permisos detallados de la tabla 'acceso')
                if (rol.getAccesos() != null) {
                    rol.getAccesos().forEach(acceso -> 
                        authorities.add(new SimpleGrantedAuthority(acceso.getCodigo()))
                    );
                }
            });
        }

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getActivo(),
                true, true, true,
                authorities
        );
    }
}