# Flujo de Autenticación y Autorización con Keycloak

Este documento explica el flujo completo de autenticación y autorización en el sistema.

## 🔄 Diagrama de Flujo General

```
┌──────────────┐                  ┌──────────────┐                  ┌──────────────────┐
│              │                  │              │                  │                  │
│   Cliente    │                  │   Keycloak   │                  │  API (Spring     │
│  (Postman)   │                  │   Server     │                  │   Boot)          │
│              │                  │              │                  │                  │
└──────┬───────┘                  └──────┬───────┘                  └────────┬─────────┘
       │                                 │                                   │
       │  1. POST /token                 │                                   │
       │  (username + password)          │                                   │
       │────────────────────────────────>│                                   │
       │                                 │                                   │
       │                                 │  2. Valida credenciales           │
       │                                 │     y verifica roles              │
       │                                 │                                   │
       │  3. Retorna JWT                 │                                   │
       │  (access_token + refresh_token) │                                   │
       │<────────────────────────────────│                                   │
       │                                 │                                   │
       │  4. GET /api/transportista/mis-tramos                              │
       │  Header: Authorization: Bearer {JWT}                                │
       │────────────────────────────────────────────────────────────────────>│
       │                                 │                                   │
       │                                 │  5. Validar firma JWT             │
       │                                 │     con clave pública             │
       │                                 │<──────────────────────────────────│
       │                                 │                                   │
       │                                 │  6. Retorna clave pública         │
       │                                 │────────────────────────────────>│
       │                                 │                                   │
       │                                 │                                   │  7. Verifica:
       │                                 │                                   │     - Token válido
       │                                 │                                   │     - No expirado
       │                                 │                                   │     - Rol correcto
       │                                 │                                   │
       │  8. Retorna datos si autorizado                                    │
       │<────────────────────────────────────────────────────────────────────│
       │    o 403 si no tiene permisos   │                                   │
       │                                 │                                   │
```

## 🔐 Detalle del Token JWT

### Estructura del Token

Un JWT tiene tres partes separadas por puntos (.):

```
eyJhbGci...header.eyJzdWIi...payload.SflKxwRJ...signature
   ↑              ↑              ↑
Header        Payload        Signature
```

### 1. Header (Encabezado)

```json
{
  "alg": "RS256",           // Algoritmo de firma
  "typ": "JWT",             // Tipo de token
  "kid": "key-id-123"       // ID de la clave usada
}
```

### 2. Payload (Carga útil)

```json
{
  "exp": 1705332000,                          // Fecha de expiración (Unix timestamp)
  "iat": 1705331700,                          // Fecha de emisión
  "jti": "unique-token-id",                   // ID único del token
  "iss": "http://localhost:8080/realms/contenedores-app",  // Emisor
  "aud": "contenedores-api",                  // Audiencia
  "sub": "user-uuid-1234",                    // Subject (ID del usuario)
  "typ": "Bearer",                            // Tipo
  "azp": "contenedores-api",                  // Authorized party
  "session_state": "session-id",              // Estado de sesión
  "realm_access": {
    "roles": [
      "TRANSPORTISTA",                        // Rol del usuario
      "offline_access",
      "uma_authorization"
    ]
  },
  "scope": "email profile",                   // Scopes
  "email_verified": true,                     // Email verificado
  "name": "Juan Perez",                       // Nombre completo
  "preferred_username": "transportista1",     // Username
  "given_name": "Juan",                       // Nombre
  "family_name": "Perez",                     // Apellido
  "email": "transportista1@example.com",      // Email
  "dominio_camion": "ABC123"                  // Atributo personalizado
}
```

### 3. Signature (Firma)

```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret/privateKey
)
```

La firma garantiza que:
- El token no ha sido modificado
- Fue emitido por Keycloak
- Es confiable

## 🔍 Proceso de Validación en Spring Boot

```
Petición HTTP
     ↓
┌─────────────────────────────────────────┐
│  Spring Security Filter Chain           │
│                                          │
│  1. BearerTokenAuthenticationFilter     │
│     ├─ Extrae token del header          │
│     └─ Authorization: Bearer {token}    │
│                                          │
│  2. JwtDecoder                           │
│     ├─ Decodifica el JWT                │
│     ├─ Obtiene clave pública de         │
│     │  Keycloak (/.../certs)            │
│     └─ Valida firma                     │
│                                          │
│  3. JwtAuthenticationConverter           │
│     ├─ Extrae claims del token          │
│     ├─ Convierte roles:                 │
│     │  realm_access.roles →             │
│     │  ROLE_TRANSPORTISTA                │
│     └─ Crea Authentication object       │
│                                          │
│  4. SecurityFilterChain                  │
│     ├─ Verifica permisos:               │
│     │  .hasRole("TRANSPORTISTA")        │
│     └─ Decide: ALLOW / DENY             │
│                                          │
└─────────────────────────────────────────┘
     ↓
  Controller
     ↓
  Response
```

