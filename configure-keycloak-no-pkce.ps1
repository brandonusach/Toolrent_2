# Script para configurar Keycloak SIN PKCE (solución definitiva para HTTP)
# Este script desactiva completamente PKCE para evitar el error "Web Crypto API is not available"

$KEYCLOAK_URL = "http://172.20.147.108:30090"
$REALM = "ToolRent"
$CLIENT_ID = "toolrent-client"
$ADMIN_USER = "admin"
$ADMIN_PASS = "admin"

Write-Host "=== CONFIGURANDO KEYCLOAK SIN PKCE PARA HTTP ===" -ForegroundColor Cyan
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
    Write-Host "✅ Token obtenido exitosamente" -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR al obtener token: $_" -ForegroundColor Red
    Write-Host "Verifica que Keycloak esté corriendo: kubectl get pods -n toolrent" -ForegroundColor Yellow
    exit 1
}

# 2. Obtener el cliente actual
Write-Host ""
Write-Host "2. Obteniendo configuración del cliente '$CLIENT_ID'..." -ForegroundColor Yellow
try {
    $headers = @{
        "Authorization" = "Bearer $ACCESS_TOKEN"
        "Content-Type" = "application/json"
    }

    $clients = Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients?clientId=$CLIENT_ID" `
        -Method Get `
        -Headers $headers

    if ($clients.Count -eq 0) {
        Write-Host "❌ Cliente '$CLIENT_ID' no encontrado" -ForegroundColor Red
        exit 1
    }

    $client = $clients[0]
    $CLIENT_UUID = $client.id
    Write-Host "✅ Cliente encontrado (ID: $CLIENT_UUID)" -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR al obtener cliente: $_" -ForegroundColor Red
    exit 1
}

# 3. Actualizar configuración del cliente SIN PKCE
Write-Host ""
Write-Host "3. Actualizando configuración del cliente (DESACTIVANDO PKCE)..." -ForegroundColor Yellow

# Configuración ÓPTIMA para HTTP sin PKCE
$clientConfig = @{
    id = $CLIENT_UUID
    clientId = $CLIENT_ID
    enabled = $true
    publicClient = $true
    protocol = "openid-connect"

    # FLUJOS: Habilitar Standard Flow (Authorization Code) sin forzar PKCE
    standardFlowEnabled = $true
    implicitFlowEnabled = $true           # HABILITAR: Permite fallback si PKCE falla
    directAccessGrantsEnabled = $true
    serviceAccountsEnabled = $false
    authorizationServicesEnabled = $false
    fullScopeAllowed = $true

    # URLs permitidas - IMPORTANTE para CORS
    rootUrl = "http://172.20.147.108:30081"
    baseUrl = "http://172.20.147.108:30081"
    redirectUris = @(
        "http://172.20.147.108:30081/*",
        "http://localhost:5173/*"
    )
    webOrigins = @(
        "http://172.20.147.108:30081",
        "http://localhost:5173",
        "+"  # Permite cualquier origen (solo para desarrollo)
    )

    # ATRIBUTOS CLAVE: DESACTIVAR PKCE
    attributes = @{
        # ⚠️ CRÍTICO: No incluir pkce.code.challenge.method para desactivar PKCE
        # Si se incluye, Keycloak lo forzará en versiones 23+

        "post.logout.redirect.uris" = "http://172.20.147.108:30081/*"
        "oauth2.device.authorization.grant.enabled" = "false"
        "oidc.ciba.grant.enabled" = "false"
        "backchannel.logout.session.required" = "false"
        "backchannel.logout.revoke.offline.tokens" = "false"
        "use.refresh.tokens" = "true"
        "client_credentials.use_refresh_token" = "false"

        # Configuración de acceso
        "access.token.lifespan" = "1800"  # 30 minutos
        "client.session.idle.timeout" = "1800"
        "client.session.max.lifespan" = "36000"
    }
}

try {
    $body = $clientConfig | ConvertTo-Json -Depth 10

    Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients/$CLIENT_UUID" `
        -Method Put `
        -Headers $headers `
        -Body $body `
        -ContentType "application/json"

    Write-Host "✅ Cliente actualizado exitosamente SIN PKCE" -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR al actualizar cliente: $_" -ForegroundColor Red
    Write-Host "Detalles: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 4. Verificar configuración actual
Write-Host ""
Write-Host "5. Verificando configuración aplicada..." -ForegroundColor Yellow
try {
    $clientUpdated = Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients/$CLIENT_UUID" `
        -Method Get `
        -Headers $headers

    Write-Host "✅ Configuración verificada:" -ForegroundColor Green
    Write-Host "  - Standard Flow: $($clientUpdated.standardFlowEnabled)" -ForegroundColor Cyan
    Write-Host "  - Implicit Flow: $($clientUpdated.implicitFlowEnabled)" -ForegroundColor Cyan
    Write-Host "  - Public Client: $($clientUpdated.publicClient)" -ForegroundColor Cyan

    if ($clientUpdated.attributes."pkce.code.challenge.method") {
        Write-Host "  ⚠️  PKCE detectado: $($clientUpdated.attributes.'pkce.code.challenge.method')" -ForegroundColor Yellow
    } else {
        Write-Host "  ✅ PKCE: DESACTIVADO (correcto para HTTP)" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️  No se pudo verificar la configuración" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== CONFIGURACIÓN COMPLETADA ===" -ForegroundColor Green
Write-Host ""
Write-Host "📋 SIGUIENTES PASOS:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Reconstruir y redesplegar el frontend:" -ForegroundColor Cyan
Write-Host "   cd frontend" -ForegroundColor White
Write-Host "   .\rebuild-and-redeploy.ps1" -ForegroundColor White
Write-Host ""
Write-Host "2. Limpiar caché del navegador (Ctrl + Shift + Delete)" -ForegroundColor Cyan
Write-Host ""
Write-Host "3. Abrir la aplicación:" -ForegroundColor Cyan
Write-Host "   http://172.20.147.108:30081" -ForegroundColor White
Write-Host ""
Write-Host "4. Verificar en la consola del navegador (F12) que NO aparezca:" -ForegroundColor Cyan
Write-Host "   'Web Crypto API is not available'" -ForegroundColor White
Write-Host ""


