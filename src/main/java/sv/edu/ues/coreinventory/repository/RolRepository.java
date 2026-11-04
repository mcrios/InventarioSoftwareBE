package sv.edu.ues.coreinventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

    @Override
    @EntityGraph(attributePaths = {"accesos"})
    List<Rol> findAll();

    @Override
    @EntityGraph(attributePaths = {"accesos"})
    Optional<Rol> findById(Long id);
}
