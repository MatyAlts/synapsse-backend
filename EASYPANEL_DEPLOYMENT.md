# 🚀 Guía de Despliegue en EasyPanel con Docker Compose

Esta guía te ayudará a desplegar tu backend de Spring Boot en EasyPanel usando Docker Compose.

## 📋 ¿Qué es EasyPanel?

EasyPanel es una plataforma moderna para desplegar aplicaciones usando Docker. Ofrece:
- ✅ Interfaz web intuitiva
- ✅ Soporte nativo para Docker Compose
- ✅ SSL automático con Let's Encrypt
- ✅ Monitoreo y logs en tiempo real
- ✅ Backups automáticos

## 🎯 Requisitos Previos

1. Cuenta en tu servidor con EasyPanel instalado
2. Docker instalado localmente (para pruebas)
3. Acceso SSH a tu servidor (opcional)

## 🏗️ Estructura de Archivos

Tu backend incluye:
```
backend/
├── docker-compose.yml          # Para desarrollo local
├── docker-compose.prod.yml     # Para producción en EasyPanel
├── Dockerfile                   # Imagen optimizada multi-stage
├── .dockerignore               # Optimización de build
├── .env.example                # Template de variables de entorno
├── start-docker.bat/.sh        # Scripts para iniciar localmente
└── stop-docker.bat             # Script para detener
```

## 🧪 Paso 1: Prueba Local (Recomendado)

Antes de desplegar, prueba localmente con Docker:

### Windows:

1. **Genera el JWT_SECRET:**
   ```cmd
   cd backend
   powershell -ExecutionPolicy Bypass -File generate-jwt-secret.ps1
   ```
   Copia el resultado.

2. **Configura las variables de entorno:**
   ```cmd
   copy .env.example .env
   notepad .env
   ```
   
   Edita y completa:
   ```env
   DB_PASSWORD=tu_password_segura
   JWT_SECRET=<el generado anteriormente>
   JWT_EXPIRATION=86400000
   ```

3. **Inicia con Docker Compose:**
   ```cmd
   start-docker.bat
   ```
   
   O manualmente:
   ```cmd
   docker-compose build
   docker-compose up -d
   ```

4. **Verifica que funciona:**
   ```cmd
   curl http://localhost:8080/api/products
   ```

5. **Ver logs:**
   ```cmd
   docker-compose logs -f backend
   ```

6. **Detener cuando termines:**
   ```cmd
   stop-docker.bat
   ```

### Linux/Mac:

```bash
cd backend

# Generar JWT_SECRET
./generate-jwt-secret.sh

# Configurar .env
cp .env.example .env
nano .env

# Iniciar
chmod +x start-docker.sh
./start-docker.sh

# Verificar
curl http://localhost:8080/api/products

# Ver logs
docker-compose logs -f backend

# Detener
docker-compose down
```

## 🌐 Paso 2: Desplegar en EasyPanel

### Opción A: Desde el Panel Web (Recomendado)

1. **Accede a EasyPanel:**
   - URL: `https://tu-servidor:3000` (o el puerto configurado)
   - Login con tus credenciales

2. **Crear un Nuevo Proyecto:**
   - Click en **"+ New Project"**
   - Name: `synapsse-backend`
   - Click en **"Create"**

3. **Agregar el Servicio:**
   
   **Método 1: Desde Git (Recomendado)**
   - Click en **"Add Service"** → **"From Git"**
   - Repository URL: `https://github.com/dmampel/synapsse`
   - Branch: `front`
   - Path: `/backend`
   - Build Method: **"Docker Compose"**
   - Compose File: `docker-compose.prod.yml`

   **Método 2: Upload Manual**
   - Click en **"Add Service"** → **"Docker Compose"**
   - Pega el contenido de `docker-compose.prod.yml`

4. **Configurar Variables de Entorno:**
   
   En la sección "Environment Variables", agrega:

   | Variable | Valor | Descripción |
   |----------|-------|-------------|
   | `POSTGRES_DB` | `synapsse` | Nombre de la base de datos |
   | `POSTGRES_USER` | `synapsse` | Usuario de PostgreSQL |
   | `POSTGRES_PASSWORD` | `<password seguro>` | ⚠️ Password de PostgreSQL (SEGURO) |
   | `JWT_SECRET` | `<generado con script>` | ⚠️ Secreto para JWT (genera con script) |
   | `JWT_EXPIRATION` | `86400000` | Expiración del token (24h) |
   | `BACKEND_PORT` | `8080` | Puerto del backend |
   | `JAVA_OPTS` | `-Xmx768m -Xms256m` | Opciones de JVM |

   **⚠️ IMPORTANTE:** 
   - NO uses valores por defecto en producción
   - Genera JWT_SECRET ejecutando: `generate-jwt-secret.ps1`
   - Usa passwords seguras y únicas

