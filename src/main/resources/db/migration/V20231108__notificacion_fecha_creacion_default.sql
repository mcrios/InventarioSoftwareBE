-- Asegura que las notificaciones creadas por SQL nativo (por ejemplo, al
-- aprobar una solicitud de actualización) también tengan fecha de creación.
ALTER TABLE notificacion
    ALTER COLUMN fecha_creacion SET DEFAULT now();

UPDATE notificacion
SET fecha_creacion = now()
WHERE fecha_creacion IS NULL;
