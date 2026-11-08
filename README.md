# InventarioSoftwareBE

Sistema para llevar control de software dentro de una empresa.

## API REST

El context path de la aplicación es `/coreinventory`, por lo que todos los endpoints
quedan precedidos por `http://localhost:8080/coreinventory`.

Todos los recursos (excepto `/api/auth/**`) requieren el header:

```
Authorization: Bearer <token>
```

### Autenticación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Inicia sesión y devuelve el token JWT |
| GET | `/api/auth/roles` | Verifica el token y los roles del usuario |

### Recursos CRUD

Cada recurso expone el mismo conjunto de operaciones:

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/{recurso}/` | Lista los registros (paginado cuando aplica) |
| GET | `/api/{recurso}/{id}` | Obtiene un registro por id |
| POST | `/api/{recurso}/` | Crea un registro |
| PUT | `/api/{recurso}/{id}` | Actualiza un registro |
| DELETE | `/api/{recurso}/{id}` | Elimina un registro |

### Paginación

Los listados paginados aceptan los parámetros estándar de Spring Data:

| Parámetro | Descripción | Valor por defecto |
|-----------|-------------|-------------------|
| `page` | Número de página (inicia en 0) | `0` |
| `size` | Cantidad de registros por página | `10` |
| `sort` | Campo y dirección, ej: `nombre,asc` | `id,asc` |

Ejemplo:

```
GET /coreinventory/api/equipos/?page=0&size=10&sort=nombre,asc
```

La respuesta incluye el objeto estándar `Page` dentro de `data`:

```json
{
  "success": true,
  "message": "Equipos obtenidos con éxito",
  "data": {
    "content": [],
    "totalElements": 0,
    "totalPages": 0,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": true
  }
}
```

> Los recursos `roles`, `tipos-software` y `accesos` **no** están paginados: su `GET /`
> devuelve la lista completa.

### Recursos disponibles

| Recurso | Ruta | Paginado |
|---------|------|----------|
| Roles | `/api/roles` | No |
| Accesos | `/api/accesos` | No |
| Usuarios | `/api/usuarios` | Sí |
| Empresas | `/api/empresas` | Sí |
| Tipos de software | `/api/tipos-software` | No |
| Licencias | `/api/licencias` | Sí |
| Software | `/api/software` | Sí |
| Equipos | `/api/equipos` | Sí |
| Software instalado en equipos | `/api/equipo-software` | Sí |
| Actualizaciones | `/api/actualizaciones` | Sí |
| Solicitudes de actualización | `/api/solicitudes-actualizacion` | Sí |
| Equipos de una solicitud | `/api/solicitudes-actualizacion-equipo` | Sí |
| Notificaciones | `/api/notificaciones` | Sí |
| Registros de auditoría | `/api/audit-logs` | Sí |

### Notificaciones por correo

El backend ejecuta una tarea programada que revisa el inventario y genera
notificaciones internas + correos para:

- Licencias vencidas.
- Licencias que vencen dentro de `NOTIFICACIONES_DIAS_AVISO` días (30 por defecto).
- Equipos con estado de salud crítico (crítico, falla, error, dañado, avería, vulnerable, etc.).
- Equipos con software instalado en una versión anterior a la última `Actualizacion` publicada.

La tarea corre diariamente a las 08:00 (`America/El_Salvador` por defecto) y no
repite la misma alerta para el mismo usuario durante
`NOTIFICACIONES_DIAS_RECORDATORIO` días. Cada alerta se guarda en la tabla
`notificacion` y, si el correo está habilitado, se envía al destinatario.

También se puede ejecutar manualmente (requiere rol `ADMIN`):

```
POST /coreinventory/api/notificaciones/alertas/procesar
```

Configuración por variables de entorno:

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `MAIL_HOST` | Servidor SMTP | `localhost` |
| `MAIL_PORT` | Puerto SMTP | `1025` |
| `MAIL_USERNAME` | Usuario SMTP | vacío |
| `MAIL_PASSWORD` | Contraseña SMTP | vacío |
| `MAIL_SMTP_AUTH` | Habilita autenticación SMTP | `false` |
| `MAIL_SMTP_STARTTLS` | Habilita STARTTLS | `false` |
| `NOTIFICACIONES_CORREO_HABILITADO` | Activa/desactiva el envío real | `true` |
| `NOTIFICACIONES_REMITENTE` | Remitente de los correos | `no-reply@coreinventory.com` |
| `NOTIFICACIONES_DIAS_AVISO` | Días de anticipación para licencias | `30` |
| `NOTIFICACIONES_DIAS_RECORDATORIO` | Días entre recordatorios iguales | `7` |
| `NOTIFICACIONES_INCLUIR_ADMINS` | Incluye administradores como destinatarios | `true` |
| `NOTIFICACIONES_CRON` | Cron de ejecución | `0 0 8 * * *` |
| `NOTIFICACIONES_ZONA` | Zona horaria del cron | `America/El_Salvador` |

Para desarrollo local se puede usar MailHog o Mailpit escuchando en
`localhost:1025`; con la configuración por defecto los correos se capturan ahí
sin necesidad de credenciales.

### Ejemplo de creación con relaciones

Las relaciones se envían haciendo referencia al id del registro existente:

```json
POST /coreinventory/api/equipos/
{
  "empresa": { "id": 1 },
  "responsable": { "id": 1 },
  "nombre": "PC-01",
  "tipo": "FISICO",
  "ubicacion": "Oficina",
  "fabricante": "Dell",
  "ip": "192.168.1.10",
  "salud": "OK"
}
```

La documentación interactiva está disponible en Swagger UI:

```
http://localhost:8080/coreinventory/swagger-ui.html
```
