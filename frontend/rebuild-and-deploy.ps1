# Script para reconstruir y redesplegar el frontend en Kubernetes
# Ejecutar desde la carpeta frontend

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  REBUILD Y DEPLOY DEL FRONTEND EN KUBERNETES  " -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar IP de Minikube
Write-Host "1. Verificando IP de Minikube..." -ForegroundColor Yellow
$minikubeIP = minikube ip
Write-Host "   IP de Minikube: $minikubeIP" -ForegroundColor Green

if ($minikubeIP -ne "172.20.147.108") {
    Write-Host "   ADVERTENCIA: La IP de Minikube ha cambiado!" -ForegroundColor Red
    Write-Host "   IP actual: $minikubeIP" -ForegroundColor Red
    Write-Host "   IP configurada: 172.20.147.108" -ForegroundColor Red
    Write-Host ""
    $continue = Read-Host "   ¿Deseas continuar de todas formas? (s/n)"
    if ($continue -ne "s") {
        Write-Host "   Deploy cancelado." -ForegroundColor Red
        exit
    }
}

Write-Host ""

# Build de la imagen Docker
Write-Host "2. Construyendo imagen Docker del frontend..." -ForegroundColor Yellow
docker build -t brandonusach/toolrent-frontend:latest .

if ($LASTEXITCODE -ne 0) {
    Write-Host "   ERROR: Falló el build de Docker" -ForegroundColor Red
    exit 1
}
Write-Host "   Build completado exitosamente" -ForegroundColor Green
Write-Host ""

# Push a Docker Hub (opcional)
Write-Host "3. ¿Deseas hacer push a Docker Hub? (s/n)" -ForegroundColor Yellow
$pushToHub = Read-Host "   "
if ($pushToHub -eq "s") {
    Write-Host "   Pushing imagen a Docker Hub..." -ForegroundColor Yellow
    docker push brandonusach/toolrent-frontend:latest

    if ($LASTEXITCODE -ne 0) {
        Write-Host "   ERROR: Falló el push a Docker Hub" -ForegroundColor Red
        exit 1
    }
    Write-Host "   Push completado exitosamente" -ForegroundColor Green
}
Write-Host ""

# Cargar imagen en Minikube (si no se hizo push)
if ($pushToHub -ne "s") {
    Write-Host "4. Cargando imagen en Minikube..." -ForegroundColor Yellow
    minikube image load brandonusach/toolrent-frontend:latest

    if ($LASTEXITCODE -ne 0) {
        Write-Host "   ERROR: Falló la carga de la imagen en Minikube" -ForegroundColor Red
        exit 1
    }
    Write-Host "   Imagen cargada en Minikube exitosamente" -ForegroundColor Green
} else {
    Write-Host "4. Imagen disponible en Docker Hub, Minikube la descargará" -ForegroundColor Green
}
Write-Host ""

# Verificar namespace
Write-Host "5. Verificando namespace toolrent..." -ForegroundColor Yellow
$namespace = kubectl get namespace toolrent --ignore-not-found
if ([string]::IsNullOrEmpty($namespace)) {
    Write-Host "   Namespace no existe, creándolo..." -ForegroundColor Yellow
    kubectl create namespace toolrent
    Write-Host "   Namespace creado" -ForegroundColor Green
} else {
    Write-Host "   Namespace ya existe" -ForegroundColor Green
}
Write-Host ""

# Deploy del frontend
Write-Host "6. Desplegando frontend en Kubernetes..." -ForegroundColor Yellow
kubectl apply -f ..\microservicios\k8s\configmaps\frontend-nginx-config.yml
kubectl apply -f ..\microservicios\k8s\frontend.yml

if ($LASTEXITCODE -ne 0) {
    Write-Host "   ERROR: Falló el deploy en Kubernetes" -ForegroundColor Red
    exit 1
}
Write-Host "   Deploy completado exitosamente" -ForegroundColor Green
Write-Host ""

# Reiniciar el deployment
Write-Host "7. Reiniciando deployment para aplicar cambios..." -ForegroundColor Yellow
kubectl rollout restart deployment/frontend -n toolrent

if ($LASTEXITCODE -ne 0) {
    Write-Host "   ERROR: Falló el restart del deployment" -ForegroundColor Red
    exit 1
}
Write-Host "   Deployment reiniciado" -ForegroundColor Green
Write-Host ""

# Esperar a que el pod esté listo
Write-Host "8. Esperando a que el pod esté listo..." -ForegroundColor Yellow
kubectl wait --for=condition=ready pod -l app=frontend -n toolrent --timeout=120s

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "           DEPLOY COMPLETADO                    " -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "URLs de acceso:" -ForegroundColor Green
Write-Host "  Frontend:    http://$minikubeIP:30081" -ForegroundColor White
Write-Host "  API Gateway: http://$minikubeIP:30080" -ForegroundColor White
Write-Host "  Keycloak:    http://$minikubeIP:30090" -ForegroundColor White
Write-Host ""
Write-Host "Comandos útiles:" -ForegroundColor Green
Write-Host "  Ver pods:         kubectl get pods -n toolrent" -ForegroundColor White
Write-Host "  Ver logs:         kubectl logs -f deployment/frontend -n toolrent" -ForegroundColor White
Write-Host "  Ver servicios:    kubectl get svc -n toolrent" -ForegroundColor White
Write-Host ""

