# Script para configurar el cliente de Keycloak para HTTP (sin PKCE)
# Ejecutar este script después de que Keycloak esté funcionando

$KEYCLOAK_URL = "http://172.20.147.108:30090"
$REALM = "ToolRent"
$CLIENT_ID = "toolrent-client"
$ADMIN_USER = "admin"
$ADMIN_PASS = "admin"

Write-Host "=== CONFIGURANDO KEYCLOAK PARA HTTP ===" -ForegroundColor Cyan
Write-Host ""

# 1. Obtener token de admin
Write-Host "1. Obteniendo token de administrador..." -ForegroundColor Yellow
try {
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
    Write-Host "OK - Token obtenido exitosamente" -ForegroundColor Green
} catch {
    Write-Host "ERROR al obtener token: $_" -ForegroundColor Red
    exit 1
}

# 2. Obtener el cliente actual
Write-Host ""
Write-Host "2. Obteniendo configuracion del cliente '$CLIENT_ID'..." -ForegroundColor Yellow
try {
    $headers = @{
        "Authorization" = "Bearer $ACCESS_TOKEN"
        "Content-Type" = "application/json"
    }

    $clients = Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients?clientId=$CLIENT_ID" `
        -Method Get `
        -Headers $headers

    if ($clients.Count -eq 0) {
        Write-Host "ERROR - Cliente '$CLIENT_ID' no encontrado" -ForegroundColor Red
        exit 1
    }

    $client = $clients[0]
    $CLIENT_UUID = $client.id
    Write-Host "OK - Cliente encontrado (ID: $CLIENT_UUID)" -ForegroundColor Green
} catch {
    Write-Host "ERROR al obtener cliente: $_" -ForegroundColor Red
    exit 1
}

# 3. Actualizar configuración del cliente
Write-Host ""
Write-Host "3. Actualizando configuracion del cliente..." -ForegroundColor Yellow

# Configuración optimizada para HTTP (sin PKCE obligatorio)
$clientConfig = @{
    id = $CLIENT_UUID
    clientId = $CLIENT_ID
    enabled = $true
    publicClient = $true
    protocol = "openid-connect"
    standardFlowEnabled = $true           # Authorization Code Flow
    implicitFlowEnabled = $false          # NO usar implicit
    directAccessGrantsEnabled = $true     # Permitir Resource Owner Password
    serviceAccountsEnabled = $false
    authorizationServicesEnabled = $false
    fullScopeAllowed = $true

    # URLs permitidas - IMPORTANTE para CORS
    rootUrl = "http://172.20.147.108:30081"
    baseUrl = "http://172.20.147.108:30081"
    redirectUris = @(
        "http://172.20.147.108:30081/*",
        "http://localhost:5173/*"  # Para desarrollo local
    )
    webOrigins = @(
        "http://172.20.147.108:30081",
        "http://localhost:5173"
    )

    # Atributos específicos para el cliente
    attributes = @{
        "pkce.code.challenge.method" = "S256"           # PKCE si es posible
        "post.logout.redirect.uris" = "http://172.20.147.108:30081/*"
        "oauth2.device.authorization.grant.enabled" = "false"
        "oidc.ciba.grant.enabled" = "false"
        "backchannel.logout.session.required" = "true"
        "backchannel.logout.revoke.offline.tokens" = "false"
        "use.refresh.tokens" = "true"
        "client_credentials.use_refresh_token" = "false"
    }
}

try {
    $body = $clientConfig | ConvertTo-Json -Depth 10

    Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients/$CLIENT_UUID" `
        -Method Put `
        -Headers $headers `
        -Body $body `
        -ContentType "application/json"

    Write-Host "OK - Cliente actualizado exitosamente" -ForegroundColor Green
} catch {
    Write-Host "ERROR al actualizar cliente: $_" -ForegroundColor Red
    Write-Host "Detalles: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 4. Verificar roles del cliente
Write-Host ""
Write-Host "4. Verificando roles del cliente..." -ForegroundColor Yellow
try {
    $roles = Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients/$CLIENT_UUID/roles" `
        -Method Get `
        -Headers $headers

    Write-Host "OK - Roles encontrados: $($roles.Count)" -ForegroundColor Green
    $roles | ForEach-Object { Write-Host "  - $($_.name)" -ForegroundColor Cyan }
} catch {
    Write-Host "WARNING - No se pudieron obtener los roles" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== CONFIGURACION COMPLETADA ===" -ForegroundColor Green
Write-Host ""
Write-Host "SIGUIENTE PASO:" -ForegroundColor Yellow
Write-Host '1. Ir a la carpeta frontend: cd frontend' -ForegroundColor Cyan
Write-Host '2. Ejecutar script de rebuild: .\rebuild-and-redeploy.ps1' -ForegroundColor Cyan
Write-Host ""

