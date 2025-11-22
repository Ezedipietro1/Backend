# Guía Rápida: Colección de Postman para Keycloak

Esta guía te ayudará a crear una colección en Postman para probar los endpoints protegidos con Keycloak.

## 🚀 Quick Start - 3 Pasos Rápidos

### 1️⃣ Obtener Token (Autenticación)

**Petición**: `POST http://localhost:8080/realms/contenedores-app/protocol/openid-connect/token`

**Body** (x-www-form-urlencoded):
```
client_id: contenedores-api
client_secret: (tu client secret de Keycloak)
username: transportista1
password: transportista123
grant_type: password
```

**Respuesta**:
```json
{
    "access_token": "eyJhbGci...",
    "expires_in": 300,
    "refresh_token": "eyJhbGci..."
}
```

### 2️⃣ Usar el Token

En todas las peticiones siguientes, agrega en **Headers**:
```
Authorization: Bearer eyJhbGci...TU_TOKEN_AQUI
```

### 3️⃣ Probar Endpoints

**Ejemplo**: `GET http://localhost:8082/api/transportista/mis-tramos`

---

## 📦 Colección Completa de Postman

### Variables de Entorno

Crea un Environment en Postman con estas variables:

| Variable | Initial Value | Current Value |
|----------|--------------|---------------|
| keycloak_url | http://localhost:8080 | http://localhost:8080 |
| realm | contenedores-app | contenedores-app |
| client_id | contenedores-api | contenedores-api |
| client_secret | (tu secret) | (tu secret) |
| username | transportista1 | transportista1 |
| password | transportista123 | transportista123 |
| api_url | http://localhost:8082 | http://localhost:8082 |
| access_token | | (se llenará automáticamente) |

### 1. Autenticación

#### 1.1 Obtener Token (Login)

```
POST {{keycloak_url}}/realms/{{realm}}/protocol/openid-connect/token

Body (x-www-form-urlencoded):
- client_id: {{client_id}}
- client_secret: {{client_secret}}
- username: {{username}}
- password: {{password}}
- grant_type: password

Tests (para guardar el token automáticamente):
```
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("access_token", jsonData.access_token);
    pm.environment.set("refresh_token", jsonData.refresh_token);
}
```

#### 1.2 Refrescar Token

```
POST {{keycloak_url}}/realms/{{realm}}/protocol/openid-connect/token

Body (x-www-form-urlencoded):
- client_id: {{client_id}}
- client_secret: {{client_secret}}
- refresh_token: {{refresh_token}}
- grant_type: refresh_token

Tests:
```
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("access_token", jsonData.access_token);
    pm.environment.set("refresh_token", jsonData.refresh_token);
}
```

### 2. Endpoints TRANSPORTISTA

Todas estas peticiones necesitan el header:
```
Authorization: Bearer {{access_token}}
```

#### 2.1 Obtener Mis Tramos

```
GET {{api_url}}/api/transportista/mis-tramos

Headers:
- Authorization: Bearer {{access_token}}
```

#### 2.2 Obtener Mi Camión

```
GET {{api_url}}/api/transportista/mi-camion

Headers:
- Authorization: Bearer {{access_token}}
```

#### 2.3 Obtener Tramos por Dominio

```
GET {{api_url}}/api/tramos/ABC123

Headers:
- Authorization: Bearer {{access_token}}
```

#### 2.4 Iniciar Tramo

```
PUT {{api_url}}/api/transportista/tramos/1/iniciar

Headers:
- Authorization: Bearer {{access_token}}
```

#### 2.5 Finalizar Tramo

```
PUT {{api_url}}/api/transportista/tramos/1/finalizar

Headers:
- Authorization: Bearer {{access_token}}
```

### 3. Pruebas de Seguridad

#### 3.1 Sin Token (Debe fallar con 401)

```
GET {{api_url}}/api/transportista/mis-tramos

Headers: (ninguno)
```

#### 3.2 Acceso Denegado (Debe fallar con 403)

```
GET {{api_url}}/api/ciudades

Headers:
- Authorization: Bearer {{access_token}}
```

---

## 🔧 Configuración de Authorization en Postman