5. **Configurar el Dominio:**
   - En "Domains", click en **"Add Domain"**
   - Ingresa: `api.tudominio.com` (o el que prefieras)
   - EasyPanel configurará SSL automáticamente con Let's Encrypt

6. **Deploy:**
   - Click en **"Deploy"**
   - Espera 5-10 minutos para el primer build

7. **Verificar:**
   - Una vez desplegado, visita: `https://api.tudominio.com/api/products`

### Opción B: Desde SSH

Si prefieres usar la terminal:

1. **Conectarse al servidor:**
   ```bash
   ssh user@tu-servidor
   ```

2. **Crear directorio del proyecto:**
   ```bash
   mkdir -p ~/easypanel/projects/synapsse-backend
   cd ~/easypanel/projects/synapsse-backend
   ```

3. **Clonar o copiar archivos:**
   ```bash
   # Opción 1: Clonar desde Git
   git clone https://github.com/dmampel/synapsse.git
   cd synapsse/backend

   # Opción 2: Copiar archivos manualmente con scp
   # Desde tu PC local:
   # scp -r backend/ user@tu-servidor:~/easypanel/projects/synapsse-backend/
   ```

4. **Configurar variables de entorno:**
   ```bash
   cp .env.example .env
   nano .env
   ```
   
   Completa los valores necesarios.

5. **Construir y desplegar:**
   ```bash
   docker-compose -f docker-compose.prod.yml build
   docker-compose -f docker-compose.prod.yml up -d
   ```

6. **Verificar:**
   ```bash
   docker-compose -f docker-compose.prod.yml ps
   docker-compose -f docker-compose.prod.yml logs -f backend
   ```

## 🔧 Paso 3: Configuración Post-Despliegue

### 1. Actualizar CORS en el Backend

Una vez que tengas tu dominio de EasyPanel, actualiza `SecurityConfig.java`:

```java
configuration.setAllowedOriginPatterns(List.of(
    "http://localhost:3000",
    "https://*.vercel.app",
    "https://tu-app.vercel.app",  // Tu app de Vercel
    "https://tudominio.com"        // Tu dominio personalizado
));
```

Commit y push, EasyPanel redesplegará automáticamente.

### 2. Actualizar Frontend en Vercel

En las variables de entorno de Vercel:
```
NEXT_PUBLIC_API_URL=https://api.tudominio.com
```

Redespliega el frontend.

### 3. Configurar Backups (Recomendado)

En EasyPanel, ve a tu proyecto y configura backups automáticos:
- Settings → Backups → Enable
- Frecuencia: Diaria
- Retención: 7 días (o lo que prefieras)

## 📊 Monitoreo y Mantenimiento

### Ver Logs en Tiempo Real

**Desde EasyPanel:**
- Dashboard → Tu Proyecto → Logs

**Desde Terminal:**
```bash
docker-compose -f docker-compose.prod.yml logs -f backend
docker-compose -f docker-compose.prod.yml logs -f postgres
```

### Ver Estado de los Servicios

```bash
docker-compose -f docker-compose.prod.yml ps
```

### Reiniciar un Servicio

**Desde EasyPanel:**
- Dashboard → Tu Servicio → Restart

**Desde Terminal:**
```bash
docker-compose -f docker-compose.prod.yml restart backend
```

### Actualizar el Backend

**Si usas Git en EasyPanel:**
1. Haz push a tu repositorio
2. EasyPanel detectará los cambios y redesplegará

**Manualmente:**
```bash
cd ~/easypanel/projects/synapsse-backend/synapsse/backend
git pull
docker-compose -f docker-compose.prod.yml up -d --build
```

### Ver Uso de Recursos

**Desde EasyPanel:**
- Dashboard → Métricas (CPU, RAM, Disco)

**Desde Terminal:**
```bash
docker stats
```

## 🔒 Seguridad

### 1. Variables de Entorno Sensibles

- ✅ Usa el sistema de secrets de EasyPanel
- ✅ Nunca commitees archivos `.env` al repositorio
- ✅ Genera JWT_SECRET único y seguro
- ✅ Usa passwords fuertes para PostgreSQL

### 2. Red Interna

Los servicios se comunican por una red Docker interna:
- PostgreSQL NO está expuesto externamente
- Solo el backend (puerto 8080) es accesible desde fuera

### 3. SSL/TLS

EasyPanel configura automáticamente SSL con Let's Encrypt:
- Certificados se renuevan automáticamente
- HTTPS forzado por defecto

### 4. Firewall

Asegúrate de que solo los puertos necesarios estén abiertos:
```bash
# Solo si tienes acceso SSH
sudo ufw allow 80/tcp    # HTTP (redirige a HTTPS)
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 3000/tcp  # Panel de EasyPanel (opcional)
```

## 🐛 Solución de Problemas

### Error: "Cannot connect to database"

**Causa:** PostgreSQL no está listo o credenciales incorrectas

