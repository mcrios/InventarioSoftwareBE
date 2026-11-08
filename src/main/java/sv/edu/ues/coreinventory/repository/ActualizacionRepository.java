package sv.edu.ues.coreinventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sv.edu.ues.coreinventory.entity.Actualizacion;

public interface ActualizacionRepository extends JpaRepository<Actualizacion, Long> {

    @Override
    @EntityGraph(attributePaths = {"software", "software.licencia", "software.licencia.empresa", "software.tipoSoftware"})
    Page<Actualizacion> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"software", "software.licencia", "software.licencia.empresa", "software.tipoSoftware"})
    Optional<Actualizacion> findById(Long id);

    @Query("SELECT a FROM Actualizacion a JOIN FETCH a.software")
    List<Actualizacion> findAllParaAlertas();
}
