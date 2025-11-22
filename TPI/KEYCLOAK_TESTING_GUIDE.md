# Guía de Prueba de Keycloak - Rol TRANSPORTISTA

Esta guía explica paso a paso cómo probar que Keycloak está funcionando correctamente, específicamente para el rol de TRANSPORTISTA.

## 📋 Requisitos Previos

1. Docker y Docker Compose instalados
2. Postman instalado (o cualquier cliente HTTP)
3. Los servicios levantados con `docker-compose up`

## 🚀 Paso 1: Levantar los Servicios

```bash
cd TPI
docker-compose up -d
```

Verifica que los servicios estén corriendo:
```bash
docker-compose ps
```

Deberías ver:
- `keycloak` en puerto 8080
- `solicitudtranslado` en puerto 8082
- `clientes` en puerto 8081

## 🔐 Paso 2: Configurar Keycloak

### 2.1 Acceder a Keycloak Admin Console

1. Abre tu navegador y ve a: `http://localhost:8080`
2. Haz clic en "Administration Console"
3. Inicia sesión con las credenciales de administrador:
   - **Usuario**: `admin`
   - **Contraseña**: `admin123`

### 2.2 Crear el Realm

1. En el menú desplegable superior izquierdo (donde dice "master"), haz clic
2. Selecciona "Create Realm"
3. Configura:
   - **Realm name**: `contenedores-app`
4. Haz clic en "Create"

### 2.3 Crear el Cliente (Client)

1. En el menú lateral, ve a "Clients"
2. Haz clic en "Create client"
3. En la pestaña "General Settings":
   - **Client type**: `OpenID Connect`
   - **Client ID**: `contenedores-api`
   - Haz clic en "Next"
4. En la pestaña "Capability config":
   - Activa: `Client authentication`
   - Activa: `Authorization`
   - Activa: `Standard flow`
   - Activa: `Direct access grants` (importante para obtener tokens con Postman)
   - Haz clic en "Next"
5. En la pestaña "Login settings":
   - **Root URL**: `http://localhost:8082`
   - **Valid redirect URIs**: `http://localhost:8082/*`
   - **Web origins**: `*`
   - Haz clic en "Save"

### 2.4 Obtener el Client Secret

1. Ve al cliente `contenedores-api` recién creado
2. Ve a la pestaña "Credentials"
3. Copia el **Client Secret** (lo necesitarás más adelante)
4. Actualiza el archivo `application.properties` con este secret:
   ```properties
   keycloak.credentials.secret=TU_CLIENT_SECRET_COPIADO
   ```

### 2.5 Crear Roles

1. En el menú lateral, ve a "Realm roles"
2. Haz clic en "Create role"
3. Crea los siguientes roles uno por uno:
   - **Role name**: `TRANSPORTISTA`
   - **Role name**: `CLIENTE`
   - **Role name**: `OPERADOR`

### 2.6 Crear Usuario TRANSPORTISTA

1. En el menú lateral, ve a "Users"
2. Haz clic en "Add user"
3. Configura el usuario:
   - **Username**: `transportista1`
   - **Email**: `transportista1@example.com`
   - **First name**: `Juan`
   - **Last name**: `Perez`
   - **Email verified**: `ON`
   - Haz clic en "Create"

4. **Configurar contraseña**:
   - Ve a la pestaña "Credentials"
   - Haz clic en "Set password"
   - **Password**: `transportista123`
   - **Password confirmation**: `transportista123`
   - Desactiva "Temporary" (para que no pida cambiar la contraseña)
   - Haz clic en "Save"
   - Confirma "Save password"

5. **Asignar rol**:
   - Ve a la pestaña "Role mapping"
   - Haz clic en "Assign role"
   - Filtra por "Filter by realm roles"
   - Selecciona `TRANSPORTISTA`
   - Haz clic en "Assign"

6. **Agregar atributos personalizados** (opcional pero recomendado):
   - Ve a la pestaña "Attributes"
   - Haz clic en "Add an attribute"
   - **Key**: `dominio_camion`
   - **Value**: `ABC123` (dominio del camión asignado)
   - Haz clic en "Save"

### 2.7 Configurar Mappers para incluir roles en el token

1. Ve a "Clients" > `contenedores-api`
2. Ve a la pestaña "Client scopes"
3. Haz clic en `contenedores-api-dedicated`
4. Haz clic en "Add mapper" > "By configuration"
5. Selecciona "User Realm Role"
6. Configura:
   - **Name**: `realm-roles`
   - **Token Claim Name**: `realm_access.roles`
   - **Claim JSON Type**: `String`
   - **Add to ID token**: `ON`
   - **Add to access token**: `ON`
   - **Add to userinfo**: `ON`
7. Haz clic en "Save"