**Solución:**
```bash
# Verificar logs de PostgreSQL
docker-compose logs postgres

# Verificar variables de entorno
docker-compose config

# Reiniciar servicios
docker-compose restart
```

### Error: "Build failed"

**Causa:** Problemas con Maven o dependencias

**Solución:**
```bash
# Limpiar y reconstruir
docker-compose build --no-cache backend

# Ver logs detallados
docker-compose up backend
```

### Error: "Port already in use"

**Causa:** El puerto 8080 está ocupado

**Solución:**
```bash
# Ver qué está usando el puerto
netstat -ano | findstr :8080    # Windows
lsof -i :8080                   # Linux/Mac

# Cambiar puerto en docker-compose.yml
ports:
  - "8081:8080"  # Exponer en 8081 en lugar de 8080
```

### Error: "Container keeps restarting"

**Causa:** La aplicación falla al iniciar

**Solución:**
```bash
# Ver logs completos
docker-compose logs --tail=100 backend

# Verificar health check
docker inspect synapsse-backend | grep Health

# Verificar configuración
docker-compose config
```

### Base de Datos se Llenó

**Solución:**
```bash
# Conectarse a PostgreSQL
docker exec -it synapsse-db psql -U synapsse -d synapsse

# Ver tamaño de tablas
\dt+

# Limpiar datos antiguos (con cuidado!)
DELETE FROM orders WHERE created_at < NOW() - INTERVAL '90 days';
```

## 🔄 Comandos Útiles

### Gestión Básica

```bash
# Iniciar todos los servicios
docker-compose up -d

# Iniciar solo el backend (la BD se inicia automáticamente)
docker-compose up -d backend

# Detener todo
docker-compose down

# Detener y eliminar volúmenes (¡CUIDADO! Elimina la BD)
docker-compose down -v

# Reiniciar servicios
docker-compose restart

# Ver logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend
docker-compose logs -f postgres

# Ver estado
docker-compose ps
```

### Gestión de la Base de Datos

```bash
# Conectarse a PostgreSQL
docker exec -it synapsse-db psql -U synapsse -d synapsse

# Backup de la base de datos
docker exec synapsse-db pg_dump -U synapsse synapsse > backup.sql

# Restaurar backup
docker exec -i synapsse-db psql -U synapsse synapsse < backup.sql

# Ver conexiones activas
docker exec -it synapsse-db psql -U synapsse -d synapsse -c "SELECT * FROM pg_stat_activity;"
```

### Debugging

```bash
# Ejecutar comando dentro del contenedor del backend
docker exec -it synapsse-backend /bin/sh

# Ver variables de entorno del contenedor
docker exec synapsse-backend env

# Ver uso de recursos
docker stats

# Ver información detallada de un contenedor
docker inspect synapsse-backend

# Limpiar recursos no usados
docker system prune -a
```

## 📈 Optimización de Rendimiento

### 1. Aumentar Recursos de Java

En `docker-compose.prod.yml`, ajusta `JAVA_OPTS`:

```yaml
environment:
  JAVA_OPTS: -Xmx1024m -Xms512m  # Aumentar RAM si tienes disponible
```

### 2. Pool de Conexiones

Ya configurado en `application-production.yml`:
```yaml
hikari:
  maximum-pool-size: 10
  minimum-idle: 5
```

Ajusta según tu carga.

### 3. Habilitar Compresión

Ya incluido en `application-production.yml`:
```yaml
server:
  compression:
    enabled: true
```

## 📚 Recursos Adicionales

- [Documentación de EasyPanel](https://easypanel.io/docs)
- [Docker Compose Reference](https://docs.docker.com/compose/)
- [Spring Boot con Docker](https://spring.io/guides/gs/spring-boot-docker/)

## ✅ Checklist de Despliegue

- [ ] Dockerfile probado localmente
- [ ] docker-compose.yml funciona en local
- [ ] Variables de entorno configuradas
- [ ] JWT_SECRET generado y seguro
- [ ] Proyecto creado en EasyPanel
- [ ] Servicios desplegados correctamente
- [ ] Dominio configurado y SSL activo
- [ ] CORS actualizado en el backend
- [ ] Frontend actualizado con nueva URL del backend
- [ ] Endpoints de API responden correctamente
- [ ] Autenticación JWT funciona
- [ ] Backups configurados
- [ ] Monitoreo configurado

## 🎉 ¡Listo!

Tu backend está desplegado en EasyPanel con:
- ✅ Backend Spring Boot en Docker
- ✅ PostgreSQL con persistencia de datos
- ✅ SSL automático
- ✅ Reinicio automático en caso de fallas
- ✅ Health checks
- ✅ Logs centralizados

**URL del backend:** `https://api.tudominio.com`

---

Para soporte adicional, consulta la documentación de EasyPanel o el canal de soporte de tu proveedor.
