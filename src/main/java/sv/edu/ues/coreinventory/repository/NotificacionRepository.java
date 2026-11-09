package sv.edu.ues.coreinventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sv.edu.ues.coreinventory.entity.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Override
    @EntityGraph(attributePaths = {"usuario", "usuario.roles", "usuario.roles.accesos", "usuario.empresas"})
    Page<Notificacion> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"usuario", "usuario.roles", "usuario.roles.accesos", "usuario.empresas"})
    Optional<Notificacion> findById(Long id);

    /**
     * Genera una notificación para cada usuario responsable de los equipos
     * que tienen instalado el software de la actualización solicitada.
     * Se usa DISTINCT para no duplicar notificaciones cuando un usuario es
     * responsable de varios equipos con el mismo software.
     */
    @Modifying(flushAutomatically = true)
    @Query(value = """
            INSERT INTO notificacion (usuario_id, titulo, mensaje, tipo, leida)
            SELECT DISTINCT
                   u.id,
                   LEFT(a.tipo_cambio || ' ' || s.nombre, 150),
                   COALESCE(a.descripcion, ''),
                   a.tipo_cambio,
                   false
            FROM solicitud_actualizacion sa
            INNER JOIN actualizacion a ON sa.actualizacion_id = a.id
            INNER JOIN equipo_software es ON es.software_id = a.software_id
            INNER JOIN software s ON s.id = es.software_id
            INNER JOIN equipo e ON es.equipo_id = e.id
            INNER JOIN usuario u ON e.responsable_id = u.id
            WHERE sa.id = :solicitudId 
            """, nativeQuery = true)
    int crearNotificacionesPorSolicitud(@Param("solicitudId") Long solicitudId);
}
