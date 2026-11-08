package sv.edu.ues.coreinventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "parche")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parche {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_parche_gen")
    @SequenceGenerator(name = "seq_parche_gen", sequenceName = "seq_parche", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "software_id", nullable = false)
    private Software software;

    @Column(name = "version", nullable = false, length = 50)
    private String version;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_lanzamiento")
    private OffsetDateTime fechaLanzamiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private ParcheEstado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitado_por_id")
    private Usuario solicitadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por_id")
    private Usuario aprobadoPor;
}

