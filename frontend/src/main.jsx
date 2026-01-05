import React from "react";
import ReactDOM from "react-dom/client";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import keycloak from "./auth/keycloak.js";
import App from "./App";
import "./index.css";

// Configuración de inicialización de Keycloak
const initOptions = {
    onLoad: 'check-sso',      // Verifica SSO sin forzar login inmediato
    checkLoginIframe: false,  // Desactiva iframe para evitar problemas
    flow: 'implicit',         // Usar flujo implicit para evitar PKCE y Web Crypto API
    enableLogging: true,      // Habilitar logging para debug
    silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
};

ReactDOM.createRoot(document.getElementById('root')).render(
    <ReactKeycloakProvider
        authClient={keycloak}
        initOptions={initOptions}
        onEvent={(event, error) => {
            console.log('Keycloak event:', event, error);
        }}
        onTokens={(tokens) => {
            console.log('Keycloak tokens:', tokens);
        }}
    >
        <App />
    </ReactKeycloakProvider>
)