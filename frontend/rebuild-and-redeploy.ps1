# Script para reconstruir y redesplegar el frontend con los cambios de Keycloak
# Este script automatiza todo el proceso

$ErrorActionPreference = "Stop"

Write-Host "=== RECONSTRUYENDO Y DESPLEGANDO FRONTEND ===" -ForegroundColor Cyan
Write-Host ""

# Variables
$FRONTEND_PATH = "C:\Users\brand\Desktop\TINGESO\toolrent\frontend"
$IMAGE_NAME = "brandonusach/toolrent-frontend:latest"
$NAMESPACE = "toolrent"

# 1. Ir a la carpeta del frontend
Write-Host "1. Navegando a la carpeta del frontend..." -ForegroundColor Yellow
Set-Location $FRONTEND_PATH
Write-Host "✓ En: $FRONTEND_PATH" -ForegroundColor Green
Write-Host ""

# 2. Limpiar build anterior
Write-Host "2. Limpiando build anterior..." -ForegroundColor Yellow
if (Test-Path "dist") {
    Remove-Item -Recurse -Force "dist"
    Write-Host "✓ Carpeta dist eliminada" -ForegroundColor Green
} else {
    Write-Host "✓ No hay build anterior" -ForegroundColor Green
}
Write-Host ""

# 3. Instalar dependencias (opcional, por si acaso)
Write-Host "3. Verificando dependencias..." -ForegroundColor Yellow
if (!(Test-Path "node_modules")) {
    Write-Host "Instalando dependencias..." -ForegroundColor Cyan
    npm ci
    Write-Host "✓ Dependencias instaladas" -ForegroundColor Green
} else {
    Write-Host "✓ Dependencias ya instaladas" -ForegroundColor Green
}
Write-Host ""

# 4. Build local (para verificar que no hay errores)
Write-Host "4. Construyendo aplicación..." -ForegroundColor Yellow
npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Error en el build" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Build completado exitosamente" -ForegroundColor Green
Write-Host ""

# 5. Construir imagen Docker
Write-Host "5. Construyendo imagen Docker..." -ForegroundColor Yellow
docker build -t $IMAGE_NAME .
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Error al construir imagen Docker" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Imagen Docker construida: $IMAGE_NAME" -ForegroundColor Green
Write-Host ""

# 6. Pushear imagen a Docker Hub
Write-Host "6. Pusheando imagen a Docker Hub..." -ForegroundColor Yellow
docker push $IMAGE_NAME
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Error al pushear imagen" -ForegroundColor Red
    Write-Host "⚠ ¿Estás logueado en Docker Hub? Ejecuta: docker login" -ForegroundColor Yellow
    exit 1
}
Write-Host "✓ Imagen pusheada exitosamente" -ForegroundColor Green
Write-Host ""

# 7. Reiniciar deployment en Kubernetes
Write-Host "7. Reiniciando deployment del frontend en Kubernetes..." -ForegroundColor Yellow
kubectl rollout restart deployment/frontend -n $NAMESPACE
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Error al reiniciar deployment" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Deployment reiniciado" -ForegroundColor Green
Write-Host ""

# 8. Esperar a que el pod esté listo
Write-Host "8. Esperando a que el pod esté listo..." -ForegroundColor Yellow
kubectl rollout status deployment/frontend -n $NAMESPACE --timeout=300s
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Timeout esperando el pod" -ForegroundColor Red
    Write-Host ""
    Write-Host "Verificar logs con:" -ForegroundColor Yellow
    Write-Host "kubectl logs -n $NAMESPACE -l app=frontend --tail=50" -ForegroundColor Cyan
    exit 1
}
Write-Host "✓ Pod listo" -ForegroundColor Green
Write-Host ""

# 9. Mostrar información del pod
Write-Host "9. Estado actual del frontend:" -ForegroundColor Yellow
kubectl get pods -n $NAMESPACE -l app=frontend
Write-Host ""

# 10. Obtener URL de acceso
$MINIKUBE_IP = (minikube ip).Trim()
$FRONTEND_PORT = 30081

Write-Host "=== DESPLIEGUE COMPLETADO ===" -ForegroundColor Green
Write-Host ""
Write-Host "Frontend disponible en:" -ForegroundColor Yellow
Write-Host "  http://${MINIKUBE_IP}:${FRONTEND_PORT}" -ForegroundColor Cyan
Write-Host ""
Write-Host "Keycloak disponible en:" -ForegroundColor Yellow
Write-Host "  http://${MINIKUBE_IP}:30090" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para ver logs en tiempo real:" -ForegroundColor Yellow
Write-Host "  kubectl logs -n $NAMESPACE -l app=frontend -f" -ForegroundColor Cyan
Write-Host ""
Write-Host "IMPORTANTE: Ejecuta el siguiente comando para configurar el cliente de Keycloak:" -ForegroundColor Yellow
Write-Host "  .\configure-keycloak-client.ps1" -ForegroundColor Cyan
Write-Host ""

