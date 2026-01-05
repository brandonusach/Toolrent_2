import Keycloak from 'keycloak-js';

const keycloakConfig = {
    url: import.meta.env.VITE_KEYCLOAK_URL || 'http://172.20.147.108:30090',
    realm: import.meta.env.VITE_KEYCLOAK_REALM || 'ToolRent',
    clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'toolrent-client'
};

console.log('Keycloak Config:', keycloakConfig);

const keycloak = new Keycloak(keycloakConfig);

export default keycloak;