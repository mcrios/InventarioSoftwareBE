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
import java.time.LocalDate;

@Entity
@Table(name = "licencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Licencia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_licencia_gen")
    @SequenceGenerator(name = "seq_licencia_gen", sequenceName = "seq_licencia", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "codigo_clave", length = 255)
    private String codigoClave;

    @Column(name = "archivo_url", length = 255)
    private String archivoUrl;

    @Column(name = "cantidad_total", nullable = false)
    @Builder.Default
    private Integer cantidadTotal = 1;

    @Column(name = "cantidad_usada", nullable = false)
    @Builder.Default
    private Integer cantidadUsada = 0;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
}