### Método 1: Por Colección (Recomendado)

1. Click derecho en tu colección > Edit
2. Ve a la pestaña "Authorization"
3. Type: "Bearer Token"
4. Token: `{{access_token}}`
5. Todas las peticiones heredarán esta configuración

### Método 2: Por Petición

1. En cada petición, ve a "Authorization"
2. Type: "Bearer Token"
3. Token: `{{access_token}}`

---

## 📋 Checklist de Pruebas

### ✅ Autenticación
- [ ] Obtener token con usuario TRANSPORTISTA
- [ ] Verificar que el token contiene el rol TRANSPORTISTA (en jwt.io)
- [ ] Verificar que el token contiene dominio_camion
- [ ] Refrescar token usando refresh_token

### ✅ Endpoints TRANSPORTISTA (Deben funcionar)
- [ ] GET /api/transportista/mis-tramos
- [ ] GET /api/transportista/mi-camion
- [ ] GET /api/tramos/{dominio}
- [ ] PUT /api/transportista/tramos/{id}/iniciar
- [ ] PUT /api/transportista/tramos/{id}/finalizar

### ✅ Seguridad (Deben fallar)
- [ ] Acceso sin token → 401 Unauthorized
- [ ] Acceso a endpoint de OPERADOR → 403 Forbidden
- [ ] Acceso a endpoint de CLIENTE → 403 Forbidden
- [ ] Token expirado → 401 Unauthorized

---

## 🎯 Flujo de Prueba Completo

### Escenario: Transportista inicia y finaliza un viaje

1. **Autenticarse**
   ```
   POST /realms/contenedores-app/protocol/openid-connect/token
   ✅ Recibir access_token
   ```

2. **Ver mis tramos asignados**
   ```
   GET /api/transportista/mis-tramos
   ✅ Ver lista de tramos
   ```

3. **Ver información de mi camión**
   ```
   GET /api/transportista/mi-camion
   ✅ Ver datos del camión ABC123
   ```

4. **Iniciar el primer tramo**
   ```
   PUT /api/transportista/tramos/1/iniciar
   ✅ Tramo marcado como iniciado
   ```

5. **Verificar que no puedo iniciarlo de nuevo**
   ```
   PUT /api/transportista/tramos/1/iniciar
   ❌ Error: "El tramo ya fue iniciado previamente"
   ```

6. **Finalizar el tramo**
   ```
   PUT /api/transportista/tramos/1/finalizar
   ✅ Tramo marcado como finalizado
   ```

7. **Verificar que no puedo finalizarlo de nuevo**
   ```
   PUT /api/transportista/tramos/1/finalizar
   ❌ Error: "El tramo ya fue finalizado previamente"
   ```

8. **Intentar acceder a endpoint de OPERADOR**
   ```
   GET /api/ciudades
   ❌ 403 Forbidden
   ```

---

## 💡 Tips y Trucos

### 1. Script Pre-request para Auto-refresh

Agrega este script en la pestaña "Pre-request Script" de tu colección:

```javascript
// Auto-refresh si el token está por expirar
const tokenTimestamp = pm.environment.get("token_timestamp");
const now = Date.now();
const fiveMinutes = 5 * 60 * 1000;

if (!tokenTimestamp || (now - tokenTimestamp) > fiveMinutes) {
    // Refrescar token
    pm.sendRequest({
        url: pm.environment.get("keycloak_url") + '/realms/' + pm.environment.get("realm") + '/protocol/openid-connect/token',
        method: 'POST',
        header: 'Content-Type: application/x-www-form-urlencoded',
        body: {
            mode: 'urlencoded',
            urlencoded: [
                {key: "client_id", value: pm.environment.get("client_id")},
                {key: "client_secret", value: pm.environment.get("client_secret")},
                {key: "refresh_token", value: pm.environment.get("refresh_token")},
                {key: "grant_type", value: "refresh_token"}
            ]
        }
    }, function (err, res) {
        if (!err) {
            const jsonData = res.json();
            pm.environment.set("access_token", jsonData.access_token);
            pm.environment.set("refresh_token", jsonData.refresh_token);
            pm.environment.set("token_timestamp", Date.now());
        }
    });
}
```

