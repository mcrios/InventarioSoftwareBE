-- Migration script to create parche table
CREATE TABLE parche (
    id BIGSERIAL PRIMARY KEY,
    software_id BIGINT NOT NULL,
    version VARCHAR(50) NOT NULL,
    descripcion TEXT,
    fecha_lanzamiento TIMESTAMP WITH TIME ZONE,
    estado VARCHAR(20) NOT NULL,
    solicitado_por_id BIGINT,
    aprobado_por_id BIGINT,
    CONSTRAINT fk_parche_software FOREIGN KEY (software_id) REFERENCES software(id),
    CONSTRAINT fk_parche_solicitado FOREIGN KEY (solicitado_por_id) REFERENCES usuario(id),
    CONSTRAINT fk_parche_aprobado FOREIGN KEY (aprobado_por_id) REFERENCES usuario(id)
);

