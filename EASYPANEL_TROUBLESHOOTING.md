# 🚨 Solución de Errores de EasyPanel

## Errores Encontrados y Corregidos

### ❌ Error 1: Variables no definidas
```
The "POSTGRES_PASSWORD" variable is not set
The "GITHUB_USER" variable is not set
```

**Solución:** ✅ Agregados valores por defecto en `docker-compose.prod.yml`

### ❌ Error 2: Version obsoleta
```
the attribute `version` is obsolete
```

**Solución:** ✅ Eliminado `version: '3.8'` de ambos archivos compose

### ❌ Error 3: Invalid reference format
```
invalid reference format
```

**Causa:** Intentaba usar imagen desde GitHub Registry sin estar publicada  
**Solución:** ✅ Cambiado a `build` local desde Dockerfile

## ✅ Cambios Realizados

### 1. `docker-compose.prod.yml`
- ❌ Removido `version: '3.8'`
- ✅ Cambiado backend de `image:` a `build:`
- ✅ Agregados valores por defecto a todas las variables
- ✅ Removida dependencia de `GITHUB_USER`

### 2. `docker-compose.yml`
- ❌ Removido `version: '3.8'`

### 3. `.gitignore`
- ✅ Actualizado para permitir `.env.example` pero bloquear `.env.production`

## 🚀 Configuración en EasyPanel

### Paso 1: Variables de Entorno OBLIGATORIAS

En EasyPanel Dashboard → Tu Proyecto → Settings → Environment Variables, agrega:

```env
POSTGRES_DB=synapsse
POSTGRES_USER=synapsse
POSTGRES_PASSWORD=tu_password_muy_segura
JWT_SECRET=tu_jwt_secret_en_base64
JWT_EXPIRATION=86400000
BACKEND_PORT=8080
JAVA_OPTS=-Xmx768m -Xms256m
```

### Paso 2: Generar JWT_SECRET

En tu PC local:
```powershell
cd backend
powershell -ExecutionPolicy Bypass -File generate-jwt-secret.ps1
```

Copia el resultado y úsalo en `JWT_SECRET`.

### Paso 3: Redeploy

Después de configurar las variables:
1. Guarda los cambios
2. Click en **"Redeploy"**
3. EasyPanel reconstruirá la imagen desde el Dockerfile

## 📋 Checklist Pre-Deploy

- [ ] Variables de entorno configuradas en EasyPanel
- [ ] `JWT_SECRET` generado y configurado
- [ ] `POSTGRES_PASSWORD` es una contraseña SEGURA
- [ ] Código pusheado a GitHub
- [ ] Branch correcta seleccionada en EasyPanel (`front`)
- [ ] Root directory correcto (`backend`)
- [ ] Compose file: `docker-compose.prod.yml`

## 🔍 Verificar Despliegue

Una vez desplegado, verifica:

```bash
# Si EasyPanel te da una URL
curl https://tu-backend-url/api/products

# Deberías ver una respuesta JSON con productos
```

## 📊 Logs

Para ver qué está pasando:
1. EasyPanel Dashboard → Tu Proyecto → Logs
2. Busca por errores
3. Verifica que veas: "Started SynapsseBackendApplication"

## 🆘 Si Aún Hay Errores

### Build Failed
**Ver logs de build en EasyPanel**
- Puede ser problema con Maven
- Verifica que todas las dependencias en `pom.xml` sean correctas

### Container Keeps Restarting
**Causa común:** PostgreSQL no está listo o credenciales incorrectas
```bash
# En logs de EasyPanel, busca:
- "Connection refused" → PostgreSQL no iniciado
- "Authentication failed" → Password incorrecto
- "Database does not exist" → POSTGRES_DB incorrecto
```

### Port Already in Use
**Cambia el puerto:**
```env
BACKEND_PORT=8081
```

## ✅ Estado Esperado

Cuando todo funcione correctamente, en los logs deberías ver:

```
postgres_1  | database system is ready to accept connections
backend_1   | Starting SynapsseBackendApplication...
backend_1   | Started SynapsseBackendApplication in X seconds
```

Y al hacer curl al endpoint:
```json
[
  {
    "id": 1,
    "name": "Producto 1",
    "price": 100.00,
    ...
  }
]
```

## 🎯 Próximo Paso

Una vez que el backend esté funcionando:
1. Copia la URL pública de tu backend en EasyPanel
2. Ve a Vercel → Settings → Environment Variables
3. Actualiza `NEXT_PUBLIC_API_URL` con la URL del backend
4. Redeploy el frontend en Vercel

---

**¿Necesitas más ayuda?** Revisa los logs en EasyPanel y busca el error específico.
