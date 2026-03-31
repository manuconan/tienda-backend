# Operacion y Configuracion

## 1. Objetivo

Documentar parametros de ejecucion local y lineamientos para promocion a otros entornos.

## 2. Configuracion principal

Archivo: `src/main/resources/application.properties`

Propiedades relevantes:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.jpa.hibernate.ddl-auto`
- `spring.jpa.show-sql`

## 3. Lineamientos por entorno

- Desarrollo: puede usar `ddl-auto=update`.
- Produccion: usar migraciones versionadas (Flyway/Liquibase) y evitar `update`.
- Secretos: inyectar por variables de entorno o gestor de secretos.

## 4. Arranque de aplicacion

```powershell
./mvnw.cmd spring-boot:run
```

## 5. Observabilidad minima recomendada

- Logging estructurado para errores y auditoria de seguridad.
- Correlacion de request (`path`, `timestamp`, `code`) en respuestas de error.

## 6. Checklist de despliegue

- Validar conectividad a BD.
- Verificar endpoints de salud/documentacion.
- Ejecutar pruebas automatizadas antes de publicar.
- Confirmar politicas de seguridad activas.

## 7. Referencias de codigo

- `src/main/resources/application.properties`
- `src/main/java/manuel/tienda/config/ConfigurationParameters.java`
- `src/main/java/manuel/tienda/auth/config/SecurityConfig.java`

