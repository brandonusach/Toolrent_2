# Script para corregir y desplegar el frontend con los cambios del manejo de errores
# Este script debe ejecutarse con privilegios de administrador

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  Fix y Deploy Frontend" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si se está ejecutando como administrador
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "⚠️  ADVERTENCIA: Este script necesita privilegios de administrador" -ForegroundColor Yellow
    Write-Host "   Haz clic derecho en PowerShell y selecciona 'Ejecutar como administrador'" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Presiona Enter para salir"
    exit 1
}

# Verificar si Minikube está corriendo
Write-Host "0. Verificando estado de Minikube..." -ForegroundColor Yellow
$minikubeStatus = minikube status 2>&1 | Out-String
if ($minikubeStatus -notlike "*Running*") {
    Write-Host "   Minikube no está corriendo. Iniciando..." -ForegroundColor Yellow
    minikube start
    if ($LASTEXITCODE -ne 0) {
        Write-Host "   ✗ Error al iniciar Minikube" -ForegroundColor Red
        Read-Host "Presiona Enter para salir"
        exit 1
    }
    Write-Host "   ✓ Minikube iniciado" -ForegroundColor Green
} else {
    Write-Host "   ✓ Minikube ya está corriendo" -ForegroundColor Green
}

# Verificar si ya está construido el frontend
Write-Host "1. Frontend ya construido (usando imagen en caché)..." -ForegroundColor Green

# Construir la imagen Docker (si no existe o hay cambios)
Write-Host "2. Construyendo imagen Docker..." -ForegroundColor Yellow
docker build -t toolrent-frontend:latest .
if ($LASTEXITCODE -ne 0) {
    Write-Host "   ✗ Error al construir la imagen Docker" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}
Write-Host "   ✓ Imagen Docker construida" -ForegroundColor Green

# Cargar la imagen en Minikube
Write-Host "3. Cargando imagen en Minikube..." -ForegroundColor Yellow
minikube image load toolrent-frontend:latest
if ($LASTEXITCODE -ne 0) {
    Write-Host "   ✗ Error al cargar la imagen en Minikube" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}
Write-Host "   ✓ Imagen cargada en Minikube" -ForegroundColor Green

# Actualizar el ConfigMap
Write-Host "4. Actualizando ConfigMap..." -ForegroundColor Yellow
kubectl apply -f ..\microservicios\k8s\frontend-hotfix-configmap.yml
if ($LASTEXITCODE -ne 0) {
    Write-Host "   ✗ Error al actualizar el ConfigMap" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}
Write-Host "   ✓ ConfigMap actualizado" -ForegroundColor Green

# Reiniciar el deployment del frontend
Write-Host "5. Reiniciando deployment del frontend..." -ForegroundColor Yellow
kubectl rollout restart deployment frontend -n toolrent
if ($LASTEXITCODE -ne 0) {
    Write-Host "   ✗ Error al reiniciar el deployment" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}
Write-Host "   ✓ Deployment reiniciado" -ForegroundColor Green

# Esperar a que el deployment esté listo
Write-Host "6. Esperando a que el deployment esté listo..." -ForegroundColor Yellow
kubectl rollout status deployment frontend -n toolrent --timeout=120s
if ($LASTEXITCODE -ne 0) {
    Write-Host "   ✗ Timeout esperando el deployment" -ForegroundColor Red
    Write-Host "   Verifica el estado con: kubectl get pods -n toolrent" -ForegroundColor Yellow
    Read-Host "Presiona Enter para salir"
    exit 1
}
Write-Host "   ✓ Deployment listo" -ForegroundColor Green

Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  ✓ Deployment completado exitosamente" -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Próximos pasos:" -ForegroundColor Cyan
Write-Host "1. Abre tu navegador y ve a la aplicación" -ForegroundColor White
Write-Host "2. Presiona F12 para abrir las herramientas de desarrollo" -ForegroundColor White
Write-Host "3. Verifica que ya NO aparezcan los errores:" -ForegroundColor White
Write-Host "   'Cannot read properties of undefined (reading config)'" -ForegroundColor Gray
Write-Host ""
Write-Host "Comandos útiles:" -ForegroundColor Yellow
Write-Host "  Ver pods:  kubectl get pods -n toolrent | Select-String frontend" -ForegroundColor White
Write-Host "  Ver logs:  kubectl logs -n toolrent -l app=frontend -f" -ForegroundColor White
Write-Host ""
Read-Host "Presiona Enter para salir"

