package sv.edu.ues.coreinventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.Licencia;

public interface LicenciaRepository extends JpaRepository<Licencia, Long> {

    @Override
    @EntityGraph(attributePaths = {"empresa"})
    Page<Licencia> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"empresa"})
    Optional<Licencia> findById(Long id);
}
