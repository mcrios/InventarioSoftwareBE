-- Migration script to create software table
CREATE TABLE software (
    id BIGSERIAL PRIMARY KEY,
    licencia_id BIGINT NOT NULL,
    tipo_software_id BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    fabricante VARCHAR(100),
    version_actual VARCHAR(50) NOT NULL,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE,
    fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_software_licencia FOREIGN KEY (licencia_id) REFERENCES licencia(id),
    CONSTRAINT fk_software_tipo FOREIGN KEY (tipo_software_id) REFERENCES tipo_software(id)
);

