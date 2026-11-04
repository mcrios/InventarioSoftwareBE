package sv.edu.ues.coreinventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.TipoSoftware;

public interface TipoSoftwareRepository extends JpaRepository<TipoSoftware, Long> {

}
