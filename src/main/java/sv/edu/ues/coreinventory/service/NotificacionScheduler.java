package sv.edu.ues.coreinventory.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sv.edu.ues.coreinventory.dto.AlertaResumenDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificacionScheduler {

    private final NotificacionService notificacionService;

    /**
     * Ejecuta el escaneo diario de licencias y equipos. El horario y la zona
     * horaria se configuran con app.notificaciones.cron y
     * app.notificaciones.zona-horaria.
     */
    @Scheduled(
            cron = "${app.notificaciones.cron:0 0 8 * * *}",
            zone = "${app.notificaciones.zona-horaria:America/El_Salvador}")
    public void procesarAlertasDiarias() {
        log.info("Iniciando procesamiento diario de alertas por correo");

        try {
            AlertaResumenDto resumen = notificacionService.procesarAlertas();
            log.info("Alertas diarias procesadas: {}", resumen);
        } catch (Exception ex) {
            log.error("Error al procesar las alertas diarias", ex);
        }
    }
}
