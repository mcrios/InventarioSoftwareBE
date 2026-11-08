package sv.edu.ues.coreinventory.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sv.edu.ues.coreinventory.dto.ParcheCreateDto;
import sv.edu.ues.coreinventory.dto.ParcheDto;
import sv.edu.ues.coreinventory.entity.Parche;
import sv.edu.ues.coreinventory.entity.ParcheEstado;
import sv.edu.ues.coreinventory.entity.Software;
import sv.edu.ues.coreinventory.entity.Usuario;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.ParcheRepository;
import sv.edu.ues.coreinventory.repository.SoftwareRepository;
import sv.edu.ues.coreinventory.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ParcheService {

    private final ParcheRepository parcheRepository;
    private final SoftwareRepository softwareRepository;
    private final UsuarioRepository usuarioRepository;

    /** Retrieve all patches with pagination */
    public Page<Parche> findAll(Pageable pageable) {
        return parcheRepository.findAll(pageable);
    }

    /** Retrieve a single patch */
    public Parche findById(Long id) {
        return parcheRepository.findById(id)
                .orElseThrow(() -> new AppException("Parche no encontrado con id: " + id, HttpStatus.NOT_FOUND));
    }

    /** Create a new patch */
    public Parche create(ParcheCreateDto dto, Long solicitanteId) {
        Software software = softwareRepository.findById(dto.getSoftwareId())
                .orElseThrow(() -> new AppException("Software no encontrado con id: " + dto.getSoftwareId(), HttpStatus.NOT_FOUND));
        Usuario solicitante = null;
        if (solicitanteId != null) {
            solicitante = usuarioRepository.findById(solicitanteId)
                    .orElseThrow(() -> new AppException("Usuario solicitante no encontrado", HttpStatus.NOT_FOUND));
        }
        Parche parche = Parche.builder()
                .software(software)
                .version(dto.getVersion())
                .descripcion(dto.getDescripcion())
                .fechaLanzamiento(dto.getFechaLanzamiento())
                .estado(ParcheEstado.PENDIENTE)
                .solicitadoPor(solicitante)
                .build();
        return parcheRepository.save(parche);
    }

    /** Approve a patch */
    public Parche approve(Long parcheId, Long gerenteId) {
        Parche parche = findById(parcheId);
        if (parche.getEstado() != ParcheEstado.PENDIENTE) {
            throw new AppException("Solo se pueden aprobar parches pendientes", HttpStatus.BAD_REQUEST);
        }
        Usuario gerente = usuarioRepository.findById(gerenteId)
                .orElseThrow(() -> new AppException("Gerente no encontrado", HttpStatus.NOT_FOUND));
        parche.setEstado(ParcheEstado.APROBADO);
        parche.setAprobadoPor(gerente);
        // Actualizar versión del software asociado
        Software software = parche.getSoftware();
        software.setVersionActual(parche.getVersion());
        softwareRepository.save(software);
        return parcheRepository.save(parche);
    }

    /** Reject a patch */
    public Parche reject(Long parcheId, Long gerenteId) {
        Parche parche = findById(parcheId);
        if (parche.getEstado() != ParcheEstado.PENDIENTE) {
            throw new AppException("Solo se pueden rechazar parches pendientes", HttpStatus.BAD_REQUEST);
        }
        Usuario gerente = usuarioRepository.findById(gerenteId)
                .orElseThrow(() -> new AppException("Gerente no encontrado", HttpStatus.NOT_FOUND));
        parche.setEstado(ParcheEstado.RECHAZADO);
        parche.setAprobadoPor(gerente);
        return parcheRepository.save(parche);
    }

    /** Delete a patch */
    public void delete(Long id) {
        Parche parche = findById(id);
        parcheRepository.delete(parche);
    }
}

