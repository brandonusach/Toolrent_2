import Keycloak from 'keycloak-js';

const keycloakConfig = {
    url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8090',
    realm: import.meta.env.VITE_KEYCLOAK_REALM || 'toolrent-realm',
    clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'toolrent-frontend'
};

console.log('Keycloak Config:', keycloakConfig);

const keycloak = new Keycloak(keycloakConfig);

export default keycloak;