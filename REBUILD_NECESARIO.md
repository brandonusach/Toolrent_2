# ⚠️ RESPUESTA RÁPIDA: ¿Necesito reconstruir?

## Frontend: ✅ SÍ, NECESITA REBUILD

**¿Por qué?**
- Las variables de entorno cambiaron (Realm, Client ID, IP de Minikube)
- Los archivos `.env` se incluyen en la imagen Docker
- El `docker-entrypoint.sh` tiene nuevos valores por defecto

**Cómo hacer rebuild:**
```powershell
# Opción 1: Script automático (RECOMENDADO)
cd C:\Users\brand\Desktop\TINGESO\toolrent\frontend
.\rebuild-and-deploy.ps1

# Opción 2: Manual
docker build -t brandonusach/toolrent-frontend:latest .
minikube image load brandonusach/toolrent-frontend:latest
kubectl rollout restart deployment/frontend -n toolrent
```

---

## Keycloak: ❌ NO NECESITA REBUILD

**¿Por qué?**
- Keycloak es una imagen oficial de Quay.io
- Solo necesita configuración manual en su UI
- El deployment ya está configurado correctamente

**Lo que SÍ necesitas hacer:**
1. Verificar que esté corriendo:
   ```powershell
   .\check-keycloak.ps1
   ```

2. Configurar manualmente en http://172.20.147.108:30090:
   - Login: admin/admin
   - Crear realm: **ToolRent** (con mayúsculas)
   - Crear client: **toolrent-client**
     - Access Type: public
     - Valid Redirect URIs: `http://172.20.147.108:30081/*`
     - Web Origins: `http://172.20.147.108:30081`

---

## Resumen

| Componente | ¿Rebuild? | Acción |
|------------|-----------|--------|
| Frontend   | ✅ SÍ     | Ejecutar `rebuild-and-deploy.ps1` |
| Keycloak   | ❌ NO     | Ejecutar `check-keycloak.ps1` y configurar UI |

**Tiempo estimado:**
- Frontend rebuild + deploy: ~5 minutos
- Configuración de Keycloak: ~5 minutos
- **Total: ~10 minutos**

## Comando único para todo:
```powershell
# 1. Rebuild frontend
cd C:\Users\brand\Desktop\TINGESO\toolrent\frontend
.\rebuild-and-deploy.ps1

# 2. Verificar Keycloak
cd ..
.\check-keycloak.ps1

# 3. Acceder a configurar Keycloak
start http://172.20.147.108:30090
```

