package sv.edu.ues.coreinventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sv.edu.ues.coreinventory.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.accesos WHERE u.email = :email")
    Optional<Usuario> findByEmailWithRolesAndAccesos(@Param("email") String email);
}