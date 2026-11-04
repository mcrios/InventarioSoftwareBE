package sv.edu.ues.coreinventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.ues.coreinventory.entity.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

}
