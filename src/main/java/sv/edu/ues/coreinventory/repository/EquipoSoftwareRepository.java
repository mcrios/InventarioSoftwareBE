package sv.edu.ues.coreinventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.EquipoSoftware;

public interface EquipoSoftwareRepository extends JpaRepository<EquipoSoftware, Long> {

    @Override
    @EntityGraph(attributePaths = {"equipo", "equipo.empresa", "equipo.responsable", "equipo.responsable.roles", "equipo.responsable.roles.accesos", "equipo.responsable.empresas", "software", "software.licencia", "software.licencia.empresa", "software.tipoSoftware", "licencia", "licencia.empresa"})
    Page<EquipoSoftware> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"equipo", "equipo.empresa", "equipo.responsable", "equipo.responsable.roles", "equipo.responsable.roles.accesos", "equipo.responsable.empresas", "software", "software.licencia", "software.licencia.empresa", "software.tipoSoftware", "licencia", "licencia.empresa"})
    Optional<EquipoSoftware> findById(Long id);
}
