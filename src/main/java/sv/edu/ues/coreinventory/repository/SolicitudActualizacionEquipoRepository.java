package sv.edu.ues.coreinventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.SolicitudActualizacionEquipo;

public interface SolicitudActualizacionEquipoRepository extends JpaRepository<SolicitudActualizacionEquipo, Long> {

    @Override
    @EntityGraph(attributePaths = {"solicitud", "solicitud.empresa", "solicitud.actualizacion", "solicitud.actualizacion.software", "solicitud.actualizacion.software.licencia", "solicitud.actualizacion.software.licencia.empresa", "solicitud.actualizacion.software.tipoSoftware", "solicitud.solicitante", "solicitud.solicitante.roles", "solicitud.solicitante.roles.accesos", "solicitud.solicitante.empresas", "solicitud.aprobador", "solicitud.aprobador.roles", "solicitud.aprobador.roles.accesos", "solicitud.aprobador.empresas", "equipo", "equipo.empresa", "equipo.responsable", "equipo.responsable.roles", "equipo.responsable.roles.accesos", "equipo.responsable.empresas"})
    Page<SolicitudActualizacionEquipo> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"solicitud", "solicitud.empresa", "solicitud.actualizacion", "solicitud.actualizacion.software", "solicitud.actualizacion.software.licencia", "solicitud.actualizacion.software.licencia.empresa", "solicitud.actualizacion.software.tipoSoftware", "solicitud.solicitante", "solicitud.solicitante.roles", "solicitud.solicitante.roles.accesos", "solicitud.solicitante.empresas", "solicitud.aprobador", "solicitud.aprobador.roles", "solicitud.aprobador.roles.accesos", "solicitud.aprobador.empresas", "equipo", "equipo.empresa", "equipo.responsable", "equipo.responsable.roles", "equipo.responsable.roles.accesos", "equipo.responsable.empresas"})
    Optional<SolicitudActualizacionEquipo> findById(Long id);
}
