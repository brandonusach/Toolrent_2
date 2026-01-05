// Este archivo será reemplazado en runtime con las variables de entorno correctas
window.ENV_CONFIG = {
  VITE_MINIKUBE_IP: '__VITE_MINIKUBE_IP__',
  VITE_API_GATEWAY_PORT: '__VITE_API_GATEWAY_PORT__',
  VITE_KEYCLOAK_URL: '__VITE_KEYCLOAK_URL__',
  VITE_KEYCLOAK_REALM: '__VITE_KEYCLOAK_REALM__',
  VITE_KEYCLOAK_CLIENT_ID: '__VITE_KEYCLOAK_CLIENT_ID__'
};

// Valores por defecto si no se reemplazan (para Kubernetes con Minikube)
if (window.ENV_CONFIG.VITE_MINIKUBE_IP === '__VITE_MINIKUBE_IP__') {
  window.ENV_CONFIG = {
    VITE_MINIKUBE_IP: '172.20.147.108',
    VITE_API_GATEWAY_PORT: '30080',
    VITE_KEYCLOAK_URL: 'http://172.20.147.108:30090',
    VITE_KEYCLOAK_REALM: 'ToolRent',
    VITE_KEYCLOAK_CLIENT_ID: 'toolrent-client'
  };
}

