package sv.edu.ues.coreinventory.dto;

import java.time.OffsetDateTime;

import sv.edu.ues.coreinventory.entity.ParcheEstado;

public class ParcheDto {
    private Long id;
    private Long softwareId;
    private String version;
    private String descripcion;
    private OffsetDateTime fechaLanzamiento;
    private ParcheEstado estado;
    private Long solicitadoPorId;
    private Long aprobadoPorId;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSoftwareId() { return softwareId; }
    public void setSoftwareId(Long softwareId) { this.softwareId = softwareId; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public OffsetDateTime getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(OffsetDateTime fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }
    public ParcheEstado getEstado() { return estado; }
    public void setEstado(ParcheEstado estado) { this.estado = estado; }
    public Long getSolicitadoPorId() { return solicitadoPorId; }
    public void setSolicitadoPorId(Long solicitadoPorId) { this.solicitadoPorId = solicitadoPorId; }
    public Long getAprobadoPorId() { return aprobadoPorId; }
    public void setAprobadoPorId(Long aprobadoPorId) { this.aprobadoPorId = aprobadoPorId; }
}

