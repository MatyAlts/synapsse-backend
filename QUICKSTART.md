# 🐳 Quick Start - Docker Compose

## 🚀 Inicio Rápido Local

### 1. Generar JWT Secret
```bash
# Windows
cd backend
powershell -ExecutionPolicy Bypass -File generate-jwt-secret.ps1

# Linux/Mac
./generate-jwt-secret.sh
```

### 2. Configurar Variables de Entorno
```bash
# Copiar template
cp .env.example .env

# Editar (Windows)
notepad .env

# Editar (Linux/Mac)
nano .env
```

Completar:
```env
DB_PASSWORD=tu_password_segura
JWT_SECRET=<copiado del paso 1>
JWT_EXPIRATION=86400000
```

### 3. Iniciar con Docker Compose

**Windows:**
```cmd
start-docker.bat
```

**Linux/Mac:**
```bash
chmod +x start-docker.sh
./start-docker.sh
```

**O manualmente:**
```bash
docker-compose build
docker-compose up -d
```

### 4. Verificar
```bash
# Ver estado
docker-compose ps

# Ver logs
docker-compose logs -f backend

# Probar API
curl http://localhost:8080/api/products
```

### 5. Detener
```cmd
# Windows
stop-docker.bat

# Linux/Mac o Manualmente
docker-compose down
```

---

## 🌐 Despliegue en EasyPanel

### 1. Preparar
- [ ] Generar JWT_SECRET
- [ ] Probar localmente primero
- [ ] Pushear código a GitHub

### 2. En EasyPanel
1. New Project → `synapsse-backend`
2. Add Service → From Git
3. Repo: `dmampel/synapsse`
4. Branch: `front`
5. Path: `/backend`
6. Compose File: `docker-compose.prod.yml`

### 3. Variables de Entorno
```
POSTGRES_DB=synapsse
POSTGRES_USER=synapsse
POSTGRES_PASSWORD=<seguro>
JWT_SECRET=<generado>
JWT_EXPIRATION=86400000
BACKEND_PORT=8080
```

### 4. Configurar Dominio
- Settings → Domains → Add
- `api.tudominio.com`
- SSL automático ✅

### 5. Deploy
- Click "Deploy"
- Esperar 5-10 min

### 6. Verificar
```bash
curl https://api.tudominio.com/api/products
```

---

## 📝 Comandos Útiles

### Gestión Básica
```bash
# Iniciar
docker-compose up -d

# Detener
docker-compose down

# Reiniciar
docker-compose restart

# Ver logs
docker-compose logs -f

# Ver estado
docker-compose ps

# Reconstruir
docker-compose up -d --build
```

### Base de Datos
```bash
# Conectar a PostgreSQL
docker exec -it synapsse-db psql -U synapsse -d synapsse

# Backup
docker exec synapsse-db pg_dump -U synapsse synapsse > backup.sql

# Restaurar
docker exec -i synapsse-db psql -U synapsse synapsse < backup.sql
```

### Debugging
```bash
# Entrar al contenedor
docker exec -it synapsse-backend /bin/sh

# Ver variables de entorno
docker exec synapsse-backend env

# Ver uso de recursos
docker stats

# Limpiar recursos
docker system prune -a
```

---

## 🆘 Problemas Comunes

### "Cannot connect to database"
```bash
# Ver logs de PostgreSQL
docker-compose logs postgres

# Verificar que está corriendo
docker-compose ps

# Reiniciar
docker-compose restart postgres
```

### "Port already in use"
```bash
# Ver qué usa el puerto
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac

# Cambiar puerto en docker-compose.yml
ports:
  - "8081:8080"
```

### "Build failed"
```bash
# Limpiar y reconstruir
docker-compose build --no-cache

# Ver logs detallados
docker-compose up backend
```

### "Container keeps restarting"
```bash
# Ver logs
docker-compose logs --tail=100 backend

# Ver salud
docker inspect synapsse-backend | grep Health
```

---

## 📚 Documentación Completa

- [Guía Detallada de EasyPanel](EASYPANEL_DEPLOYMENT.md)
- [Guía de Vercel (Frontend)](../VERCEL_DEPLOYMENT.md)
- [Checklist Completo](../DEPLOYMENT_CHECKLIST.md)

---

## ✅ URLs

### Desarrollo Local
- Backend: http://localhost:8080
- PostgreSQL: localhost:5432
- Credenciales: ver `.env`

### Producción
- Backend: https://api.tudominio.com
- Frontend: https://tu-app.vercel.app
- Admin: https://tu-app.vercel.app/admin

---

**¡Listo para producción! 🚀**
