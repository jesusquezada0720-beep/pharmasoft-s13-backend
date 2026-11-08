# PharmaSoft Backend

Backend Spring Boot para el sistema de botica. La actividad de la sesión 13 documenta las mediciones de optimización en [`docs/informe-s13.pdf`](docs/informe-s13.pdf).

## Despliegue en producción

### Arquitectura

```text
Navegador -- HTTPS :443 --> Nginx :80/:8081 -- /api --> PharmaBackend :8080
                                                        |--> Oracle :1521 (host local de desarrollo: 1522)
                                                        `--> Redis :6379
```

En el entorno de desarrollo local, Nginx publica la SPA en `http://localhost:8081`, Oracle se alcanza en `localhost:1522` y Redis en `localhost:6379`. En producción, publicar HTTPS en el proxy y mantener Oracle, Redis y el puerto 8080 fuera del acceso público.

### Variables de entorno

| Variable | Propósito |
| --- | --- |
| `DB_URL` | URL JDBC del servicio Oracle |
| `DB_USERNAME` / `DB_PASSWORD` | Cuenta de aplicación de Oracle |
| `REDIS_HOST` / `REDIS_PORT` | Dirección de Redis |
| `JWT_SECRET` | Clave de firma JWT, secreta y con longitud adecuada |
| `JWT_EXPIRACION_MINUTOS` | Vigencia del JWT |
| `SERVER_PORT` | Puerto HTTP interno del backend (8080 por defecto) |
| `FRONTEND_ORIGINS` | Orígenes permitidos por CORS |

No guardar valores reales en Git, README, capturas ni logs. Inyectarlos desde el gestor de secretos del entorno.

### Construcción y despliegue

1. Revisar la solicitud de incorporación hacia `develop` y ejecutar `./mvnw test` (Windows: `mvnw.cmd test`).
2. Definir las variables anteriores y construir con `./mvnw -DskipTests package`.
3. Desplegar el JAR en el servicio de aplicación, aplicar las migraciones Flyway y comprobar `/actuator/health`.
4. Actualizar la imagen o el artefacto de backend de forma gradual; confirmar login, catálogo y registro de ventas antes de habilitar el tráfico.
5. Para volver atrás, restaurar el artefacto/imagen anterior y ejecutar las migraciones `undo` solo si el cambio las admite; de lo contrario, aplicar una migración correctiva hacia adelante. No restaurar Oracle sin validar un respaldo consistente.

### Estado de prácticas

| Práctica | Estado | Acción pendiente |
| --- | --- | --- |
| JWT sin sesión, autorización por permiso y endpoints de monitoreo protegidos | Cumple | Rotar claves con el gestor de secretos en cada ambiente. |
| Redis para categorías/productos, TTL de 10 minutos e invalidación | Cumple | Alertar por latencia, evicciones y caída de Redis. |
| Request ID en logs y respuestas de error | Cumple tras el cambio de Sesión 13 | Verificar correlación desde el proxy y centralizar logs en producción. |
| Secretos y datos personales fuera de logs | Cumple en el perfil de producción revisado | Mantener una auditoría automatizada al desplegar. |
| Health y métricas Actuator | Parcial | Health es público con detalle limitado; restringir/redactar detalles y alertar por estado DOWN. |
| Alta disponibilidad y respaldo de Oracle | Parcial | Definir respaldos, retención y simulacros de restauración. |

### Recomendaciones priorizadas para tres locales

1. **Prioridad alta:** publicar únicamente por HTTPS y restringir el acceso directo a backend, Oracle y Redis mediante red privada y reglas de firewall.
2. **Prioridad alta:** configurar copias de seguridad de Oracle con retención y ensayar la restauración antes de abrir los tres locales.
3. **Prioridad media:** centralizar backend y Redis en una plataforma compartida con alertas por `/actuator/health`, latencia y errores; mantener Redis como caché descartable, nunca como única fuente de datos.
