package sv.edu.ues.coreinventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import sv.edu.ues.coreinventory.dto.ParcheCreateDto;
import sv.edu.ues.coreinventory.entity.Parche;
import sv.edu.ues.coreinventory.entity.ParcheEstado;
import sv.edu.ues.coreinventory.entity.Software;
import sv.edu.ues.coreinventory.entity.Usuario;
import sv.edu.ues.coreinventory.exception.AppException;
import sv.edu.ues.coreinventory.repository.ParcheRepository;
import sv.edu.ues.coreinventory.repository.SoftwareRepository;
import sv.edu.ues.coreinventory.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class ParcheServiceTest {

    @Mock
    private ParcheRepository parcheRepository;

    @Mock
    private SoftwareRepository softwareRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ParcheService parcheService;

    private Software software;
    private Usuario adminUser;
    private Usuario gerenteUser;

    @BeforeEach
    void setUp() {
        software = Software.builder()
                .id(1L)
                .nombre("TestSoft")
                .versionActual("1.0")
                .build();
        adminUser = new Usuario();
        adminUser.setId(10L);
        gerenteUser = new Usuario();
        gerenteUser.setId(20L);
    }

    @Test
    void testCreateParcheSuccess() {
        ParcheCreateDto dto = new ParcheCreateDto();
        dto.setSoftwareId(software.getId());
        dto.setVersion("2.0");
        dto.setDescripcion("Update");
        dto.setFechaLanzamiento(OffsetDateTime.now());

        when(softwareRepository.findById(software.getId())).thenReturn(Optional.of(software));
        when(usuarioRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(parcheRepository.save(any(Parche.class))).thenAnswer(invocation -> {
            Parche p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        Parche result = parcheService.create(dto, adminUser.getId());
        assertNotNull(result);
        assertEquals(ParcheEstado.PENDIENTE, result.getEstado());
        assertEquals(adminUser, result.getSolicitadoPor());
        assertEquals("2.0", result.getVersion());
    }

    @Test
    void testCreateParcheSoftwareNotFound() {
        ParcheCreateDto dto = new ParcheCreateDto();
        dto.setSoftwareId(999L);
        dto.setVersion("1.1");
        when(softwareRepository.findById(999L)).thenReturn(Optional.empty());
        AppException ex = assertThrows(AppException.class, () -> parcheService.create(dto, null));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void testApproveParcheSuccess() {
        Parche parche = Parche.builder()
                .id(200L)
                .software(software)
                .version("2.0")
                .estado(ParcheEstado.PENDIENTE)
                .build();
        when(parcheRepository.findById(parche.getId())).thenReturn(Optional.of(parche));
        when(usuarioRepository.findById(gerenteUser.getId())).thenReturn(Optional.of(gerenteUser));
        when(softwareRepository.save(any(Software.class))).thenAnswer(i -> i.getArgument(0));
        when(parcheRepository.save(any(Parche.class))).thenAnswer(i -> i.getArgument(0));

        Parche approved = parcheService.approve(parche.getId(), gerenteUser.getId());
        assertEquals(ParcheEstado.APROBADO, approved.getEstado());
        assertEquals(gerenteUser, approved.getAprobadoPor());
        assertEquals("2.0", software.getVersionActual());
    }

    @Test
    void testApproveParcheNotPending() {
        Parche parche = Parche.builder()
                .id(201L)
                .software(software)
                .estado(ParcheEstado.APROBADO)
                .build();
        when(parcheRepository.findById(parche.getId())).thenReturn(Optional.of(parche));
        AppException ex = assertThrows(AppException.class, () -> parcheService.approve(parche.getId(), gerenteUser.getId()));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void testRejectParcheSuccess() {
        Parche parche = Parche.builder()
                .id(300L)
                .software(software)
                .estado(ParcheEstado.PENDIENTE)
                .build();
        when(parcheRepository.findById(parche.getId())).thenReturn(Optional.of(parche));
        when(usuarioRepository.findById(gerenteUser.getId())).thenReturn(Optional.of(gerenteUser));
        when(parcheRepository.save(any(Parche.class))).thenAnswer(i -> i.getArgument(0));

        Parche rejected = parcheService.reject(parche.getId(), gerenteUser.getId());
        assertEquals(ParcheEstado.RECHAZADO, rejected.getEstado());
        assertEquals(gerenteUser, rejected.getAprobadoPor());
    }
}

