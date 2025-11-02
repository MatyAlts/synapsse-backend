# 🚀 Guía de Despliegue del Backend en Render

Esta guía te llevará paso a paso para desplegar tu backend de Spring Boot en Render.

## 📋 Requisitos Previos

1. Cuenta en [Render.com](https://render.com) (gratuita)
2. Tu código backend debe estar en un repositorio Git (GitHub, GitLab o Bitbucket)
3. Java 17 instalado localmente para pruebas

## 🎯 Método 1: Despliegue Automático desde GitHub (Recomendado)

### Paso 1: Preparar el Repositorio

1. **Verifica que los archivos estén en su lugar:**
   ```
   backend/
   ├── Dockerfile
   ├── render.yaml
   ├── pom.xml
   ├── src/
   │   └── main/
   │       ├── java/
   │       └── resources/
   │           ├── application.yml
   │           └── application-production.yml
   └── .dockerignore
   ```

2. **Commitea y pushea los cambios:**
   ```bash
   git add backend/
   git commit -m "Agregar configuración para despliegue en Render"
   git push origin front
   ```

### Paso 2: Crear Base de Datos en Render

1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Click en **"New +"** → **"PostgreSQL"**
3. Configura la base de datos:
   - **Name**: `synapsse-db`
   - **Database**: `synapsse`
   - **User**: `synapsse` (se genera automáticamente)
   - **Region**: Oregon (u otra región cercana)
   - **Plan**: Free
4. Click en **"Create Database"**
5. **GUARDA** la información:
   - Internal Database URL
   - External Database URL
   - PSQL Command

### Paso 3: Crear Web Service en Render

1. En el Dashboard, click en **"New +"** → **"Web Service"**

2. **Conecta tu repositorio:**
   - Selecciona tu repositorio Git
   - Branch: `front` (o la que uses)
   - Root Directory: `backend`

3. **Configura el servicio:**
   - **Name**: `synapsse-backend`
   - **Region**: Oregon (la misma que la BD)
   - **Branch**: `front`
   - **Root Directory**: `backend`
   - **Runtime**: Java
   - **Build Command**: 
     ```bash
     mvn clean package -DskipTests
     ```
   - **Start Command**: 
     ```bash
     java -jar target/synapsse-backend-0.0.1-SNAPSHOT.jar
     ```
   - **Plan**: Free

4. **Variables de Entorno** (muy importante):
   
   Click en "Advanced" y agrega:

   | Key | Value | Descripción |
   |-----|-------|-------------|
   | `SPRING_PROFILES_ACTIVE` | `production` | Activa el perfil de producción |
   | `DATABASE_URL` | *Copia el Internal Database URL* | URL de conexión a PostgreSQL |
   | `JWT_SECRET` | *Genera un string aleatorio largo* | Secreto para firmar tokens JWT |
   | `JWT_EXPIRATION` | `86400000` | Expiración del token (24h en ms) |
   | `JAVA_OPTS` | `-Xmx512m -Xms256m` | Opciones de JVM |
   | `PORT` | `8080` | Puerto (Render lo configura automático) |

   **Para generar JWT_SECRET** usa:
   ```bash
   # En PowerShell
   [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes((New-Guid).ToString() + (New-Guid).ToString()))
   ```

5. Click en **"Create Web Service"**

### Paso 4: Esperar el Despliegue

- El primer despliegue toma 5-10 minutos
- Render descargará dependencias de Maven
- Compilará el proyecto
- Iniciará la aplicación

Puedes ver los logs en tiempo real en la pestaña "Logs".

### Paso 5: Verificar el Despliegue

Una vez completado, tu backend estará disponible en:
```
https://synapsse-backend.onrender.com
```

**Prueba los endpoints:**
```bash
# Probar productos
curl https://synapsse-backend.onrender.com/api/products

# Health check
curl https://synapsse-backend.onrender.com/actuator/health
```

## 🎯 Método 2: Despliegue Manual con Blueprint

Si prefieres usar el archivo `render.yaml`:

1. Ve a Dashboard → **"New +"** → **"Blueprint"**
2. Conecta tu repositorio
3. Render detectará automáticamente `backend/render.yaml`
4. Revisa la configuración y haz click en **"Apply"**

## 🔧 Configuración Post-Despliegue

### 1. Actualizar Frontend en Vercel

En las variables de entorno de Vercel, actualiza:
```
NEXT_PUBLIC_API_URL=https://synapsse-backend.onrender.com
```

Luego redespliega el frontend.

### 2. Configurar Dominios (Opcional)

En Render, puedes agregar un dominio personalizado:
1. Settings → Custom Domain
2. Agrega tu dominio: `api.tudominio.com`
3. Configura el registro CNAME en tu proveedor de DNS

### 3. Actualizar CORS

Si usas un dominio personalizado, actualiza `SecurityConfig.java`:
```java
configuration.setAllowedOriginPatterns(List.of(
    "http://localhost:3000",
    "https://*.vercel.app",
    "https://tu-app.vercel.app", // Tu app específica
    "https://tudominio.com"       // Tu dominio personalizado
));
```

## 📊 Monitoreo y Mantenimiento

### Ver Logs en Tiempo Real
```
Dashboard → Tu Servicio → Logs
```

### Métricas
Render Free tier incluye:
- CPU usage
- Memory usage
- Request rates
- Response times

### Health Checks
Render automáticamente hace health checks al endpoint raíz.

Para configurar un endpoint custom:
Settings → Health Check Path: `/api/products`

## ⚠️ Limitaciones del Plan Free

- **Sleep después de 15 min de inactividad**
  - Primera request puede tomar 30-60 segundos
  - Considera usar un servicio de "keep-alive" o upgrade a plan pagado

- **750 horas/mes de runtime**
  - Suficiente para desarrollo/demo
  - No apto para producción seria

- **PostgreSQL Free**
  - 256 MB de storage
  - 97 conexiones máximo
  - Backup automático por 7 días

## 🔄 Actualizaciones Automáticas

Render redesplegará automáticamente cuando:
- Hagas push a la rama configurada
- Cambies las variables de entorno
- Actualices la configuración del servicio

Para desactivar auto-deploy:
Settings → Auto-Deploy: OFF

## 🐛 Solución de Problemas

### Error: "Build Failed"

**Causa**: Maven no puede compilar
**Solución**:
1. Verifica que `pom.xml` esté correcto
2. Revisa los logs de build
3. Prueba localmente: `mvn clean package`

### Error: "Application failed to start"

**Causa**: Problema con variables de entorno o BD
**Solución**:
1. Verifica `DATABASE_URL` en variables de entorno
2. Asegúrate de que la BD esté corriendo
3. Revisa los logs de la aplicación

### Error: "Connection timeout"

**Causa**: La aplicación está en sleep (plan free)
**Solución**:
- Espera 30-60 segundos en la primera request
- Considera upgrade a plan pagado
- Usa un servicio de keep-alive

### Error: "CORS Policy"

**Causa**: Dominio no permitido en CORS
**Solución**:
1. Actualiza `SecurityConfig.java` con el dominio correcto
2. Usa `allowedOriginPatterns` con wildcard: `https://*.vercel.app`
3. Redespliega

### Error: "Out of Memory"

**Causa**: JVM usa demasiada memoria
**Solución**:
1. Ajusta `JAVA_OPTS`: `-Xmx450m -Xms200m`
2. Optimiza queries y caché
3. Considera upgrade a plan pagado

## 📱 Comandos Útiles

### Conectarse a la Base de Datos
```bash
# Usando el PSQL Command de Render
psql -h oregon-postgres.render.com -U synapsse synapsse
```

### Ver Logs Recientes
```bash
# Instalar Render CLI
npm install -g @render/cli

# Login
render login

# Ver logs
render logs
```

### Hacer Rollback
1. Dashboard → Deployments
2. Encuentra el deployment anterior exitoso
3. Click en "Redeploy"

## 🚀 Upgrade a Plan Pagado

Si tu app crece, considera:

**Starter ($7/mes por servicio)**
- Sin sleep automático
- Más CPU y RAM
- Mejor para producción

**Standard ($25/mes por servicio)**
- 2GB RAM
- Mejor rendimiento
- Ideal para apps en producción

## 📚 Recursos Adicionales

- [Documentación de Render](https://render.com/docs)
- [Spring Boot en Render](https://render.com/docs/deploy-spring-boot)
- [PostgreSQL en Render](https://render.com/docs/databases)
- [Variables de Entorno](https://render.com/docs/environment-variables)

## ✅ Checklist Final

Antes de considerar el despliegue completo:

- [ ] Base de datos PostgreSQL creada
- [ ] Web Service configurado y corriendo
- [ ] Variables de entorno configuradas
- [ ] Endpoints de API responden correctamente
- [ ] CORS configurado para tu dominio de Vercel
- [ ] Frontend actualizado con la URL del backend
- [ ] Autenticación JWT funciona correctamente
- [ ] Productos se cargan correctamente
- [ ] Health checks pasan

---

## 🎉 ¡Listo!

Tu backend está desplegado en Render. Ahora tu aplicación full-stack está en producción:

- **Frontend**: Vercel
- **Backend**: Render
- **Base de Datos**: Render PostgreSQL

Para soporte, consulta la [documentación de Render](https://render.com/docs) o su [Discord community](https://render.com/discord).