### 2. Decodificar Token Automáticamente

Agrega este script en "Tests" de la petición de login:

```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("access_token", jsonData.access_token);
    pm.environment.set("refresh_token", jsonData.refresh_token);
    pm.environment.set("token_timestamp", Date.now());
    
    // Decodificar y mostrar el payload del token
    const token = jsonData.access_token;
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const payload = JSON.parse(atob(base64));
    
    console.log("Token payload:", payload);
    console.log("Roles:", payload.realm_access.roles);
    console.log("Dominio camión:", payload.dominio_camion);
}
```

### 3. Tests Automáticos

Agrega estos tests en cada petición de TRANSPORTISTA:

```javascript
// Verificar que la respuesta es exitosa
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// Verificar que la respuesta es JSON
pm.test("Response is JSON", function () {
    pm.response.to.be.json;
});

// Para endpoints que devuelven datos
pm.test("Response has data", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.not.be.empty;
});
```

---

## 📊 Códigos de Estado Esperados

| Escenario | Código | Significado |
|-----------|--------|-------------|
| Petición exitosa | 200 | OK |
| Token ausente | 401 | Unauthorized |
| Token inválido/expirado | 401 | Unauthorized |
| Rol incorrecto | 403 | Forbidden |
| Recurso no encontrado | 404 | Not Found |
| Error de validación | 400 | Bad Request |
| Error del servidor | 500 | Internal Server Error |

---

## 🔗 URLs de Referencia Rápida

- **Keycloak Admin**: http://localhost:8080
- **Realm Account**: http://localhost:8080/realms/contenedores-app/account/
- **Token Endpoint**: http://localhost:8080/realms/contenedores-app/protocol/openid-connect/token
- **API Base URL**: http://localhost:8082
- **JWT Decoder**: https://jwt.io

---

## 📥 Importar Colección JSON

Crea un archivo `keycloak-transportista.postman_collection.json` con este contenido y expórtalo en Postman:

```json
{
  "info": {
    "name": "Keycloak - Transportista Tests",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Login - Get Token",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "if (pm.response.code === 200) {",
                  "    var jsonData = pm.response.json();",
                  "    pm.environment.set(\"access_token\", jsonData.access_token);",
                  "    pm.environment.set(\"refresh_token\", jsonData.refresh_token);",
                  "}"
                ]
              }
            }
          ],
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "urlencoded",
              "urlencoded": [
                {"key": "client_id", "value": "{{client_id}}"},
                {"key": "client_secret", "value": "{{client_secret}}"},
                {"key": "username", "value": "{{username}}"},
                {"key": "password", "value": "{{password}}"},
                {"key": "grant_type", "value": "password"}
              ]
            },
            "url": "{{keycloak_url}}/realms/{{realm}}/protocol/openid-connect/token"
          }
        }
      ]
    },
    {
      "name": "Transportista",
      "item": [
        {
          "name": "Obtener Mis Tramos",
          "request": {
            "method": "GET",
            "header": [
              {"key": "Authorization", "value": "Bearer {{access_token}}"}
            ],
            "url": "{{api_url}}/api/transportista/mis-tramos"
          }
        },
        {
          "name": "Obtener Mi Camión",
          "request": {
            "method": "GET",
            "header": [
              {"key": "Authorization", "value": "Bearer {{access_token}}"}
            ],
            "url": "{{api_url}}/api/transportista/mi-camion"
          }
        },
        {
          "name": "Iniciar Tramo",
          "request": {
            "method": "PUT",
            "header": [
              {"key": "Authorization", "value": "Bearer {{access_token}}"}
            ],
            "url": "{{api_url}}/api/transportista/tramos/1/iniciar"
          }
        },
        {
          "name": "Finalizar Tramo",
          "request": {
            "method": "PUT",
            "header": [
              {"key": "Authorization", "value": "Bearer {{access_token}}"}
            ],
            "url": "{{api_url}}/api/transportista/tramos/1/finalizar"
          }
        }
      ]
    }
  ]
}
```

---

¡Con esta guía deberías poder probar completamente la funcionalidad de Keycloak para el rol TRANSPORTISTA! 🚀
