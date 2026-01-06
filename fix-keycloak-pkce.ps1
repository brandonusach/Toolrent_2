# Script DEFINITIVO para eliminar PKCE de Keycloak
# Usa un enfoque directo para remover el atributo problemático

$KEYCLOAK_URL = "http://172.20.147.108:30090"
$REALM = "ToolRent"
$CLIENT_ID = "toolrent-client"
$ADMIN_USER = "admin"
$ADMIN_PASS = "admin"

Write-Host "=== ELIMINANDO PKCE DE KEYCLOAK ===" -ForegroundColor Cyan
Write-Host ""

# 1. Obtener token
Write-Host "1. Autenticando..." -ForegroundColor Yellow
$tokenResponse = Invoke-RestMethod -Uri "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" `
    -Method Post `
    -Body @{
        username = $ADMIN_USER
        password = $ADMIN_PASS
        grant_type = "password"
        client_id = "admin-cli"
    } `
    -ContentType "application/x-www-form-urlencoded"

$ACCESS_TOKEN = $tokenResponse.access_token
Write-Host "✅ Autenticado" -ForegroundColor Green

# 2. Obtener cliente
Write-Host ""
Write-Host "2. Buscando cliente..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $ACCESS_TOKEN"
    "Content-Type" = "application/json"
}

$clients = Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients?clientId=$CLIENT_ID" `
    -Method Get `
    -Headers $headers

$CLIENT_UUID = $clients[0].id
Write-Host "✅ Cliente encontrado: $CLIENT_UUID" -ForegroundColor Green

# 3. Obtener configuración actual completa
Write-Host ""
Write-Host "3. Obteniendo configuración actual..." -ForegroundColor Yellow
$currentClient = Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients/$CLIENT_UUID" `
    -Method Get `
    -Headers $headers

Write-Host "✅ Configuración obtenida" -ForegroundColor Green

# 4. Modificar la configuración
Write-Host ""
Write-Host "4. Modificando configuración (removiendo PKCE)..." -ForegroundColor Yellow

# Convertir a JSON, modificar y volver a convertir
$clientJson = $currentClient | ConvertTo-Json -Depth 10
$clientObj = $clientJson | ConvertFrom-Json

# Habilitar Implicit Flow
$clientObj.implicitFlowEnabled = $true

# Remover PKCE de los atributos
if ($clientObj.attributes.PSObject.Properties["pkce.code.challenge.method"]) {
    Write-Host "  Removiendo pkce.code.challenge.method..." -ForegroundColor Cyan
    $clientObj.attributes.PSObject.Properties.Remove("pkce.code.challenge.method")
}

# Asegurar web origins correctos
$clientObj.webOrigins = @(
    "http://172.20.147.108:30081",
    "http://localhost:5173",
    "+"
)

Write-Host "✅ Configuración modificada" -ForegroundColor Green

# 5. Aplicar cambios
Write-Host ""
Write-Host "5. Aplicando cambios..." -ForegroundColor Yellow

$updatedJson = $clientObj | ConvertTo-Json -Depth 10

try {
    Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients/$CLIENT_UUID" `
        -Method Put `
        -Headers $headers `
        -Body $updatedJson `
        -ContentType "application/json"

    Write-Host "✅ Cambios aplicados exitosamente" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: $_" -ForegroundColor Red
    exit 1
}

# 6. Verificar
Write-Host ""
Write-Host "6. Verificando cambios..." -ForegroundColor Yellow
$verifyClient = Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients/$CLIENT_UUID" `
    -Method Get `
    -Headers $headers

Write-Host ""
Write-Host "CONFIGURACION FINAL:" -ForegroundColor Cyan
Write-Host "  - Standard Flow: $($verifyClient.standardFlowEnabled)" -ForegroundColor White
Write-Host "  - Implicit Flow: $($verifyClient.implicitFlowEnabled)" -ForegroundColor White
Write-Host "  - Public Client: $($verifyClient.publicClient)" -ForegroundColor White

$hasPKCE = $verifyClient.attributes.PSObject.Properties["pkce.code.challenge.method"]
if ($hasPKCE) {
    Write-Host "  - PKCE: AUN PRESENTE ($($verifyClient.attributes.'pkce.code.challenge.method'))" -ForegroundColor Red
    Write-Host ""
    Write-Host "ADVERTENCIA: PKCE no se pudo remover automaticamente." -ForegroundColor Yellow
    Write-Host "   Hazlo manualmente en Keycloak:" -ForegroundColor Yellow
    Write-Host "   1. Ir a: http://172.20.147.108:30090/admin" -ForegroundColor Cyan
    Write-Host "   2. Login: admin / admin" -ForegroundColor Cyan
    Write-Host "   3. Realm: ToolRent > Clients > toolrent-client" -ForegroundColor Cyan
    Write-Host "   4. Tab Advanced > OAuth 2.0 settings" -ForegroundColor Cyan
    Write-Host "   5. Proof Key for Code Exchange Code Challenge Method: (empty)" -ForegroundColor Cyan
    Write-Host "   6. Save" -ForegroundColor Cyan
} else {
    Write-Host "  - PKCE: DESACTIVADO" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== PROCESO COMPLETADO ===" -ForegroundColor Green
Write-Host ""
Write-Host "SIGUIENTES PASOS:" -ForegroundColor Yellow
Write-Host "1. cd frontend" -ForegroundColor Cyan
Write-Host "2. .\rebuild-and-redeploy.ps1" -ForegroundColor Cyan
Write-Host "3. Limpiar cache del navegador (Ctrl + Shift + Delete)" -ForegroundColor Cyan
Write-Host "4. Abrir http://172.20.147.108:30081" -ForegroundColor Cyan
Write-Host ""

