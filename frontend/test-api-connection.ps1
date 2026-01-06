# Script para diagnosticar problemas de conectividad con el API Gateway

$ErrorActionPreference = "Continue"

Write-Host "=== DIAGNÓSTICO DE CONECTIVIDAD ===" -ForegroundColor Cyan
Write-Host ""

# Variables
$MINIKUBE_IP = "172.20.147.108"
$API_GATEWAY_PORT = "30080"
$KEYCLOAK_PORT = "30090"
$FRONTEND_PORT = "30081"

# Test 1: Ping a Minikube IP
Write-Host "1. Probando conectividad a Minikube IP..." -ForegroundColor Yellow
$pingResult = Test-Connection -ComputerName $MINIKUBE_IP -Count 2 -Quiet
if ($pingResult) {
    Write-Host "✓ Minikube IP ($MINIKUBE_IP) es accesible" -ForegroundColor Green
} else {
    Write-Host "✗ Minikube IP ($MINIKUBE_IP) NO es accesible" -ForegroundColor Red
}
Write-Host ""

# Test 2: API Gateway Health
Write-Host "2. Probando API Gateway health endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://${MINIKUBE_IP}:${API_GATEWAY_PORT}/actuator/health" -UseBasicParsing -TimeoutSec 5
    Write-Host "✓ API Gateway responde (Status: $($response.StatusCode))" -ForegroundColor Green
    Write-Host "  Content: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ API Gateway NO responde" -ForegroundColor Red
    Write-Host "  Error: $_" -ForegroundColor Red
}
Write-Host ""

# Test 3: Categories endpoint
Write-Host "3. Probando endpoint de categorías..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://${MINIKUBE_IP}:${API_GATEWAY_PORT}/api/v1/categories/" -UseBasicParsing -TimeoutSec 5
    Write-Host "✓ Endpoint de categorías responde (Status: $($response.StatusCode))" -ForegroundColor Green
    $categories = $response.Content | ConvertFrom-Json
    Write-Host "  Categorías encontradas: $($categories.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Endpoint de categorías NO responde" -ForegroundColor Red
    Write-Host "  Error: $_" -ForegroundColor Red
}
Write-Host ""

# Test 4: Tools endpoint
Write-Host "4. Probando endpoint de herramientas..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://${MINIKUBE_IP}:${API_GATEWAY_PORT}/api/v1/tools/" -UseBasicParsing -TimeoutSec 5
    Write-Host "✓ Endpoint de herramientas responde (Status: $($response.StatusCode))" -ForegroundColor Green
    $tools = $response.Content | ConvertFrom-Json
    Write-Host "  Herramientas encontradas: $($tools.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Endpoint de herramientas NO responde" -ForegroundColor Red
    Write-Host "  Error: $_" -ForegroundColor Red
}
Write-Host ""

# Test 5: Keycloak
Write-Host "5. Probando Keycloak..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://${MINIKUBE_IP}:${KEYCLOAK_PORT}/realms/ToolRent" -UseBasicParsing -TimeoutSec 5
    Write-Host "✓ Keycloak responde (Status: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "✗ Keycloak NO responde" -ForegroundColor Red
    Write-Host "  Error: $_" -ForegroundColor Red
}
Write-Host ""

# Test 6: Frontend
Write-Host "6. Probando Frontend..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://${MINIKUBE_IP}:${FRONTEND_PORT}/" -UseBasicParsing -TimeoutSec 5
    Write-Host "✓ Frontend responde (Status: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "✗ Frontend NO responde" -ForegroundColor Red
    Write-Host "  Error: $_" -ForegroundColor Red
}
Write-Host ""

# Test 7: Verificar pods en Kubernetes
Write-Host "7. Estado de los pods en Kubernetes..." -ForegroundColor Yellow
kubectl get pods -n toolrent -o wide
Write-Host ""

# Test 8: Verificar servicios en Kubernetes
Write-Host "8. Servicios expuestos en Kubernetes..." -ForegroundColor Yellow
kubectl get services -n toolrent | Select-String "NodePort|NAME"
Write-Host ""

Write-Host "=== FIN DEL DIAGNÓSTICO ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Si todos los tests pasan pero el frontend no funciona," -ForegroundColor Yellow
Write-Host "el problema está en el código JavaScript del navegador." -ForegroundColor Yellow
Write-Host ""
Write-Host "URLs de acceso:" -ForegroundColor Cyan
Write-Host "  Frontend: http://${MINIKUBE_IP}:${FRONTEND_PORT}" -ForegroundColor White
Write-Host "  API Gateway: http://${MINIKUBE_IP}:${API_GATEWAY_PORT}" -ForegroundColor White
Write-Host "  Keycloak: http://${MINIKUBE_IP}:${KEYCLOAK_PORT}" -ForegroundColor White

