#!/bin/sh

# Script para inyectar variables de entorno en el frontend en runtime
# Se ejecuta cuando el contenedor inicia

# Ruta del archivo de configuración
ENV_FILE="/usr/share/nginx/html/env-config.js"

# Reemplazar los placeholders con los valores reales de las variables de entorno
# Si no están definidas, usar valores por defecto

MINIKUBE_IP=${VITE_MINIKUBE_IP:-"172.20.147.108"}
API_GATEWAY_PORT=${VITE_API_GATEWAY_PORT:-"30080"}
KEYCLOAK_URL=${VITE_KEYCLOAK_URL:-"http://172.20.147.108:30090"}
KEYCLOAK_REALM=${VITE_KEYCLOAK_REALM:-"ToolRent"}
KEYCLOAK_CLIENT_ID=${VITE_KEYCLOAK_CLIENT_ID:-"toolrent-client"}

echo "Configurando variables de entorno del frontend..."
echo "MINIKUBE_IP: $MINIKUBE_IP"
echo "API_GATEWAY_PORT: $API_GATEWAY_PORT"
echo "KEYCLOAK_URL: $KEYCLOAK_URL"
echo "KEYCLOAK_REALM: $KEYCLOAK_REALM"
echo "KEYCLOAK_CLIENT_ID: $KEYCLOAK_CLIENT_ID"

# Crear el archivo de configuración con los valores reales
cat > $ENV_FILE << EOF
window.ENV_CONFIG = {
  VITE_MINIKUBE_IP: '$MINIKUBE_IP',
  VITE_API_GATEWAY_PORT: '$API_GATEWAY_PORT',
  VITE_KEYCLOAK_URL: '$KEYCLOAK_URL',
  VITE_KEYCLOAK_REALM: '$KEYCLOAK_REALM',
  VITE_KEYCLOAK_CLIENT_ID: '$KEYCLOAK_CLIENT_ID'
};
EOF

echo "Configuración completada."

# Iniciar nginx
exec nginx -g 'daemon off;'

