package sv.edu.ues.coreinventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Override
    @EntityGraph(attributePaths = {"usuario", "usuario.roles", "usuario.roles.accesos", "usuario.empresas"})
    Page<Notificacion> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"usuario", "usuario.roles", "usuario.roles.accesos", "usuario.empresas"})
    Optional<Notificacion> findById(Long id);
}
