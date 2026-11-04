package sv.edu.ues.coreinventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.SolicitudActualizacion;

public interface SolicitudActualizacionRepository extends JpaRepository<SolicitudActualizacion, Long> {

    @Override
    @EntityGraph(attributePaths = {"empresa", "actualizacion", "actualizacion.software", "actualizacion.software.licencia", "actualizacion.software.licencia.empresa", "actualizacion.software.tipoSoftware", "solicitante", "solicitante.roles", "solicitante.roles.accesos", "solicitante.empresas", "aprobador", "aprobador.roles", "aprobador.roles.accesos", "aprobador.empresas"})
    Page<SolicitudActualizacion> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"empresa", "actualizacion", "actualizacion.software", "actualizacion.software.licencia", "actualizacion.software.licencia.empresa", "actualizacion.software.tipoSoftware", "solicitante", "solicitante.roles", "solicitante.roles.accesos", "solicitante.empresas", "aprobador", "aprobador.roles", "aprobador.roles.accesos", "aprobador.empresas"})
    Optional<SolicitudActualizacion> findById(Long id);
}
