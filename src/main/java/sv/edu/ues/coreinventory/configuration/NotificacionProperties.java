package sv.edu.ues.coreinventory.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "app.notificaciones")
@Getter
@Setter
public class NotificacionProperties {

    /** Activa o desactiva el envío real de correos. */
    private boolean correoHabilitado = true;

    /** Remitente que aparecerá en los correos enviados. */
    private String remitente = "no-reply@coreinventory.com";

    /** Días de anticipación para avisar que una licencia está por vencer. */
    private int diasAvisoVencimiento = 30;

    /** Días mínimos entre recordatorios iguales para un mismo usuario. */
    private int diasEntreRecordatorios = 7;

    /** Incluye a los usuarios con rol ADMIN como destinatarios de las alertas. */
    private boolean incluirAdministradores = true;

    /** Expresión cron de Spring para la ejecución diaria. */
    private String cron = "0 0 8 * * *";

    /** Zona horaria usada para calcular vencimientos y ejecutar el cron. */
    private String zonaHoraria = "America/El_Salvador";
}