8. **Agregar mapper para dominio_camion** (si agregaste el atributo):
   - Haz clic en "Add mapper" > "By configuration"
   - Selecciona "User Attribute"
   - Configura:
     - **Name**: `dominio-camion-mapper`
     - **User Attribute**: `dominio_camion`
     - **Token Claim Name**: `dominio_camion`
     - **Claim JSON Type**: `String`
     - **Add to ID token**: `ON`
     - **Add to access token**: `ON`
     - **Add to userinfo**: `ON`
   - Haz clic en "Save"

## 📬 Paso 3: Obtener Token con Postman

### 3.1 Configurar la Petición en Postman

1. Abre Postman
2. Crea una nueva petición `POST`
3. **URL**: `http://localhost:8080/realms/contenedores-app/protocol/openid-connect/token`

### 3.2 Configurar Body

1. Selecciona la pestaña "Body"
2. Selecciona `x-www-form-urlencoded`
3. Agrega los siguientes parámetros:

| Key            | Value                  |
|----------------|------------------------|
| client_id      | contenedores-api       |
| client_secret  | (tu client secret)     |
| username       | transportista1         |
| password       | transportista123       |
| grant_type     | password               |

### 3.3 Enviar la Petición

1. Haz clic en "Send"
2. Deberías recibir una respuesta JSON similar a:

```json
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI...",
    "expires_in": 300,
    "refresh_expires_in": 1800,
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI...",
    "token_type": "Bearer",
    "not-before-policy": 0,
    "session_state": "...",
    "scope": "profile email"
}
```

3. **Copia el valor de `access_token`** (lo necesitarás para las siguientes peticiones)

### 3.4 Verificar el Token (Opcional)

