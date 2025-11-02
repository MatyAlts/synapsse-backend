#!/bin/bash
# Script para construir y ejecutar el backend con Docker Compose

echo "=========================================="
echo "🚀 Synapsse Backend - Docker Compose"
echo "=========================================="
echo ""

# Verificar si existe .env
if [ ! -f .env ]; then
    echo "⚠️  Archivo .env no encontrado!"
    echo "📝 Creando .env desde .env.example..."
    cp .env.example .env
    echo ""
    echo "⚠️  IMPORTANTE: Edita el archivo .env y configura:"
    echo "   - DB_PASSWORD"
    echo "   - JWT_SECRET (usa: ./generate-jwt-secret.ps1)"
    echo ""
    read -p "Presiona Enter cuando hayas configurado .env..."
fi

echo "🔨 Construyendo imágenes Docker..."
docker-compose build

echo ""
echo "🚀 Iniciando servicios..."
docker-compose up -d

echo ""
echo "📊 Estado de los contenedores:"
docker-compose ps

echo ""
echo "=========================================="
echo "✅ Backend iniciado correctamente"
echo "=========================================="
echo ""
echo "📡 URLs:"
echo "   Backend API: http://localhost:8080"
echo "   PostgreSQL:  localhost:5432"
echo ""
echo "📝 Comandos útiles:"
echo "   Ver logs:     docker-compose logs -f backend"
echo "   Detener:      docker-compose down"
echo "   Reiniciar:    docker-compose restart backend"
echo "   Reconstruir:  docker-compose up -d --build"
echo ""
