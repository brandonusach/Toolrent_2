# Build stage
FROM node:20.19-bullseye as build

WORKDIR /app

# Copiar package files
COPY package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar código fuente
COPY . .

# IMPORTANTE: Declarar ARGs para recibir las variables en build time
ARG VITE_MINIKUBE_IP
ARG VITE_API_GATEWAY_PORT
ARG VITE_KEYCLOAK_URL
ARG VITE_KEYCLOAK_REALM
ARG VITE_KEYCLOAK_CLIENT_ID

# Convertir ARGs a ENVs para que Vite las pueda usar
ENV VITE_MINIKUBE_IP=$VITE_MINIKUBE_IP
ENV VITE_API_GATEWAY_PORT=$VITE_API_GATEWAY_PORT
ENV VITE_KEYCLOAK_URL=$VITE_KEYCLOAK_URL
ENV VITE_KEYCLOAK_REALM=$VITE_KEYCLOAK_REALM
ENV VITE_KEYCLOAK_CLIENT_ID=$VITE_KEYCLOAK_CLIENT_ID

# Build de la aplicación
RUN npm run build

# Production stage
FROM nginx:alpine

# Copiar los archivos buildeados
COPY --from=build /app/dist /usr/share/nginx/html

# Copiar configuración de nginx personalizada
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar el script de entrypoint para inyectar variables en runtime
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Copiar el template de configuración de entorno
COPY public/env-config.js /usr/share/nginx/html/env-config.js

EXPOSE 80

# Usar el script de entrypoint en lugar de nginx directamente
ENTRYPOINT ["/docker-entrypoint.sh"]
