package sv.edu.ues.coreinventory.service;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sv.edu.ues.coreinventory.configuration.NotificacionProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificacionProperties properties;

    /**
     * Envía un correo HTML. No propaga excepciones para que un fallo del SMTP
     * no interrumpa el resto de alertas.
     *
     * @return {@code true} si el correo se envió correctamente.
     */
    public boolean enviar(String destinatario, String asunto, String titulo, String mensaje, String tipo) {
        if (!StringUtils.hasText(destinatario)) {
            log.warn("No se envió el correo de tipo {}: destinatario vacío", tipo);
            return false;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setFrom(properties.getRemitente());
            helper.setTo(destinatario);
            helper.setSubject("[Core Inventory] " + asunto);
            helper.setText(construirTextoPlano(titulo, mensaje), construirHtml(titulo, mensaje, tipo));

            mailSender.send(mimeMessage);
            log.info("Correo de alerta '{}' enviado a {}", tipo, destinatario);
            return true;
        } catch (Exception ex) {
            log.error("No se pudo enviar el correo de alerta '{}' a {}: {}",
                    tipo, destinatario, ex.getMessage());
            return false;
        }
    }

    private String construirTextoPlano(String titulo, String mensaje) {
        return titulo + System.lineSeparator() + System.lineSeparator()
                + mensaje + System.lineSeparator() + System.lineSeparator()
                + "Core Inventory - Notificaciones automáticas";
    }

    private String construirHtml(String titulo, String mensaje, String tipo) {
        String tituloSeguro = HtmlUtils.htmlEscape(titulo);
        String mensajeSeguro = HtmlUtils.htmlEscape(mensaje).replace("\n", "<br>");
        String tipoSeguro = HtmlUtils.htmlEscape(tipo);

        return """
                <!DOCTYPE html>
                <html lang="es">
                <body style="margin:0;padding:0;background:#f4f5f7;font-family:Arial,Helvetica,sans-serif;color:#1f2937;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f5f7;padding:24px 12px;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="max-width:600px;background:#ffffff;border-radius:12px;overflow:hidden;border:1px solid #e5e7eb;">
                          <tr>
                            <td style="background:#111827;color:#ffffff;padding:20px 28px;">
                              <span style="font-size:18px;font-weight:bold;">Core Inventory</span>
                              <span style="float:right;font-size:12px;opacity:.75;">%s</span>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:28px;">
                              <h1 style="margin:0 0 16px;font-size:20px;line-height:1.3;color:#111827;">%s</h1>
                              <p style="margin:0;font-size:15px;line-height:1.6;color:#374151;">%s</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:16px 28px;background:#f9fafb;border-top:1px solid #e5e7eb;font-size:12px;color:#6b7280;">
                              Este es un mensaje automático del sistema de inventario de software.
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(tipoSeguro, tituloSeguro, mensajeSeguro);
    }
}