Puedes verificar el contenido del token en [jwt.io](https://jwt.io):
1. Ve a https://jwt.io
2. Pega el access_token en el campo "Encoded"
3. Verifica que en el payload aparezca:
   - `realm_access.roles` contenga `["TRANSPORTISTA"]`
   - `dominio_camion` tenga el valor configurado (si lo agregaste)

## 🧪 Paso 4: Probar los Endpoints del TRANSPORTISTA

Según la configuración de seguridad, el rol TRANSPORTISTA tiene acceso a los siguientes endpoints:

### Endpoints Disponibles para TRANSPORTISTA

#### 1. Obtener Mis Tramos
**Endpoint**: `GET /api/transportista/mis-tramos`

**Descripción**: Obtiene todos los tramos asignados al camión del transportista.

**Postman**:
1. Método: `GET`
2. URL: `http://localhost:8082/api/transportista/mis-tramos`
3. Headers:
   - **Key**: `Authorization`
   - **Value**: `Bearer TU_ACCESS_TOKEN_AQUI`
4. Enviar

**Respuesta esperada**:
```json
{
    "dominoCamion": "ABC123",
    "tramos": [
        {
            "id": 1,
            "origen": {...},
            "destino": {...},
            "fechaInicio": null,
            "fechaFin": null,
            ...
        }
    ],
    "cantidad": 1
}
```

#### 2. Obtener Tramos por Dominio
**Endpoint**: `GET /api/tramos/{dominio}`

**Descripción**: Obtiene los tramos de un camión específico por su dominio.

**Postman**:
1. Método: `GET`
2. URL: `http://localhost:8082/api/tramos/ABC123`
3. Headers:
   - **Key**: `Authorization`
   - **Value**: `Bearer TU_ACCESS_TOKEN_AQUI`
4. Enviar

**Respuesta esperada**:
```json
[
    {
        "id": 1,
        "origen": {...},
        "destino": {...},
        "camion": {
            "dominio": "ABC123",
            ...
        },
        ...
    }
]
```

#### 3. Obtener Información de Mi Camión
**Endpoint**: `GET /api/transportista/mi-camion`

**Descripción**: Obtiene la información del camión asignado al transportista.

**Postman**:
1. Método: `GET`
2. URL: `http://localhost:8082/api/transportista/mi-camion`
3. Headers:
   - **Key**: `Authorization`
   - **Value**: `Bearer TU_ACCESS_TOKEN_AQUI`
4. Enviar

#### 4. Iniciar un Tramo
**Endpoint**: `PUT /api/transportista/tramos/{id}/iniciar`

**Descripción**: Registra la fecha de inicio de un tramo.

**Postman**:
1. Método: `PUT`
2. URL: `http://localhost:8082/api/transportista/tramos/1/iniciar`
3. Headers:
   - **Key**: `Authorization`
   - **Value**: `Bearer TU_ACCESS_TOKEN_AQUI`
4. Enviar

**Respuesta esperada**:
```json
{
    "mensaje": "Tramo iniciado exitosamente",
    "tramoId": 1,
    "fechaInicio": "2025-01-15",
    "origen": "Buenos Aires",
    "destino": "Córdoba"
}
```

#### 5. Finalizar un Tramo
**Endpoint**: `PUT /api/transportista/tramos/{id}/finalizar`

**Descripción**: Registra la fecha de finalización de un tramo.

**Postman**:
1. Método: `PUT`
2. URL: `http://localhost:8082/api/transportista/tramos/1/finalizar`
3. Headers:
   - **Key**: `Authorization`
   - **Value**: `Bearer TU_ACCESS_TOKEN_AQUI`
4. Enviar

**Respuesta esperada**:
```json
{
    "mensaje": "Tramo finalizado exitosamente",
    "tramoId": 1,
    "fechaInicio": "2025-01-15",
    "fechaFin": "2025-01-16",
    "origen": "Buenos Aires",
    "destino": "Córdoba"
}
```

## ❌ Probar Acceso Denegado

Para verificar que la seguridad funciona correctamente, intenta acceder a un endpoint de OPERADOR:

**Endpoint**: `GET /api/ciudades`

**Postman**:
1. Método: `GET`
2. URL: `http://localhost:8082/api/ciudades`
3. Headers:
   - **Key**: `Authorization`
   - **Value**: `Bearer TU_ACCESS_TOKEN_AQUI`
4. Enviar

**Respuesta esperada**:
```json
{
    "error": "Forbidden",
    "status": 403,
    "message": "Access Denied"
}
```

Esto confirma que el usuario TRANSPORTISTA no tiene acceso a endpoints de OPERADOR.

## 🔍 Verificación de Seguridad

### Matriz de Permisos por Rol

| Endpoint | TRANSPORTISTA | OPERADOR | CLIENTE |
|----------|--------------|----------|---------|
| `GET /api/transportista/mis-tramos` | ✅ | ❌ | ❌ |
| `GET /api/tramos/{dominio}` | ✅ | ❌ | ❌ |
| `PUT /api/tramos/{id}/iniciar` | ✅ | ❌ | ❌ |
| `PUT /api/tramos/{id}/finalizar` | ✅ | ❌ | ❌ |
| `GET /api/ciudades/**` | ❌ | ✅ | ❌ |
| `GET /api/depositos/**` | ❌ | ✅ | ❌ |
| `POST /api/solicitudes/**` | ❌ | ❌ | ✅ |

## 🐛 Troubleshooting

### Error: "401 Unauthorized"
- Verifica que el token no haya expirado (expira en 5 minutos por defecto)
- Asegúrate de incluir el header `Authorization: Bearer TOKEN`
- Obtén un nuevo token si es necesario

### Error: "403 Forbidden"
- Verifica que el usuario tenga el rol correcto asignado en Keycloak
- Confirma que el rol esté incluido en el token (verifica en jwt.io)
- Asegúrate de que el mapper de roles esté configurado correctamente

### Error: "No se pudo identificar el camión del transportista"
- Verifica que el atributo `dominio_camion` esté configurado en el usuario
- Confirma que el mapper del atributo esté creado y activo
- Verifica que el claim `dominio_camion` esté en el token

### El token no incluye los roles
- Verifica que el mapper `realm-roles` esté configurado correctamente
- Asegúrate de que el claim name sea exactamente `realm_access.roles`
- Revisa que el mapper esté en el scope del cliente

### Error de conexión a Keycloak
- Verifica que el contenedor de Keycloak esté corriendo: `docker ps`
- Comprueba que el puerto 8080 no esté ocupado por otro proceso
- Revisa los logs: `docker logs keycloak`

## 📝 Notas Importantes

1. **Expiración del Token**: El access_token expira en 5 minutos por defecto. Usa el `refresh_token` para obtener un nuevo token sin volver a autenticarte.

2. **Configuración de Producción**: En producción, NUNCA uses `grant_type=password`. Usa el flujo de Authorization Code con PKCE.

3. **Client Secret**: Mantén el client secret seguro y no lo compartas en repositorios públicos.

4. **HTTPS**: En producción, asegúrate de usar HTTPS para todas las comunicaciones con Keycloak.

5. **Realm Name**: El nombre del realm en `application.properties` debe coincidir exactamente con el configurado en Keycloak. El URL que mencionaste `http://localhost:8080/realms/backend-containers/account/` sugiere que el realm se llama `backend-containers`, pero en la configuración actual está como `contenedores-app`. Asegúrate de usar el mismo nombre en todas partes.

## 🔄 Actualizar Configuración del Realm (Si es necesario)

Si el realm en Keycloak se llama `backend-containers` (como indica tu URL), actualiza el archivo `application.properties`:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/backend-containers
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/backend-containers/protocol/openid-connect/certs

keycloak.auth-server-url=http://localhost:8080
keycloak.realm=backend-containers
keycloak.resource=contenedores-api
keycloak.credentials.secret=TU_CLIENT_SECRET_AQUI
```

Y en Postman, usa:
```
POST http://localhost:8080/realms/backend-containers/protocol/openid-connect/token
```

## 📚 Referencias

- [Documentación oficial de Keycloak](https://www.keycloak.org/documentation)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [JWT.io - Decodificador de tokens](https://jwt.io)
