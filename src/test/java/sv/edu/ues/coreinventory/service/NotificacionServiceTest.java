package sv.edu.ues.coreinventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.coreinventory.configuration.NotificacionProperties;
import sv.edu.ues.coreinventory.dto.AlertaResumenDto;
import sv.edu.ues.coreinventory.entity.Actualizacion;
import sv.edu.ues.coreinventory.entity.Empresa;
import sv.edu.ues.coreinventory.entity.Equipo;
import sv.edu.ues.coreinventory.entity.EquipoSoftware;
import sv.edu.ues.coreinventory.entity.Licencia;
import sv.edu.ues.coreinventory.entity.Notificacion;
import sv.edu.ues.coreinventory.entity.Software;
import sv.edu.ues.coreinventory.entity.Usuario;
import sv.edu.ues.coreinventory.repository.ActualizacionRepository;
import sv.edu.ues.coreinventory.repository.EquipoRepository;
import sv.edu.ues.coreinventory.repository.EquipoSoftwareRepository;
import sv.edu.ues.coreinventory.repository.LicenciaRepository;
import sv.edu.ues.coreinventory.repository.NotificacionRepository;
import sv.edu.ues.coreinventory.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 8);

    @Mock
    private LicenciaRepository licenciaRepository;
    @Mock
    private EquipoRepository equipoRepository;
    @Mock
    private EquipoSoftwareRepository equipoSoftwareRepository;
    @Mock
    private ActualizacionRepository actualizacionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private NotificacionRepository notificacionRepository;
    @Mock
    private EmailService emailService;

    private NotificacionProperties properties;
    private NotificacionService notificacionService;

    private Empresa empresa;
    private Usuario responsable;

    @BeforeEach
    void setUp() {
        properties = new NotificacionProperties();
        properties.setCorreoHabilitado(true);
        properties.setDiasAvisoVencimiento(30);
        properties.setDiasEntreRecordatorios(7);
        properties.setIncluirAdministradores(true);
        properties.setZonaHoraria("America/El_Salvador");

        notificacionService = new NotificacionService(
                licenciaRepository,
                equipoRepository,
                equipoSoftwareRepository,
                actualizacionRepository,
                usuarioRepository,
                notificacionRepository,
                emailService,
                properties);

        empresa = Empresa.builder().id(1L).nombre("Acme").build();
        responsable = Usuario.builder()
                .id(5L)
                .nombre("Ana Responsable")
                .email("ana@acme.com")
                .activo(true)
                .build();

        lenient().when(licenciaRepository.findByFechaVencimientoBefore(any(LocalDate.class)))
                .thenReturn(List.of());
        lenient().when(licenciaRepository.findByFechaVencimientoBetween(
                any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());
        lenient().when(equipoRepository.findConSalud()).thenReturn(List.of());
        lenient().when(equipoSoftwareRepository.findAllParaAlertas()).thenReturn(List.of());
        lenient().when(actualizacionRepository.findAllParaAlertas()).thenReturn(List.of());
        lenient().when(usuarioRepository.findActivosPorRol(anyString())).thenReturn(List.of());
        lenient().when(notificacionRepository.countByUsuarioAndTipoAndTituloDesde(
                anyLong(), anyString(), anyString(), any(OffsetDateTime.class))).thenReturn(0L);
        lenient().when(emailService.enviar(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);
    }

    @Test
    void notificaUnaLicenciaVencida() {
        Licencia licencia = licenciaVencida();
        when(licenciaRepository.findByFechaVencimientoBefore(HOY)).thenReturn(List.of(licencia));
        when(usuarioRepository.findActivosPorEmpresa(1L)).thenReturn(List.of(responsable));

        AlertaResumenDto resumen = notificacionService.procesarAlertas(HOY);

        assertEquals(1, resumen.total());
        assertEquals(1, resumen.licenciasVencidas());

        Notificacion notificacion = capturarNotificacionUnica();
        assertEquals(NotificacionService.TIPO_LICENCIA_VENCIDA, notificacion.getTipo());
        assertTrue(notificacion.getTitulo().contains("#10"));
        assertTrue(notificacion.getTitulo().contains("Acme"));
        assertTrue(notificacion.getMensaje().contains("venció"));

        verify(emailService).enviar(
                anyString(), anyString(), anyString(), anyString(),
                org.mockito.ArgumentMatchers.eq(NotificacionService.TIPO_LICENCIA_VENCIDA));
    }

    @Test
    void notificaUnaLicenciaPorVencer() {
        Licencia licencia = Licencia.builder()
                .id(11L)
                .empresa(empresa)
                .fechaVencimiento(HOY.plusDays(5))
                .build();
        when(licenciaRepository.findByFechaVencimientoBetween(HOY, HOY.plusDays(30)))
                .thenReturn(List.of(licencia));
        when(usuarioRepository.findActivosPorEmpresa(1L)).thenReturn(List.of(responsable));

        AlertaResumenDto resumen = notificacionService.procesarAlertas(HOY);

        assertEquals(1, resumen.licenciasPorVencer());

        Notificacion notificacion = capturarNotificacionUnica();
        assertEquals(NotificacionService.TIPO_LICENCIA_POR_VENCER, notificacion.getTipo());
        assertTrue(notificacion.getMensaje().contains("vence en 5 días"));
    }

    @Test
    void notificaUnEquipoConSaludCritica() {
        Equipo equipo = Equipo.builder()
                .id(2L)
                .empresa(empresa)
                .responsable(responsable)
                .nombre("PC-01")
                .salud("Crítico")
                .build();
        when(equipoRepository.findConSalud()).thenReturn(List.of(equipo));

        AlertaResumenDto resumen = notificacionService.procesarAlertas(HOY);

        assertEquals(1, resumen.equiposVulnerables());

        Notificacion notificacion = capturarNotificacionUnica();
        assertEquals(NotificacionService.TIPO_EQUIPO_VULNERABLE, notificacion.getTipo());
        assertTrue(notificacion.getMensaje().contains("Crítico"));
    }

    @Test
    void notificaSoftwareDesactualizadoComoPosibleVulnerabilidad() {
        Software software = Software.builder().id(3L).nombre("Office").build();
        Equipo equipo = Equipo.builder()
                .id(2L)
                .empresa(empresa)
                .responsable(responsable)
                .nombre("PC-01")
                .build();
        Actualizacion actualizacion = Actualizacion.builder()
                .id(1L)
                .software(software)
                .versionDestino("2.0.0")
                .build();
        EquipoSoftware instalacion = EquipoSoftware.builder()
                .id(7L)
                .equipo(equipo)
                .software(software)
                .versionInstalada("1.5.0")
                .build();

        when(actualizacionRepository.findAllParaAlertas()).thenReturn(List.of(actualizacion));
        when(equipoSoftwareRepository.findAllParaAlertas()).thenReturn(List.of(instalacion));

        AlertaResumenDto resumen = notificacionService.procesarAlertas(HOY);

        assertEquals(1, resumen.softwareDesactualizado());

        Notificacion notificacion = capturarNotificacionUnica();
        assertEquals(NotificacionService.TIPO_SOFTWARE_DESACTUALIZADO, notificacion.getTipo());
        assertTrue(notificacion.getMensaje().contains("2.0.0"));
        assertTrue(notificacion.getMensaje().contains("1.5.0"));
    }

    @Test
    void noDuplicaUnaAlertaYaRegistradaEnLaVentana() {
        when(licenciaRepository.findByFechaVencimientoBefore(HOY)).thenReturn(List.of(licenciaVencida()));
        when(usuarioRepository.findActivosPorEmpresa(1L)).thenReturn(List.of(responsable));
        when(notificacionRepository.countByUsuarioAndTipoAndTituloDesde(
                anyLong(), anyString(), anyString(), any(OffsetDateTime.class))).thenReturn(1L);

        AlertaResumenDto resumen = notificacionService.procesarAlertas(HOY);

        assertEquals(0, resumen.total());
        verify(notificacionRepository, never()).save(any());
        verifyNoInteractions(emailService);
    }

    @Test
    void creaLaNotificacionPeroNoEnviaCorreoSiEstaDeshabilitado() {
        properties.setCorreoHabilitado(false);
        when(licenciaRepository.findByFechaVencimientoBefore(HOY)).thenReturn(List.of(licenciaVencida()));
        when(usuarioRepository.findActivosPorEmpresa(1L)).thenReturn(List.of(responsable));

        AlertaResumenDto resumen = notificacionService.procesarAlertas(HOY);

        assertEquals(1, resumen.total());
        verify(notificacionRepository).save(any(Notificacion.class));
        verifyNoInteractions(emailService);
    }

    @Test
    void reconoceEstadosDeSaludCriticos() {
        assertTrue(NotificacionService.esSaludCritica("Crítico"));
        assertTrue(NotificacionService.esSaludCritica("Falla de disco"));
        assertTrue(NotificacionService.esSaludCritica("Equipo vulnerable"));
        assertFalse(NotificacionService.esSaludCritica("En mantenimiento"));
        assertFalse(NotificacionService.esSaludCritica("Óptimo"));
        assertFalse(NotificacionService.esSaludCritica(null));
    }

    @Test
    void comparaVersionesNumericasCorrectamente() {
        assertTrue(NotificacionService.compararVersiones("1.10.0", "1.9.9") > 0);
        assertTrue(NotificacionService.compararVersiones("v2.0", "2.0.1") < 0);
        assertEquals(0, NotificacionService.compararVersiones("1.0", "1.0.0"));
        assertTrue(NotificacionService.compararVersiones("", "1.0") < 0);
    }

    private Licencia licenciaVencida() {
        return Licencia.builder()
                .id(10L)
                .empresa(empresa)
                .fechaVencimiento(HOY.minusDays(2))
                .build();
    }

    private Notificacion capturarNotificacionUnica() {
        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository).save(captor.capture());
        return captor.getValue();
    }
}
