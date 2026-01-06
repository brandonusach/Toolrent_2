import React from "react";
import ReactDOM from "react-dom/client";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import keycloak from "./auth/keycloak.js";
import App from "./App";
import "./index.css";

// Configuración de inicialización de Keycloak para HTTP (sin HTTPS)
// SOLUCIÓN DEFINITIVA para evitar error "Web Crypto API is not available":
//
// En HTTP (sin HTTPS), el navegador NO permite usar Web Crypto API.
// Keycloak 21+ con Standard Flow intenta usar PKCE, que REQUIERE Web Crypto API.
// SOLUCIÓN: Deshabilitar PKCE explícitamente con flow: 'implicit'
//
// IMPORTANTE: En versión 21.1.2, el parámetro 'flow' sí es reconocido.
// Si usas versión 23+, este parámetro puede ser ignorado.
const initOptions = {
    onLoad: 'check-sso',          // Verifica SSO sin forzar login inmediato
    checkLoginIframe: false,       // CRÍTICO: Desactiva iframe para evitar problemas CORS y cookies
    flow: 'implicit',              // CRÍTICO: Usar Implicit Flow (no requiere PKCE ni Web Crypto API)
    enableLogging: true,           // Habilitar logging para debug
};

ReactDOM.createRoot(document.getElementById('root')).render(
    <ReactKeycloakProvider
        authClient={keycloak}
        initOptions={initOptions}
        onEvent={(event, error) => {
            console.log('🔐 Keycloak Event:', event);
            if (error) {
                console.error('❌ Keycloak Error:', error);
            }
        }}
        onTokens={(tokens) => {
            console.log('🎫 Keycloak Tokens:', tokens ? 'Received' : 'None');
        }}
        LoadingComponent={
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh',
                backgroundColor: '#1a1a2e',
                color: 'white'
            }}>
                <div style={{ textAlign: 'center' }}>
                    <h2>Inicializando Keycloak...</h2>
                    <p>Por favor espera...</p>
                </div>
            </div>
        }
    >
        <App />
    </ReactKeycloakProvider>
)