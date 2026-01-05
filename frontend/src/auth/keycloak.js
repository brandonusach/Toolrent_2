import Keycloak from 'keycloak-js';

// Función para obtener la configuración desde window.ENV_CONFIG o import.meta.env
const getEnvConfig = (key, defaultValue) => {
    // Primero intentar obtener desde window.ENV_CONFIG (runtime)
    if (window.ENV_CONFIG && window.ENV_CONFIG[key] && !window.ENV_CONFIG[key].startsWith('__')) {
        return window.ENV_CONFIG[key];
    }
    // Sino, usar import.meta.env (build time)
    return import.meta.env[key] || defaultValue;
};

const keycloakConfig = {
    url: getEnvConfig('VITE_KEYCLOAK_URL', 'http://172.20.147.108:30090'),
    realm: getEnvConfig('VITE_KEYCLOAK_REALM', 'ToolRent'),
    clientId: getEnvConfig('VITE_KEYCLOAK_CLIENT_ID', 'toolrent-client')
};

console.log('Keycloak Config:', keycloakConfig);

const keycloak = new Keycloak(keycloakConfig);

export default keycloak;
