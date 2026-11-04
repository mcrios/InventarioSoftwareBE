package sv.edu.ues.coreinventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    @Override
    @EntityGraph(attributePaths = {"empresa", "responsable", "responsable.roles", "responsable.roles.accesos", "responsable.empresas"})
    Page<Equipo> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"empresa", "responsable", "responsable.roles", "responsable.roles.accesos", "responsable.empresas"})
    Optional<Equipo> findById(Long id);
}
