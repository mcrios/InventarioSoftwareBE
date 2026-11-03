package sv.edu.ues.coreinventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitud_actualizacion_equipo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"solicitud_id", "equipo_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudActualizacionEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_solicitud_equipo_gen")
    @SequenceGenerator(name = "seq_solicitud_equipo_gen", sequenceName = "seq_solicitud_equipo", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudActualizacion solicitud;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @Column(name = "estado_ejecucion", nullable = false, length = 50)
    @Builder.Default
    private String estadoEjecucion = "PENDIENTE";

    @Column(name = "log_resultado", columnDefinition = "TEXT")
    private String logResultado;
}