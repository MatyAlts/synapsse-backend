# 🔧 Synapsse Backend

Backend API REST construida con Spring Boot 3 y PostgreSQL.

## 🎯 Características

- ✅ Autenticación JWT
- ✅ CRUD de productos
- ✅ Panel de administración
- ✅ Búsqueda de productos
- ✅ PostgreSQL con JPA/Hibernate
- ✅ CORS configurado
- ✅ Docker & Docker Compose
- ✅ Profiles de Spring (dev, production)

## 🚀 Quick Start

### Opción 1: Docker Compose (Recomendado)

```bash
# 1. Generar JWT Secret
powershell -ExecutionPolicy Bypass -File generate-jwt-secret.ps1

# 2. Configurar variables
cp .env.example .env
# Editar .env con tus valores

# 3. Iniciar
start-docker.bat

# 4. Verificar
curl http://localhost:8080/api/products
```

📖 [Guía detallada](QUICKSTART.md)

### Opción 2: Maven (Desarrollo)

```bash
# 1. Crear base de datos PostgreSQL
# (Ver sección "Base de Datos" abajo)

# 2. Configurar application.yml
# Editar src/main/resources/application.yml

# 3. Ejecutar
mvn spring-boot:run
```

## 📦 Estructura

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/synapsse/backend/
│   │   │   ├── config/         # Configuración (Security, CORS)
│   │   │   ├── controller/     # Endpoints REST
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # Entidades JPA
│   │   │   ├── repository/     # Repositories Spring Data
│   │   │   ├── security/       # JWT, Filters
│   │   │   └── service/        # Lógica de negocio
│   │   └── resources/
│   │       ├── application.yml            # Config desarrollo
│   │       └── application-production.yml # Config producción
│   └── test/
├── Dockerfile                   # Imagen Docker optimizada
├── docker-compose.yml          # Desarrollo local
├── docker-compose.prod.yml     # Producción
├── pom.xml                     # Dependencias Maven
├── .env.example                # Template variables de entorno
├── start-docker.bat/.sh        # Scripts de inicio
└── generate-jwt-secret.ps1     # Generador de JWT secret

📚 Documentación:
├── README.md                   # Este archivo
├── QUICKSTART.md               # Inicio rápido
├── EASYPANEL_DEPLOYMENT.md     # Despliegue en EasyPanel (detallado)
├── DEPLOYMENT_COMPARISON.md    # Comparación de plataformas
└── verify-deployment.bat       # Script de verificación pre-despliegue
```

## 🔌 Endpoints API

### Autenticación
```
POST /api/auth/register  - Registrar usuario
POST /api/auth/login     - Login (retorna JWT)
```

### Productos (Público)
```
GET  /api/products              - Lista todos los productos
GET  /api/products/{id}         - Obtener producto por ID
GET  /api/products/search?q=... - Buscar productos
```

### Admin (Requiere JWT con rol ADMIN)
```
GET    /api/admin/users         - Lista usuarios
POST   /api/admin/products      - Crear producto
PUT    /api/admin/products/{id} - Actualizar producto
DELETE /api/admin/products/{id} - Eliminar producto
```

### Usuario por Defecto
Al iniciar por primera vez se crea:
- **Email:** admin@synapsse.com
- **Password:** Admin1234
- **Rol:** ADMIN

## 🗄️ Base de Datos

### Con Docker Compose
La base de datos se crea automáticamente. Ver `.env`.

### Manual (PostgreSQL)

```sql
-- Crear base de datos
CREATE DATABASE synapsse;

-- Crear usuario
CREATE USER synapsse WITH ENCRYPTED PASSWORD 'tu_password';

-- Dar permisos
GRANT ALL PRIVILEGES ON DATABASE synapsse TO synapsse;
```

Luego editar `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/synapsse
    username: synapsse
    password: tu_password
```

## ⚙️ Tecnologías

- **Java:** 17
- **Spring Boot:** 3.2.5
- **Spring Security:** JWT Authentication
- **Spring Data JPA:** ORM
- **PostgreSQL:** Base de datos
- **Lombok:** Reduce boilerplate
- **Maven:** Build tool
- **Docker:** Containerización

## 🔧 Configuración

### Variables de Entorno

Para desarrollo local (`.env`):
```env
DB_PASSWORD=tu_password
JWT_SECRET=tu_jwt_secret_en_base64
JWT_EXPIRATION=86400000
```

Para producción (EasyPanel):
```env
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=jdbc:postgresql://postgres:5432/synapsse
DB_USERNAME=synapsse
DB_PASSWORD=tu_password_segura
JWT_SECRET=tu_jwt_secret_seguro
JWT_EXPIRATION=86400000
SERVER_PORT=8080
JAVA_OPTS=-Xmx512m -Xms256m
```

### Profiles de Spring

**development** (por defecto):
- Logs detallados
- SQL visible
- Base de datos local

**production**:
- Logs mínimos
- SQL oculto
- Optimizado para rendimiento
- Variables desde entorno

Activar profile:
```bash
# Con Maven
mvn spring-boot:run -Dspring-boot.run.profiles=production

# Con Docker
docker-compose -f docker-compose.prod.yml up
```

## 🐳 Docker

### Desarrollo Local
```bash
docker-compose up -d
```

### Producción
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Comandos Útiles
```bash
# Ver logs
docker-compose logs -f backend

# Reiniciar
docker-compose restart backend

# Detener
docker-compose down

# Reconstruir
docker-compose up -d --build
```

## 🚀 Despliegue

### EasyPanel (Recomendado)
Docker Compose nativo, SSL automático, PostgreSQL incluido.

📖 [Guía completa de EasyPanel](EASYPANEL_DEPLOYMENT.md)

### Otras Opciones
- Railway
- Heroku
- AWS ECS
- Google Cloud Run
- Azure Container Apps

Cualquier plataforma que soporte Docker Compose funcionará.

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Con coverage
mvn clean test jacoco:report
```

## 📊 Monitoreo

### Health Check
```bash
curl http://localhost:8080/api/products
```

### Logs
```bash
# Docker Compose
docker-compose logs -f backend

# Maven
# Los logs se muestran en la consola
```

### Métricas (Docker)
```bash
docker stats synapsse-backend
```

## 🔒 Seguridad

### CORS
Configurado en `SecurityConfig.java`:
```java
configuration.setAllowedOriginPatterns(List.of(
    "http://localhost:3000",
    "https://*.vercel.app",
    "https://tudominio.com"
));
```

### JWT
- Secret en Base64
- Expiración configurable
- Almacenado en variables de entorno

### Passwords
- Encriptación con BCrypt
- Validación de fortaleza

## 🆘 Soporte

### Problemas Comunes

**"Cannot connect to database"**
```bash
# Verificar que PostgreSQL está corriendo
docker-compose ps

# Ver logs
docker-compose logs postgres
```

**"Port 8080 already in use"**
```bash
# Cambiar puerto en docker-compose.yml
ports:
  - "8081:8080"
```

**"Build failed"**
```bash
# Limpiar y reconstruir
mvn clean package
docker-compose build --no-cache
```

### Documentación
- [Quick Start](QUICKSTART.md)
- [Guía de Despliegue](EASYPANEL_DEPLOYMENT.md)
- [Frontend en Vercel](../VERCEL_DEPLOYMENT.md)

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -m 'Agregar nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

## 📝 Licencia

Este proyecto es parte de Synapsse E-commerce.

---

**Desarrollado con ❤️ usando Spring Boot**
