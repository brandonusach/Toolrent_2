# 🏢 ToolRent - Proyecto Monolítico

Versión original del proyecto con arquitectura monolítica.

---

## 📁 Estructura

```
monolito/
├── backend/              # Backend Spring Boot
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/             # Frontend React
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── compose.yml           # Docker Compose
├── nginx-backend.conf    # Configuración Nginx Backend
├── nginx-frontend.conf   # Configuración Nginx Frontend
└── .env                  # Variables de entorno
```

---

## 🚀 Cómo ejecutar

### 1. Configurar variables de entorno

Edita el archivo `.env`:

```bash
# .env
HOST_IP=192.168.1.100              # Tu IP local
DB_HOST=192.168.1.100              # IP de PostgreSQL
DB_PORT=5432
DB_NAME=toolrent_db
DB_USERNAME=postgres
DB_PASSWORD=1234
KEYCLOAK_SERVER_URL=http://192.168.1.100:8080
KEYCLOAK_REALM=toolrent-realm
KEYCLOAK_CLIENT_ID=toolrent-frontend
```

### 2. Asegúrate de tener PostgreSQL corriendo

```bash
# PostgreSQL debe estar disponible en DB_HOST:DB_PORT
# Crear base de datos:
CREATE DATABASE toolrent_db;
```

### 3. (Opcional) Keycloak para autenticación

Si quieres usar autenticación con Keycloak:
```bash
# Keycloak debe estar en KEYCLOAK_SERVER_URL
# Crear realm: toolrent-realm
# Crear client: toolrent-frontend
```

### 4. Iniciar con Docker Compose

```powershell
cd C:\Users\brand\Desktop\TINGESO\toolrent\monolito
docker-compose up -d
```

### 5. Acceder a la aplicación

- **Frontend**: http://localhost:8070
- **Backend API**: http://localhost:8081
- **Backend directo**: http://localhost:8080

---

## 🐳 Comandos Docker Compose

```powershell
# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend
docker-compose logs -f frontend

# Detener servicios
docker-compose down

# Reiniciar servicios
docker-compose restart

# Reconstruir imágenes
docker-compose build

# Reconstruir y reiniciar
docker-compose up -d --build
```

---

## 📊 Arquitectura

```
Usuario → Nginx Frontend (8070) → Frontend React
           ↓
Usuario → Nginx Backend (8081) → Backend Spring Boot (8080)
                                    ↓
                                PostgreSQL (externo)
                                    ↓
                                Keycloak (externo, opcional)
```

### Componentes:

1. **Frontend React**
   - Puerto: 8070 (a través de Nginx)
   - Tecnología: React + Vite
   - Proxy: Nginx

2. **Backend Spring Boot**
   - Puerto: 8081 (a través de Nginx), 8080 (directo)
   - Tecnología: Spring Boot 3.2.0, Java 17
   - Base de datos: PostgreSQL
   - Arquitectura: Por capas (@Controller, @Service, @Repository, @Entity)

3. **PostgreSQL** (externo)
   - Base de datos compartida: toolrent_db

4. **Keycloak** (externo, opcional)
   - Autenticación y autorización

---

## 🔧 Desarrollo Local (sin Docker)

### Backend:
```powershell
cd backend
mvn spring-boot:run
```

### Frontend:
```powershell
cd frontend
npm install
npm run dev
```

---

## 📋 Endpoints del Backend

### Tools (Herramientas)
- `GET /api/v1/tools/` - Listar todas
- `GET /api/v1/tools/{id}` - Obtener por ID
- `POST /api/v1/tools/` - Crear
- `PUT /api/v1/tools/` - Actualizar
- `DELETE /api/v1/tools/{id}` - Eliminar

### Clients (Clientes)
- `GET /api/v1/clients/` - Listar todos
- `GET /api/v1/clients/{id}` - Obtener por ID
- `POST /api/v1/clients/` - Crear
- `PUT /api/v1/clients/` - Actualizar
- `DELETE /api/v1/clients/{id}` - Eliminar

### Loans (Préstamos)
- `GET /api/v1/loans/` - Listar todos
- `GET /api/v1/loans/{id}` - Obtener por ID
- `POST /api/v1/loans/` - Crear
- `PUT /api/v1/loans/{id}/return` - Devolver herramienta

### Categories, Rates, Fines, etc.
- Similar estructura REST

---

## ⚠️ Notas Importantes

1. **PostgreSQL externo**: Este docker-compose NO incluye PostgreSQL. Debes tenerlo corriendo externamente.

2. **Variables de entorno**: Edita `.env` con tu configuración local antes de ejecutar.

3. **Keycloak opcional**: Si no usas Keycloak, algunos endpoints seguirán funcionando sin autenticación.

4. **Puertos**: Asegúrate de que los puertos 8070, 8080, 8081 estén libres.

---

## 🔄 Migración a Microservicios

Si quieres ver la versión de microservicios, está en:
```
../microservicios/
```

Ver: [README principal](../README.md)

---

**Fecha:** 31 de Diciembre de 2025  
**Versión:** Monolito 1.0

