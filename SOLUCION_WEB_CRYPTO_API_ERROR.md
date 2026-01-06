# Solución: Error "Web Crypto API is not available"

## 🔴 Problema
```
Keycloak Error: Error: Web Crypto API is not available.
```

Este error ocurre porque:
- **Keycloak 23+** con **Standard Flow** habilita **PKCE** por defecto
- **PKCE** requiere obligatoriamente la **Web Crypto API**
- La Web Crypto API **solo está disponible** en:
  - **HTTPS** (cualquier dominio)
  - **HTTP** con **localhost** (127.0.0.1 o localhost)
- Al usar **HTTP con IP** (ej: http://172.20.147.108), el navegador **bloquea** la API

---

## ✅ Solución Implementada: Opción B (Implicit Flow)

**Se cambió el flujo de autenticación a `implicit` en `frontend/src/main.jsx`**

### ✨ Ventajas
- ✅ No requiere Web Crypto API
- ✅ Funciona en HTTP con cualquier IP
- ✅ No necesita configuración del navegador
- ✅ Solución permanente en el código

### ⚠️ Consideraciones
- Menos seguro que Standard Flow con PKCE
- **Recomendado solo para desarrollo**
- Para producción, usar HTTPS con Standard Flow

### 📝 Cambio realizado
```javascript
const initOptions = {
    onLoad: 'check-sso',
    checkLoginIframe: false,
    flow: 'implicit',        // ← CAMBIO: De 'standard' a 'implicit'
    enableLogging: true,
};
```

---

## 🔄 Cómo Aplicar el Cambio

### Opción 1: Redesplegar Solo Frontend (Rápido - 1-2 minutos)
```powershell
cd frontend
.\rebuild-and-redeploy.ps1
```

### Opción 2: Redesplegar Todo (Completo - 5-10 minutos)
```powershell
cd microservicios
.\redeploy-gateway-keycloak.ps1
```

---

## 🔧 Solución Alternativa: Opción A (Configuración del Navegador)

Si prefieres mantener **Standard Flow** (más seguro), puedes habilitar Web Crypto API en el navegador:

### Chrome/Edge
1. Ir a: `chrome://flags/#unsafely-treat-insecure-origin-as-secure`
2. Habilitar la opción (**Enabled**)
3. Agregar tus URLs:
   ```
   http://172.20.147.108:30081,http://172.20.147.108:30090
   ```
4. Hacer clic en **Relaunch** (reiniciar navegador)

### Firefox
1. Ir a: `about:config`
2. Buscar: `dom.securecontext.allowlist`
3. Agregar tus IPs separadas por comas

### ⚠️ Nota
- Debes repetir esto en **cada navegador** y **cada máquina**
- La configuración se pierde si cambias de navegador
- Por eso la **Opción B (código)** es más práctica

---

## 🎯 Recomendación Final

### Para Desarrollo (actual)
✅ **Usar Implicit Flow** (ya implementado)
- Más fácil de trabajar
- No requiere configuración extra
- Suficientemente seguro para desarrollo local

### Para Producción (futuro)
✅ **Usar Standard Flow + HTTPS**
```javascript
const initOptions = {
    onLoad: 'check-sso',
    checkLoginIframe: false,
    flow: 'standard',
    enableLogging: false,
};
```
- Configurar certificados SSL/TLS
- Usar dominio propio
- PKCE funcionará automáticamente

---

## 🚀 Próximos Pasos

1. **Reconstruir y redesplegar el frontend**:
   ```powershell
   cd frontend
   .\rebuild-and-redeploy.ps1
   ```

2. **Verificar que funcione**:
   - Abrir: `http://<MINIKUBE_IP>:30081`
   - Hacer clic en "Iniciar Sesión"
   - Debería redirigir a Keycloak sin errores

3. **Si persiste el error**:
   - Limpiar caché del navegador (Ctrl + Shift + Delete)
   - Cerrar y reabrir el navegador
   - Verificar que Keycloak esté corriendo: `kubectl get pods -n toolrent`

---

## 📚 Referencias
- [Keycloak PKCE Support](https://www.keycloak.org/docs/latest/securing_apps/#_proof-key-for-code-exchange-pkce-support)
- [Web Crypto API MDN](https://developer.mozilla.org/en-US/docs/Web/API/Web_Crypto_API)
- [Secure Contexts MDN](https://developer.mozilla.org/en-US/docs/Web/Security/Secure_Contexts)

---

**Fecha**: 2026-01-05  
**Estado**: ✅ Solucionado con Implicit Flow

