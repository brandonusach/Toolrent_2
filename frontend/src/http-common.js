import axios from "axios";
import keycloak from "./auth/keycloak";

const isProduction = import.meta.env.PROD;

// Para Kubernetes/Minikube, obtener IP dinámicamente o configurar manualmente
const minikubeIP = import.meta.env.VITE_MINIKUBE_IP || '172.20.147.108'; // IP obtenida con: minikube ip
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
    console.error('Error en interceptor de request:', error);
    return Promise.reject(error);
});

// Interceptor de respuesta para manejar errores
api.interceptors.response.use(
    (response) => {
        // Si la respuesta es exitosa, retornarla
        console.log('✓ Respuesta exitosa:', response.config?.url);
        return response;
    },
    (error) => {
        // Mejorar el manejo de errores con validación segura
        console.error('=== ERROR EN PETICIÓN HTTP ===');

        // Validar que el error tenga la estructura esperada
        if (!error) {
            console.error('Error es null o undefined');
            const errorObj = new Error('Error desconocido en la petición');
            console.error('==============================');
            return Promise.reject(errorObj);
        }

        // Log básico del error
        console.error('Tipo de error:', typeof error);
        console.error('Error completo:', error);

        // Solo intentar acceder a config si existe
        if (error.config) {
            console.error('URL:', error.config.url);
            console.error('Método:', error.config.method);
        } else {
            console.error('Error sin config disponible');
        }

        if (error.response) {
            // El servidor respondió con un código de error
            console.error('Status:', error.response.status);
            console.error('Data:', error.response.data);
            console.error('Headers:', error.response.headers);
            error.message = error.response.data?.message ||
                           error.response.data?.error ||
                           `Error del servidor: ${error.response.status}`;
        } else if (error.request) {
            // La petición se hizo pero no hubo respuesta
            console.error('Request enviado pero sin respuesta');
            console.error('Request:', error.request);
            error.message = 'No se pudo conectar con el servidor. Verifica que el API Gateway esté corriendo.';
        } else {
            // Algo pasó al configurar la petición
            console.error('Error al configurar la petición:', error.message);
            error.message = error.message || 'Error desconocido al configurar la petición';
        }

        console.error('Mensaje final del error:', error.message);
        console.error('==============================');
        return Promise.reject(error);
    }
);

export default api;