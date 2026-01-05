# Script para verificar y configurar Keycloak en Kubernetes
# Ejecutar desde cualquier carpeta del proyecto

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "    VERIFICACIÓN Y DEPLOY DE KEYCLOAK          " -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar IP de Minikube
Write-Host "1. Verificando IP de Minikube..." -ForegroundColor Yellow
$minikubeIP = minikube ip
Write-Host "   IP de Minikube: $minikubeIP" -ForegroundColor Green
Write-Host ""

# Verificar namespace
Write-Host "2. Verificando namespace toolrent..." -ForegroundColor Yellow
$namespace = kubectl get namespace toolrent --ignore-not-found
if ([string]::IsNullOrEmpty($namespace)) {
    Write-Host "   Namespace no existe, creándolo..." -ForegroundColor Yellow
    kubectl create namespace toolrent
    Write-Host "   Namespace creado" -ForegroundColor Green
} else {
    Write-Host "   Namespace ya existe" -ForegroundColor Green
}
Write-Host ""

# Verificar si Keycloak está desplegado
Write-Host "3. Verificando estado de Keycloak..." -ForegroundColor Yellow
$keycloakPod = kubectl get pods -n toolrent -l app=keycloak --ignore-not-found
if ([string]::IsNullOrEmpty($keycloakPod)) {
    Write-Host "   Keycloak no está desplegado. ¿Deseas desplegarlo? (s/n)" -ForegroundColor Yellow
    $deploy = Read-Host "   "

    if ($deploy -eq "s") {
        Write-Host "   Desplegando Keycloak..." -ForegroundColor Yellow
        kubectl apply -f microservicios\k8s\keycloak.yml

        Write-Host "   Esperando a que Keycloak esté listo (esto puede tomar varios minutos)..." -ForegroundColor Yellow
        kubectl wait --for=condition=ready pod -l app=keycloak -n toolrent --timeout=300s

        Write-Host "   Keycloak desplegado exitosamente" -ForegroundColor Green
    }
} else {
    Write-Host "   Keycloak ya está desplegado:" -ForegroundColor Green
    kubectl get pods -n toolrent -l app=keycloak
}
Write-Host ""

# Verificar servicio de Keycloak
Write-Host "4. Verificando servicio de Keycloak..." -ForegroundColor Yellow
$keycloakSvc = kubectl get svc keycloak -n toolrent --ignore-not-found
if ([string]::IsNullOrEmpty($keycloakSvc)) {
    Write-Host "   ERROR: Servicio de Keycloak no existe" -ForegroundColor Red
} else {
    Write-Host "   Servicio de Keycloak:" -ForegroundColor Green
    kubectl get svc keycloak -n toolrent
}
Write-Host ""

# Verificar acceso a Keycloak
Write-Host "5. Verificando acceso a Keycloak..." -ForegroundColor Yellow
$keycloakUrl = "http://$minikubeIP:30090"
Write-Host "   URL de Keycloak: $keycloakUrl" -ForegroundColor White

try {
    $response = Invoke-WebRequest -Uri $keycloakUrl -TimeoutSec 5 -UseBasicParsing -ErrorAction Stop
    Write-Host "   Keycloak está accesible (Status: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "   ADVERTENCIA: No se pudo acceder a Keycloak" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   Verifica que el pod esté running y el servicio configurado" -ForegroundColor Yellow
}
Write-Host ""

# Información importante
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "         INFORMACIÓN DE KEYCLOAK                " -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Configuración esperada en Keycloak:" -ForegroundColor Yellow
Write-Host "  URL:       http://$minikubeIP:30090" -ForegroundColor White
Write-Host "  Admin:     admin / admin" -ForegroundColor White
Write-Host "  Realm:     ToolRent (con T y R mayúsculas)" -ForegroundColor White
Write-Host "  Client ID: toolrent-client" -ForegroundColor White
Write-Host ""
Write-Host "Pasos para configurar Keycloak:" -ForegroundColor Yellow
Write-Host "  1. Acceder a: http://$minikubeIP:30090" -ForegroundColor White
Write-Host "  2. Login con admin/admin" -ForegroundColor White
Write-Host "  3. Crear realm 'ToolRent' (si no existe)" -ForegroundColor White
Write-Host "  4. Crear client 'toolrent-client'" -ForegroundColor White
Write-Host "     - Client Protocol: openid-connect" -ForegroundColor White
Write-Host "     - Access Type: public" -ForegroundColor White
Write-Host "     - Valid Redirect URIs: http://$minikubeIP:30081/*" -ForegroundColor White
Write-Host "     - Web Origins: http://$minikubeIP:30081" -ForegroundColor White
Write-Host ""
Write-Host "Comandos útiles:" -ForegroundColor Green
Write-Host "  Ver logs:      kubectl logs -f deployment/keycloak -n toolrent" -ForegroundColor White
Write-Host "  Ver pod:       kubectl get pods -n toolrent -l app=keycloak" -ForegroundColor White
Write-Host "  Restart:       kubectl rollout restart deployment/keycloak -n toolrent" -ForegroundColor White
Write-Host ""

