# Script para redesplegar API Gateway y Keycloak con configuración corregida
# Ejecutar desde: microservicios/

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "REDESPLIEGUE: API Gateway + Keycloak" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 1. Recompilar API Gateway
Write-Host "`n[1/6] Recompilando API Gateway..." -ForegroundColor Yellow
Set-Location "api-gateway"
.\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Fallo al compilar API Gateway" -ForegroundColor Red
    exit 1
}
Set-Location ..

# 2. Construir imagen Docker del API Gateway
Write-Host "`n[2/6] Construyendo imagen Docker del API Gateway..." -ForegroundColor Yellow
docker build -t brandonusach/toolrent-api-gateway:latest ./api-gateway
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Fallo al construir imagen Docker" -ForegroundColor Red
    exit 1
}

# 3. Subir imagen a Docker Hub (opcional, comentar si no es necesario)
Write-Host "`n[3/6] Subiendo imagen a Docker Hub..." -ForegroundColor Yellow
docker push brandonusach/toolrent-api-gateway:latest
if ($LASTEXITCODE -ne 0) {
    Write-Host "ADVERTENCIA: Fallo al subir imagen, continuando..." -ForegroundColor Yellow
}

# 4. Cargar imagen en Minikube
Write-Host "`n[4/6] Cargando imagen en Minikube..." -ForegroundColor Yellow
minikube image load brandonusach/toolrent-api-gateway:latest
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Fallo al cargar imagen en Minikube" -ForegroundColor Red
    exit 1
}

# 5. Aplicar configuración de Keycloak
Write-Host "`n[5/6] Aplicando configuración de Keycloak..." -ForegroundColor Yellow
kubectl apply -f k8s/keycloak.yml
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Fallo al aplicar keycloak.yml" -ForegroundColor Red
    exit 1
}

# 6. Redesplegar API Gateway
Write-Host "`n[6/6] Redesplegando API Gateway..." -ForegroundColor Yellow
kubectl rollout restart deployment/api-gateway -n toolrent
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Fallo al redesplegar API Gateway" -ForegroundColor Red
    exit 1
}

# Esperar a que los pods estén listos
Write-Host "`n[ESPERANDO] Pods reiniciándose..." -ForegroundColor Cyan
Start-Sleep -Seconds 5
kubectl rollout status deployment/api-gateway -n toolrent --timeout=120s
kubectl rollout status deployment/keycloak -n toolrent --timeout=120s

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "REDESPLIEGUE COMPLETADO EXITOSAMENTE" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

Write-Host "`nEstado de los pods:" -ForegroundColor Cyan
kubectl get pods -n toolrent | Select-String -Pattern "api-gateway|keycloak"

Write-Host "`n✅ URLS DE ACCESO:" -ForegroundColor Green
$MINIKUBE_IP = minikube ip
Write-Host "   Frontend:    http://${MINIKUBE_IP}:30081" -ForegroundColor White
Write-Host "   API Gateway: http://${MINIKUBE_IP}:30080" -ForegroundColor White
Write-Host "   Keycloak:    http://${MINIKUBE_IP}:30090" -ForegroundColor White
Write-Host "`n   Usuario Keycloak: admin / admin" -ForegroundColor Yellow