## 🎯 Flujo Completo: Transportista Inicia Viaje

### Paso a Paso Detallado

```
1. AUTENTICACIÓN
   ================
   
   Transportista → Postman:
   "Quiero iniciar sesión como transportista1"
   
   Postman → Keycloak:
   POST /realms/contenedores-app/protocol/openid-connect/token
   Body: {
     client_id: contenedores-api,
     client_secret: [secret],
     username: transportista1,
     password: transportista123,
     grant_type: password
   }
   
   Keycloak:
   - Verifica credenciales en base de datos
   - Busca roles del usuario (TRANSPORTISTA)
   - Busca atributos (dominio_camion: ABC123)
   - Genera JWT firmado
   
   Keycloak → Postman:
   {
     access_token: "eyJhbGci...",
     refresh_token: "eyJhbGci...",
     expires_in: 300
   }

2. CONSULTAR TRAMOS
   ==================
   
   Postman → API:
   GET /api/transportista/mis-tramos
   Header: Authorization: Bearer eyJhbGci...
   
   API (Spring Security):
   - Extrae token del header
   - Valida firma con clave pública de Keycloak
   - Verifica que no expiró
   - Extrae roles: ["TRANSPORTISTA"]
   - Agrega "ROLE_" prefix: ["ROLE_TRANSPORTISTA"]
   
   API (SecurityConfig):
   @PreAuthorize("hasRole('TRANSPORTISTA')")
   ✅ Usuario tiene ROLE_TRANSPORTISTA → PERMITIR
   
   API (Controller):
   - Extrae dominio_camion del JWT: "ABC123"
   - Consulta tramos del camión ABC123 en BD
   - Retorna lista de tramos
   
   API → Postman:
   {
     dominoCamion: "ABC123",
     tramos: [{id: 1, origen: {...}, destino: {...}}],
     cantidad: 1
   }

3. INICIAR TRAMO
   ==============
   
   Postman → API:
   PUT /api/transportista/tramos/1/iniciar
   Header: Authorization: Bearer eyJhbGci...
   
   API (Spring Security):
   - Valida token (igual que paso 2)
   - Verifica rol TRANSPORTISTA ✅
   
   API (Controller):
   - Busca tramo con ID=1 en BD
   - Verifica que pertenece al camión ABC123
   - Verifica que el dominio del token coincide
   - Verifica que no fue iniciado antes
   - Registra fechaInicio = NOW()
   - Guarda en BD
   
   API → Postman:
   {
     mensaje: "Tramo iniciado exitosamente",
     tramoId: 1,
     fechaInicio: "2025-01-15T10:30:00"
   }

4. TOKEN EXPIRA (después de 5 minutos)
   ====================================
   
   Postman → API:
   GET /api/transportista/mis-tramos
   Header: Authorization: Bearer eyJhbGci... (expirado)
   
   API (Spring Security):
   - Valida token
   - Verifica expiración: exp < NOW()
   ❌ Token expirado
   
   API → Postman:
   401 Unauthorized
   {
     error: "invalid_token",
     error_description: "Token has expired"
   }

5. REFRESCAR TOKEN
   ================
   
   Postman → Keycloak:
   POST /realms/contenedores-app/protocol/openid-connect/token
   Body: {
     client_id: contenedores-api,
     client_secret: [secret],
     refresh_token: "eyJhbGci...",
     grant_type: refresh_token
   }
   
   Keycloak:
   - Valida refresh_token
   - Verifica que no haya sido revocado
   - Genera nuevo access_token
   - Genera nuevo refresh_token
   
   Keycloak → Postman:
   {
     access_token: "eyJnew...",
     refresh_token: "eyJnew...",
     expires_in: 300
   }
   
   Ahora puedes usar el nuevo token por 5 minutos más.
```

## 🚫 Ejemplos de Casos NO Permitidos

### Caso 1: Sin Token

```
Postman → API:
GET /api/transportista/mis-tramos
(Sin header Authorization)

API (Spring Security):
❌ No se encontró token
→ 401 Unauthorized
```

### Caso 2: Token Inválido

