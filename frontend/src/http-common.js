import axios from "axios";
import keycloak from "./auth/keycloak";

const isProduction = import.meta.env.PROD;

// Para Kubernetes/Minikube, obtener IP dinámicamente o configurar manualmente
const minikubeIP = import.meta.env.VITE_MINIKUBE_IP || '192.168.59.100'; // Por defecto, cambiar según tu minikube ip
const apiGatewayPort = import.meta.env.VITE_API_GATEWAY_PORT || '30080'; // NodePort del API Gateway

console.log('Modo de producción:', isProduction);
console.log('Minikube IP:', minikubeIP);
console.log('API Gateway Port:', apiGatewayPort);

// En producción (Kubernetes), usar el API Gateway
// En desarrollo, usar el proxy de Vite
const baseURL = isProduction
    ? `http://${minikubeIP}:${apiGatewayPort}`
    : ''; // Proxy de Vite maneja las rutas /api

console.log('URL base configurada:', baseURL || 'PROXY VITE (Dev Mode)');

const api = axios.create({
    baseURL,
    headers: {
        'Content-Type': 'application/json'
    }
});

api.interceptors.request.use(async (config) => {
    console.log('=== DEBUG AUTH ===');
    console.log('Keycloak authenticated:', keycloak.authenticated);

    if (keycloak.authenticated) {
        await keycloak.updateToken(30);
        config.headers.Authorization = `Bearer ${keycloak.token}`;

        if (keycloak.tokenParsed) {
            console.log('Usuario:', keycloak.tokenParsed.preferred_username);
            console.log('Roles del cliente:', keycloak.tokenParsed.resource_access);
            console.log('Roles del realm:', keycloak.tokenParsed.realm_access);
        }

        console.log('Authorization header agregado');
    } else {
        console.log('Usuario NO autenticado');
    }

    console.log('Request URL:', config.url);
    console.log('==================');

    return config;
}, (error) => {
    console.error('Error en interceptor:', error);
    return Promise.reject(error);
});

export default api;