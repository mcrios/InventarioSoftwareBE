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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "equipo_software", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"equipo_id", "software_id"})
})
@Getter
@Setter
@AllArgsConstructor
@Builder
public class EquipoSoftware {

    public EquipoSoftware() {
        this.estadoOperativo = "ACTIVO";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_equipo_software_gen")
    @SequenceGenerator(name = "seq_equipo_software_gen", sequenceName = "seq_equipo_software", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "software_id", nullable = false)
    private Software software;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "licencia_id")
    private Licencia licencia;

    @Column(name = "version_instalada", nullable = false, length = 50)
    private String versionInstalada;

    @Column(name = "estado_operativo", nullable = false, length = 50)
    @Builder.Default
    private String estadoOperativo = "ACTIVO";

    @Column(name = "fecha_instalacion", insertable = false, updatable = false)
    private OffsetDateTime fechaInstalacion;

    @PrePersist
    @PreUpdate
    void aplicarValoresPorDefecto() {
        if (estadoOperativo == null) {
            estadoOperativo = "ACTIVO";
        }
    }
}