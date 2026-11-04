package sv.edu.ues.coreinventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Override
    @EntityGraph(attributePaths = {"usuario", "usuario.roles", "usuario.roles.accesos", "usuario.empresas"})
    Page<AuditLog> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"usuario", "usuario.roles", "usuario.roles.accesos", "usuario.empresas"})
    Optional<AuditLog> findById(Long id);
}