```
Postman → API:
GET /api/transportista/mis-tramos
Header: Authorization: Bearer token-inventado-123

API (Spring Security):
- Intenta validar firma
❌ Firma inválida
→ 401 Unauthorized
```

### Caso 3: Rol Incorrecto

```
Postman → API (con token de TRANSPORTISTA):
GET /api/ciudades

API (Spring Security):
- Token válido ✅
- Usuario tiene ROLE_TRANSPORTISTA
- Endpoint requiere ROLE_OPERADOR
❌ Roles no coinciden
→ 403 Forbidden
```

### Caso 4: Camión Incorrecto

```
Postman → API (token con dominio_camion: ABC123):
GET /api/tramos/XYZ789

API (Spring Security):
- Token válido ✅
- Rol correcto ✅

API (Controller):
- Token dice: dominio_camion = ABC123
- Petición solicita: XYZ789
❌ No coinciden
→ 403 Forbidden: "No tiene permisos para ver tramos de este camión"
```

## 🔑 Configuración de Seguridad en Spring

### SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configuración de endpoints y roles
            .authorizeHttpRequests(auth -> auth
                // TRANSPORTISTA
                .requestMatchers(HttpMethod.GET, "/api/tramos/{dominio}")
                    .hasRole("TRANSPORTISTA")
                .requestMatchers(HttpMethod.PUT, "/api/tramos/{id}/**")
                    .hasRole("TRANSPORTISTA")
                
                // OPERADOR
                .requestMatchers("/api/ciudades/**")
                    .hasRole("OPERADOR")
                
                // CLIENTE
                .requestMatchers(HttpMethod.POST, "/api/solicitudes/**")
                    .hasRole("CLIENTE")
                
                // Resto requiere autenticación
                .anyRequest().authenticated()
            )
            // Configurar validación JWT
            .oauth2ResourceServer(oauth2 -> 
                oauth2.jwt(jwt -> 
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = 
            new JwtGrantedAuthoritiesConverter();
        
        // Keycloak envía roles en "realm_access.roles"
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
        // Spring Security espera prefix "ROLE_"
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        
        return converter;
    }
}
```

## 📊 Comparación: Con vs Sin Keycloak

### ❌ Sin Keycloak (Básico)

```
Problemas:
- Credenciales en cada petición (inseguro)
- Sesiones en servidor (no escalable)
- Lógica de autenticación en cada app
- Difícil gestionar usuarios
- Difícil implementar SSO
- Sin estándar (OAuth2/OIDC)
```

### ✅ Con Keycloak

```
Ventajas:
- Token JWT (stateless, escalable)
- Autenticación centralizada
- Gestión de usuarios simplificada
- SSO (Single Sign-On) fácil
- Estándares (OAuth2/OIDC)
- Roles y permisos centralizados
- Sesiones gestionadas por Keycloak
- Refresh tokens para renovación
- Integración con LDAP/AD/etc.
```

## 🎓 Conceptos Clave

### JWT (JSON Web Token)
Token autocontenido que incluye toda la información necesaria (roles, usuario, permisos). No requiere consultar la base de datos en cada petición.

### OAuth 2.0
Protocolo de autorización que permite acceso limitado a recursos sin compartir credenciales.

### OpenID Connect (OIDC)
Capa de autenticación sobre OAuth 2.0 que provee información del usuario.

### Stateless
El servidor no guarda estado de sesión. Toda la información está en el token.

### Bearer Token
Tipo de token que da acceso a quien lo "porta" (bearer = portador).

### Resource Server
Servidor que protege recursos (nuestra API Spring Boot).

### Authorization Server
Servidor que emite tokens (Keycloak).

### Realm
En Keycloak, un realm es un espacio aislado para gestionar usuarios, clientes y roles.

### Client
Aplicación que solicita autenticación (nuestra API).

### Grant Type
Método para obtener tokens. Tipos:
- `password`: Usuario/contraseña (desarrollo/testing)
- `authorization_code`: Flujo estándar (producción)
- `refresh_token`: Renovar token
- `client_credentials`: Máquina a máquina

---

## 📚 Recursos Adicionales

- 📖 [Guía Completa de Testing](./KEYCLOAK_TESTING_GUIDE.md)
- 📬 [Guía de Postman](./POSTMAN_COLLECTION_GUIDE.md)
- 📋 [README del Proyecto](./README.md)
- 🌐 [Documentación Keycloak](https://www.keycloak.org/documentation)
- 🔐 [RFC 7519 - JWT](https://tools.ietf.org/html/rfc7519)
- 🔑 [RFC 6749 - OAuth 2.0](https://tools.ietf.org/html/rfc6749)
