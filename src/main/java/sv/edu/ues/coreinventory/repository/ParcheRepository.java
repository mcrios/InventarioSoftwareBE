package sv.edu.ues.coreinventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.ues.coreinventory.entity.Parche;

public interface ParcheRepository extends JpaRepository<Parche, Long> {
    // Additional query methods can be added here if needed
}

