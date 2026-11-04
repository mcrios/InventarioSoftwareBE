package sv.edu.ues.coreinventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "solicitud_actualizacion")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class SolicitudActualizacion {

    public SolicitudActualizacion() {
        this.estado = "PENDIENTE";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_solicitud_actualizacion_gen")
    @SequenceGenerator(name = "seq_solicitud_actualizacion_gen", sequenceName = "seq_solicitud_actualizacion", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actualizacion_id", nullable = false)
    private Actualizacion actualizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobador_id")
    private Usuario aprobador;

    @Column(name = "estado", nullable = false, length = 50)
    @Builder.Default
    private String estado = "PENDIENTE";

    @Column(name = "formato_despliegue", nullable = false, length = 100)
    private String formatoDespliegue;

    @Column(name = "fecha_inicio_rango", nullable = false)
    private OffsetDateTime fechaInicioRango;

    @Column(name = "fecha_fin_rango", nullable = false)
    private OffsetDateTime fechaFinRango;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    @PrePersist
    @PreUpdate
    void aplicarValoresPorDefecto() {
        if (estado == null) {
            estado = "PENDIENTE";
        }
    }
}