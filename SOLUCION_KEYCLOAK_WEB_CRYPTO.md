# SOLUCIÓN: Error "Web Crypto API is not available" en Keycloak

## 🔴 PROBLEMA IDENTIFICADO

El frontend muestra el error:
```
❌ Keycloak Error: Web Crypto API is not available.
```

### Causa raíz:
1. **HTTP en lugar de HTTPS**: Estás accediendo a `http://172.20.147.108:30081` 
2. **Web Crypto API requiere HTTPS**: Los navegadores modernos solo exponen esta API en contextos seguros
3. **PKCE requiere Web Crypto API**: Keycloak intenta usar PKCE (Proof Key for Code Exchange) que necesita esta API
4. **Cookies de terceros bloqueadas**: El navegador bloquea cookies de Keycloak en iframes

## ✅ SOLUCIÓN IMPLEMENTADA

### Cambios en `frontend/src/main.jsx`:

```javascript
const initOptions = {
    onLoad: 'check-sso',          // Verifica SSO sin forzar login inmediato
    checkLoginIframe: false,       // CRÍTICO: Desactiva iframe para evitar problemas CORS y cookies
    flow: 'standard',              // Flujo authorization code (sin PKCE en HTTP)
    enableLogging: true,           // Habilitar logging para debug
    // NO incluir pkceMethod aquí - causaría error en HTTP
};
```

**Cambios clave:**
- ✅ `flow: 'standard'` - Usa Authorization Code Flow
- ✅ **NO incluir `pkceMethod`** - Evita el error de Web Crypto API
- ✅ `checkLoginIframe: false` - Evita problemas con cookies de terceros

### Configuración del cliente en Keycloak:

El cliente `toolrent-client` debe tener:
- ✅ `publicClient: true` - Cliente público (frontend)
- ✅ `standardFlowEnabled: true` - Authorization Code Flow habilitado
- ✅ `implicitFlowEnabled: false` - NO usar implicit flow
- ✅ URLs correctas de redirect y CORS

## 📋 PASOS PARA APLICAR LA SOLUCIÓN

### 1. Configurar el cliente de Keycloak
```powershell
cd C:\Users\brand\Desktop\TINGESO\toolrent
.\configure-keycloak-client.ps1
```

Este script configura automáticamente el cliente `toolrent-client` con las opciones correctas.

### 2. Reconstruir y redesplegar el frontend
```powershell
cd frontend
.\rebuild-and-redeploy.ps1
```

Este script:
1. Limpia el build anterior
2. Construye la aplicación
3. Crea la imagen Docker
4. La sube a Docker Hub
5. Reinicia el deployment en Kubernetes

### 3. Verificar el despliegue
```powershell
# Ver el estado de los pods
kubectl get pods -n toolrent

# Ver logs del frontend
kubectl logs -n toolrent -l app=frontend -f

# Acceder a la aplicación
# http://172.20.147.108:30081
```

## 🔍 VERIFICACIÓN

Después de aplicar los cambios, verifica en la consola del navegador:

### ✅ Mensajes esperados (CORRECTO):
```
🔐 Keycloak Event: onReady
🎫 Keycloak Tokens: Received  (si ya estás autenticado)
o
🎫 Keycloak Tokens: None  (si no estás autenticado todavía)
```

### ❌ NO deberías ver:
```
❌ Keycloak Error: Web Crypto API is not available
```

## 🎯 ALTERNATIVA: HTTPS con minikube tunnel

Si prefieres usar HTTPS (más seguro), puedes:

1. **Configurar un Ingress con certificados**:
   ```yaml
   apiVersion: networking.k8s.io/v1
   kind: Ingress
   metadata:
     name: frontend-ingress
     annotations:
       cert-manager.io/cluster-issuer: "letsencrypt-prod"
   spec:
     tls:
     - hosts:
       - toolrent.local
       secretName: toolrent-tls
   ```

2. **Usar mkcert para certificados locales**:
   ```powershell
   # Instalar mkcert
   choco install mkcert
   
   # Crear certificados
   mkcert -install
   mkcert 172.20.147.108
   ```

Pero para desarrollo local, **la solución HTTP sin PKCE es suficiente y más simple**.

## 📊 ARQUITECTURA ACTUALIZADA

```
┌─────────────────────────────────────────────────┐
│  Navegador (HTTP)                                │
│  http://172.20.147.108:30081                    │
└────────────┬────────────────────────────────────┘
             │
             │ Authorization Code Flow (sin PKCE)
             │
     ┌───────▼────────┐
     │   Keycloak     │
     │   :30090       │
     └───────┬────────┘
             │
             │ Token JWT
             │
     ┌───────▼────────┐
     │   Frontend     │
     │   (React)      │
     │   :30081       │
     └───────┬────────┘
             │
             │ API calls con Bearer token
             │
     ┌───────▼────────┐
     │  API Gateway   │
     │   :30080       │
     └───────┬────────┘
             │
     ┌───────▼────────────────────────────┐
     │  Microservicios                     │
     │  ms-clients, ms-loans, etc.        │
     └────────────────────────────────────┘
```

## 🐛 DEBUGGING

Si sigues teniendo problemas:

### 1. Ver logs del frontend
```powershell
kubectl logs -n toolrent -l app=frontend --tail=100
```

### 2. Ver logs de Keycloak
```powershell
kubectl logs -n toolrent -l app=keycloak --tail=100
```

### 3. Verificar conectividad
```powershell
# Desde dentro del pod del frontend
kubectl exec -n toolrent deployment/frontend -- curl -v http://keycloak:8090/health

# Desde tu máquina
curl -v http://172.20.147.108:30090/realms/ToolRent/.well-known/openid-configuration
```

### 4. Verificar variables de entorno en el frontend
```powershell
# Entrar al pod
kubectl exec -it -n toolrent deployment/frontend -- sh

# Ver las variables
cat /usr/share/nginx/html/env-config.js

# Debería mostrar:
# window.ENV_CONFIG = {
#   VITE_MINIKUBE_IP: '172.20.147.108',
#   VITE_API_GATEWAY_PORT: '30080',
#   VITE_KEYCLOAK_URL: 'http://172.20.147.108:30090',
#   VITE_KEYCLOAK_REALM: 'ToolRent',
#   VITE_KEYCLOAK_CLIENT_ID: 'toolrent-client'
# };
```

## 📚 REFERENCIAS

- [Keycloak JavaScript Adapter](https://www.keycloak.org/docs/latest/securing_apps/#_javascript_adapter)
- [Authorization Code Flow](https://oauth.net/2/grant-types/authorization-code/)
- [Web Crypto API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Crypto_API)
- [PKCE (RFC 7636)](https://datatracker.ietf.org/doc/html/rfc7636)

## ⚠️ NOTA DE SEGURIDAD

**HTTP sin PKCE NO es recomendado para producción**. Esta configuración es solo para desarrollo local.

Para producción:
1. ✅ Usa HTTPS con certificados válidos
2. ✅ Habilita PKCE (`pkceMethod: 'S256'`)
3. ✅ Configura CORS restrictivo
4. ✅ Usa tokens de corta duración
5. ✅ Implementa refresh tokens seguros

---

**Fecha**: 2026-01-05
**Autor**: GitHub Copilot
**Versión de Keycloak**: 23.0.0
**Versión de react-keycloak/web**: ^4.0.0

