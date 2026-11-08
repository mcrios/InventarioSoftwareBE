package sv.edu.ues.coreinventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sv.edu.ues.coreinventory.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Override
    @EntityGraph(attributePaths = {"roles", "roles.accesos", "empresas"})
    Page<Usuario> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"roles", "roles.accesos", "empresas"})
    Optional<Usuario> findById(Long id);

    @Query("SELECT DISTINCT u FROM Usuario u "
            + "LEFT JOIN FETCH u.roles r "
            + "LEFT JOIN FETCH r.accesos "
            + "LEFT JOIN FETCH u.empresas "
            + "WHERE u.email = :email")
    Optional<Usuario> findByEmailWithRolesAndAccesos(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.empresas e "
            + "WHERE e.id = :empresaId AND u.activo = true")
    List<Usuario> findActivosPorEmpresa(@Param("empresaId") Long empresaId);

    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.roles r "
            + "WHERE r.nombre = :rol AND u.activo = true")
    List<Usuario> findActivosPorRol(@Param("rol") String rol);
}
