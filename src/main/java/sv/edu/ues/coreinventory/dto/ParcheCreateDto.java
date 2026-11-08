package sv.edu.ues.coreinventory.dto;

import java.time.OffsetDateTime;

public class ParcheCreateDto {
    private Long softwareId;
    private String version;
    private String descripcion;
    private OffsetDateTime fechaLanzamiento;

    // getters and setters
    public Long getSoftwareId() { return softwareId; }
    public void setSoftwareId(Long softwareId) { this.softwareId = softwareId; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public OffsetDateTime getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(OffsetDateTime fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }
}

