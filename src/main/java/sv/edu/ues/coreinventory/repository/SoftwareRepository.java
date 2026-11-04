package sv.edu.ues.coreinventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.Software;

public interface SoftwareRepository extends JpaRepository<Software, Long> {

    @Override
    @EntityGraph(attributePaths = {"licencia", "licencia.empresa", "tipoSoftware"})
    Page<Software> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"licencia", "licencia.empresa", "tipoSoftware"})
    Optional<Software> findById(Long id);
}
