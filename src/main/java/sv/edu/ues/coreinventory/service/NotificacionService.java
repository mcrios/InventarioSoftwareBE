package sv.edu.ues.coreinventory.service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sv.edu.ues.coreinventory.configuration.NotificacionProperties;
import sv.edu.ues.coreinventory.dto.AlertaResumenDto;
import sv.edu.ues.coreinventory.entity.Actualizacion;
import sv.edu.ues.coreinventory.entity.Empresa;
import sv.edu.ues.coreinventory.entity.Equipo;
import sv.edu.ues.coreinventory.entity.EquipoSoftware;
import sv.edu.ues.coreinventory.entity.Licencia;
import sv.edu.ues.coreinventory.entity.Notificacion;
import sv.edu.ues.coreinventory.entity.Usuario;
import sv.edu.ues.coreinventory.repository.ActualizacionRepository;
import sv.edu.ues.coreinventory.repository.EquipoRepository;
import sv.edu.ues.coreinventory.repository.EquipoSoftwareRepository;
import sv.edu.ues.coreinventory.repository.LicenciaRepository;
import sv.edu.ues.coreinventory.repository.NotificacionRepository;
import sv.edu.ues.coreinventory.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    public static final String TIPO_LICENCIA_VENCIDA = "LICENCIA_VENCIDA";
    public static final String TIPO_LICENCIA_POR_VENCER = "LICENCIA_POR_VENCER";
    public static final String TIPO_EQUIPO_VULNERABLE = "EQUIPO_VULNERABLE";
    public static final String TIPO_SOFTWARE_DESACTUALIZADO = "SOFTWARE_DESACTUALIZADO";

    private static final String ROL_ADMIN = "ADMIN";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern DIGITOS = Pattern.compile("\\d+");

    private final LicenciaRepository licenciaRepository;
    private final EquipoRepository equipoRepository;
    private final EquipoSoftwareRepository equipoSoftwareRepository;
    private final ActualizacionRepository actualizacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionRepository notificacionRepository;
    private final EmailService emailService;
    private final NotificacionProperties properties;

    /**
     * Ejecuta todas las reglas de alerta y devuelve cuántas notificaciones se
     * crearon por tipo. Cada notificación se guarda en la base de datos y, si
     * el correo está habilitado, se envía al destinatario.
     */
    public AlertaResumenDto procesarAlertas() {
        return procesarAlertas(LocalDate.now(ZoneId.of(properties.getZonaHoraria())));
    }

    public AlertaResumenDto procesarAlertas(LocalDate hoy) {
        int licenciasVencidas = procesarLicenciasVencidas(hoy);
        int licenciasPorVencer = procesarLicenciasPorVencer(hoy);
        int equiposVulnerables = procesarEquiposVulnerables(hoy);
        int softwareDesactualizado = procesarSoftwareDesactualizado(hoy);

        AlertaResumenDto resumen = new AlertaResumenDto(
                licenciasVencidas + licenciasPorVencer + equiposVulnerables + softwareDesactualizado,
                licenciasVencidas,
                licenciasPorVencer,
                equiposVulnerables,
                softwareDesactualizado);

        log.info("Procesamiento de alertas finalizado: {}", resumen);
        return resumen;
    }

    private int procesarLicenciasVencidas(LocalDate hoy) {
        List<Licencia> licencias = licenciaRepository.findByFechaVencimientoBefore(hoy);
        int creadas = 0;

        for (Licencia licencia : licencias) {
            long diasVencida = ChronoUnit.DAYS.between(licencia.getFechaVencimiento(), hoy);
            String titulo = "Licencia vencida #" + licencia.getId() + ": " + nombreEmpresa(licencia.getEmpresa());
            String mensaje = "La licencia #" + licencia.getId() + " de "
                    + nombreEmpresa(licencia.getEmpresa()) + " venció el "
                    + licencia.getFechaVencimiento().format(FORMATO_FECHA) + " (hace "
                    + diasVencida + (diasVencida == 1 ? " día" : " días")
                    + "). Renueva o reemplaza la licencia para evitar interrupciones.";

            creadas += notificar(
                    usuariosDeEmpresa(licencia.getEmpresa()),
                    TIPO_LICENCIA_VENCIDA,
                    titulo,
                    mensaje,
                    hoy);
        }

        return creadas;
    }

    private int procesarLicenciasPorVencer(LocalDate hoy) {
        LocalDate limite = hoy.plusDays(Math.max(0, properties.getDiasAvisoVencimiento()));
        List<Licencia> licencias = licenciaRepository.findByFechaVencimientoBetween(hoy, limite);
        int creadas = 0;

        for (Licencia licencia : licencias) {
            long diasRestantes = ChronoUnit.DAYS.between(hoy, licencia.getFechaVencimiento());
            String cuandoVence = diasRestantes == 0
                    ? "vence hoy"
                    : "vence en " + diasRestantes + (diasRestantes == 1 ? " día" : " días");

            String titulo = "Licencia por vencer #" + licencia.getId() + ": " + nombreEmpresa(licencia.getEmpresa());
            String mensaje = "La licencia #" + licencia.getId() + " de "
                    + nombreEmpresa(licencia.getEmpresa()) + " " + cuandoVence
                    + " (" + licencia.getFechaVencimiento().format(FORMATO_FECHA)
                    + "). Programa la renovación antes de la fecha de vencimiento.";

            creadas += notificar(
                    usuariosDeEmpresa(licencia.getEmpresa()),
                    TIPO_LICENCIA_POR_VENCER,
                    titulo,
                    mensaje,
                    hoy);
        }

        return creadas;
    }

    private int procesarEquiposVulnerables(LocalDate hoy) {
        List<Equipo> equipos = equipoRepository.findConSalud();
        int creadas = 0;

        for (Equipo equipo : equipos) {
            if (!esSaludCritica(equipo.getSalud())) {
                continue;
            }

            String titulo = "Equipo vulnerable #" + equipo.getId() + ": " + equipo.getNombre();
            String mensaje = "El equipo " + equipo.getNombre() + " de "
                    + nombreEmpresa(equipo.getEmpresa()) + " reporta el estado de salud '"
                    + equipo.getSalud() + "'. Requiere revisión o mantenimiento.";

            creadas += notificar(
                    usuariosDeEquipo(equipo),
                    TIPO_EQUIPO_VULNERABLE,
                    titulo,
                    mensaje,
                    hoy);
        }

        return creadas;
    }

    private int procesarSoftwareDesactualizado(LocalDate hoy) {
        Map<Long, String> ultimaVersionPorSoftware = obtenerUltimasVersiones();
        List<EquipoSoftware> instalaciones = equipoSoftwareRepository.findAllParaAlertas();
        int creadas = 0;

        for (EquipoSoftware instalacion : instalaciones) {
            Long softwareId = instalacion.getSoftware().getId();
            String versionDisponible = ultimaVersionPorSoftware.get(softwareId);

            if (versionDisponible == null
                    || compararVersiones(instalacion.getVersionInstalada(), versionDisponible) >= 0) {
                continue;
            }

            Equipo equipo = instalacion.getEquipo();
            String titulo = "Software desactualizado #" + instalacion.getId()
                    + ": " + equipo.getNombre();
            String mensaje = "El equipo " + equipo.getNombre() + " de "
                    + nombreEmpresa(equipo.getEmpresa()) + " tiene instalado "
                    + instalacion.getSoftware().getNombre() + " "
                    + instalacion.getVersionInstalada() + " y la última versión disponible es "
                    + versionDisponible + ". Actualiza el software para cerrar posibles vulnerabilidades.";

            creadas += notificar(
                    usuariosDeEquipo(equipo),
                    TIPO_SOFTWARE_DESACTUALIZADO,
                    titulo,
                    mensaje,
                    hoy);
        }

        return creadas;
    }

    private Map<Long, String> obtenerUltimasVersiones() {
        Map<Long, String> versiones = new LinkedHashMap<>();

        for (Actualizacion actualizacion : actualizacionRepository.findAllParaAlertas()) {
            Long softwareId = actualizacion.getSoftware().getId();

            if (softwareId == null || !StringUtils.hasText(actualizacion.getVersionDestino())) {
                continue;
            }

            String versionActual = versiones.get(softwareId);
            if (versionActual == null
                    || compararVersiones(actualizacion.getVersionDestino(), versionActual) > 0) {
                versiones.put(softwareId, actualizacion.getVersionDestino());
            }
        }

        return versiones;
    }

    private int notificar(Collection<Usuario> destinatarios, String tipo, String titulo,
            String mensaje, LocalDate hoy) {
        int creadas = 0;
        String tituloFinal = truncar(titulo, 150);
        OffsetDateTime inicioVentana = inicioVentana(hoy);

        for (Usuario usuario : destinatarios) {
            if (!esDestinatarioValido(usuario)) {
                continue;
            }

            long existentes = notificacionRepository.countByUsuarioAndTipoAndTituloDesde(
                    usuario.getId(), tipo, tituloFinal, inicioVentana);

            if (existentes > 0) {
                continue;
            }

            Notificacion notificacion = Notificacion.builder()
                    .usuario(usuario)
                    .titulo(tituloFinal)
                    .mensaje(mensaje)
                    .tipo(tipo)
                    .leida(false)
                    .build();

            notificacionRepository.save(notificacion);
            creadas++;

            enviarCorreo(usuario, tituloFinal, mensaje, tipo);
        }

        return creadas;
    }

    private void enviarCorreo(Usuario usuario, String titulo, String mensaje, String tipo) {
        if (!properties.isCorreoHabilitado()) {
            return;
        }

        boolean enviado = emailService.enviar(usuario.getEmail(), titulo, titulo, mensaje, tipo);

        if (!enviado) {
            log.warn("La notificación '{}' se guardó para el usuario {} pero el correo no pudo enviarse",
                    tipo, usuario.getId());
        }
    }

    private Collection<Usuario> usuariosDeEmpresa(Empresa empresa) {
        Map<Long, Usuario> destinatarios = new LinkedHashMap<>();

        if (empresa != null && empresa.getId() != null) {
            agregarUsuarios(destinatarios, usuarioRepository.findActivosPorEmpresa(empresa.getId()));
        }

        agregarAdministradores(destinatarios);
        return destinatarios.values();
    }

    private Collection<Usuario> usuariosDeEquipo(Equipo equipo) {
        Map<Long, Usuario> destinatarios = new LinkedHashMap<>();

        if (equipo.getResponsable() != null) {
            agregarUsuarios(destinatarios, Set.of(equipo.getResponsable()));
        } else if (equipo.getEmpresa() != null && equipo.getEmpresa().getId() != null) {
            agregarUsuarios(destinatarios, usuarioRepository.findActivosPorEmpresa(equipo.getEmpresa().getId()));
        }

        agregarAdministradores(destinatarios);
        return destinatarios.values();
    }

    private void agregarAdministradores(Map<Long, Usuario> destinatarios) {
        if (properties.isIncluirAdministradores()) {
            agregarUsuarios(destinatarios, usuarioRepository.findActivosPorRol(ROL_ADMIN));
        }
    }

    private void agregarUsuarios(Map<Long, Usuario> destinatarios, Collection<Usuario> usuarios) {
        if (usuarios == null) {
            return;
        }

        for (Usuario usuario : usuarios) {
            if (usuario != null && usuario.getId() != null) {
                destinatarios.putIfAbsent(usuario.getId(), usuario);
            }
        }
    }

    private boolean esDestinatarioValido(Usuario usuario) {
        return usuario != null
                && usuario.getId() != null
                && Boolean.TRUE.equals(usuario.getActivo())
                && StringUtils.hasText(usuario.getEmail());
    }

    private OffsetDateTime inicioVentana(LocalDate hoy) {
        int dias = Math.max(1, properties.getDiasEntreRecordatorios());
        return hoy.minusDays(dias)
                .atStartOfDay(ZoneId.of(properties.getZonaHoraria()))
                .toOffsetDateTime();
    }

    static boolean esSaludCritica(String salud) {
        if (!StringUtils.hasText(salud)) {
            return false;
        }

        String valor = normalizar(salud);
        return valor.contains("critic")
                || valor.contains("falla")
                || valor.contains("error")
                || valor.contains("danad")
                || valor.contains("averi")
                || valor.contains("vulnerab")
                || valor.contains("infect")
                || valor.contains("compromet")
                || valor.contains("malo");
    }

    static int compararVersiones(String izquierda, String derecha) {
        int[] versionA = parsearVersion(izquierda);
        int[] versionB = parsearVersion(derecha);
        int longitud = Math.max(versionA.length, versionB.length);

        for (int indice = 0; indice < longitud; indice++) {
            int valorA = indice < versionA.length ? versionA[indice] : 0;
            int valorB = indice < versionB.length ? versionB[indice] : 0;

            if (valorA != valorB) {
                return valorA < valorB ? -1 : 1;
            }
        }

        return 0;
    }

    private static int[] parsearVersion(String version) {
        if (!StringUtils.hasText(version)) {
            return new int[0];
        }

        String limpia = version.trim().replaceFirst("^[vV]", "");

        return Arrays.stream(limpia.split("[.\\-_+\\s]+"))
                .mapToInt(parte -> {
                    Matcher matcher = DIGITOS.matcher(parte);
                    return matcher.find() ? Integer.parseInt(matcher.group()) : 0;
                })
                .toArray();
    }

    private static String normalizar(String valor) {
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    private static String nombreEmpresa(Empresa empresa) {
        return empresa != null && StringUtils.hasText(empresa.getNombre())
                ? empresa.getNombre()
                : "empresa desconocida";
    }

    private static String truncar(String valor, int maximo) {
        if (valor == null || valor.length() <= maximo) {
            return valor;
        }
        return valor.substring(0, maximo);
    }
}
