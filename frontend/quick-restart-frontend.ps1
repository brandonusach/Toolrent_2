# Script rápido para actualizar solo el ConfigMap y reiniciar el frontend
# (asumiendo que la imagen ya está construida y cargada)

Write-Host "Actualizando ConfigMap y reiniciando frontend..." -ForegroundColor Cyan

# Actualizar el ConfigMap
kubectl apply -f ..\microservicios\k8s\frontend-hotfix-configmap.yml

# Reiniciar el deployment del frontend
kubectl rollout restart deployment frontend -n toolrent

# Esperar a que el deployment esté listo
Write-Host "Esperando a que el deployment esté listo..." -ForegroundColor Yellow
kubectl rollout status deployment frontend -n toolrent --timeout=120s

Write-Host "✓ Completado" -ForegroundColor Green

