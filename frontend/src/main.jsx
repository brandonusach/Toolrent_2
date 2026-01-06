import React from "react";
import ReactDOM from "react-dom/client";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import keycloak from "./auth/keycloak.js";
import App from "./App";
import "./index.css";

// Configuración de inicialización de Keycloak para HTTP (sin HTTPS)
// SOLUCIÓN para evitar error "Web Crypto API is not available":
// 1. Usar flujo 'standard' (authorization code) - más seguro que implicit
// 2. NO especificar pkceMethod - Keycloak automáticamente NO usará PKCE en HTTP
// 3. checkLoginIframe: false - evita problemas con cookies de terceros
const initOptions = {
    onLoad: 'check-sso',          // Verifica SSO sin forzar login inmediato
    checkLoginIframe: false,       // CRÍTICO: Desactiva iframe para evitar problemas CORS y cookies
    flow: 'standard',              // Flujo authorization code
    enableLogging: true,           // Habilitar logging para debug
    // NO incluir pkceMethod aquí - causaría error en HTTP
    // Keycloak automáticamente no usará PKCE cuando detecta HTTP
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