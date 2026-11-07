package sv.edu.ues.coreinventory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sv.edu.ues.coreinventory.entity.SolicitudActualizacion;
import sv.edu.ues.coreinventory.repository.NotificacionRepository;
import sv.edu.ues.coreinventory.repository.SolicitudActualizacionRepository;

@Service
@RequiredArgsConstructor
@Slf4j 
public class SolicitudActualizacionService {

    private final SolicitudActualizacionRepository solicitudActualizacionRepository;
    private final NotificacionRepository notificacionRepository;

    /**
     * Actualiza una solicitud y, si cambia su estado, genera las notificaciones
     * para los responsables de los equipos afectados.
     * Toda la operación se ejecuta en una sola transacción.
     */
    @Transactional
    public SolicitudActualizacion actualizar(Long id, SolicitudActualizacion item) {
        item.setId(id);
        solicitudActualizacionRepository.save(item);

        SolicitudActualizacion actualizada = solicitudActualizacionRepository.findById(id).orElse(item);

        if (item.getEstado().equals("APROBADA")) {
            int creadas = notificacionRepository.crearNotificacionesPorSolicitud(id);
            log.info("Se generaron {} notificaciones para la solicitud de actualización {}", creadas, id);
        }

        return actualizada;
    }
}
