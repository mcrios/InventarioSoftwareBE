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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "actualizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Actualizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_actualizacion_gen")
    @SequenceGenerator(name = "seq_actualizacion_gen", sequenceName = "seq_actualizacion", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "software_id", nullable = false)
    private Software software;

    @Column(name = "version_origen", length = 50)
    private String versionOrigen;

    @Column(name = "version_destino", nullable = false, length = 50)
    private String versionDestino;

    @Column(name = "tipo_cambio", nullable = false, length = 50)
    private String tipoCambio;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "estado", length = 10)
    private String estado;

    @Column(name = "fecha_lanzamiento", insertable = false, updatable = false)
    private OffsetDateTime fechaLanzamiento;
}