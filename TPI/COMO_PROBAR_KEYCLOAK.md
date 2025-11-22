# 🚀 Cómo Probar si Keycloak está Funcionando

## 📋 Resumen Rápido

Esta guía te mostrará **paso a paso** cómo probar si Keycloak está funcionando correctamente, específicamente para el usuario TRANSPORTISTA.

---

## 🎯 Objetivo

1. ✅ Obtener un token a través de Postman usando el usuario TRANSPORTISTA
2. ✅ Probar si están funcionando los ruteos/endpoints protegidos

---

## 📚 Documentación Disponible

Hemos creado **4 guías completas** para ayudarte:

| Documento | Descripción | Cuándo Usarlo |
|-----------|-------------|---------------|
| **[KEYCLOAK_TESTING_GUIDE.md](./KEYCLOAK_TESTING_GUIDE.md)** | Guía completa paso a paso | Primera vez configurando Keycloak |
| **[POSTMAN_COLLECTION_GUIDE.md](./POSTMAN_COLLECTION_GUIDE.md)** | Guía rápida de Postman | Cuando ya tienes Keycloak configurado |
| **[FLUJO_AUTENTICACION.md](./FLUJO_AUTENTICACION.md)** | Diagramas y explicaciones técnicas | Para entender cómo funciona internamente |
| **[README.md](./README.md)** | Visión general del proyecto | Para entender la arquitectura |

---

## ⚡ Quick Start - 5 Pasos

### Paso 1: Levantar los Servicios ⬆️

```bash
cd TPI
docker-compose up -d
```

Verifica que estén corriendo:
```bash
docker-compose ps
```

Deberías ver 3 servicios: `keycloak`, `solicitudtranslado`, `clientes`

### Paso 2: Configurar Keycloak 🔧

**Sigue esta guía detallada**: [KEYCLOAK_TESTING_GUIDE.md](./KEYCLOAK_TESTING_GUIDE.md)

**Resumen de lo que harás**:
1. Acceder a http://localhost:8080 (admin/admin123)
2. Crear realm: `contenedores-app`
3. Crear cliente: `contenedores-api`
4. Crear roles: `TRANSPORTISTA`, `CLIENTE`, `OPERADOR`
5. Crear usuario: `transportista1` con contraseña `transportista123`
6. Asignar rol `TRANSPORTISTA` al usuario
7. (Opcional) Agregar atributo `dominio_camion: ABC123`

**⏱️ Tiempo estimado**: 10-15 minutos

### Paso 3: Obtener Token con Postman 🎫

Abre Postman y crea una nueva petición:

**Método**: `POST`

**URL**: 
```
http://localhost:8080/realms/contenedores-app/protocol/openid-connect/token
```

**Body** (selecciona `x-www-form-urlencoded`):
```
client_id:      contenedores-api
client_secret:  (copia el secret de Keycloak - ver guía)
username:       transportista1
password:       transportista123
grant_type:     password
```

**Envía la petición** → Deberías recibir:

```json
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI...",
    "expires_in": 300,
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI..."
}
```

✅ **¡Copia el `access_token`!** Lo necesitarás para el siguiente paso.

### Paso 4: Probar los Endpoints 🧪

Ahora prueba los endpoints del TRANSPORTISTA:

#### 4.1 Obtener Mis Tramos

**Método**: `GET`

**URL**: 
```
http://localhost:8082/api/transportista/mis-tramos
```

**Headers**:
```
Authorization: Bearer TU_ACCESS_TOKEN_AQUI
```

**Respuesta Esperada**: ✅ 200 OK
```json
{
    "dominoCamion": "ABC123",
    "tramos": [...],
    "cantidad": 1
}
```

#### 4.2 Iniciar un Tramo

**Método**: `PUT`

**URL**: 
```
http://localhost:8082/api/transportista/tramos/1/iniciar
```

**Headers**:
```
Authorization: Bearer TU_ACCESS_TOKEN_AQUI
```

**Respuesta Esperada**: ✅ 200 OK
```json
{
    "mensaje": "Tramo iniciado exitosamente",
    "tramoId": 1,
    "fechaInicio": "2025-01-15T10:30:00"
}
```

### Paso 5: Verificar Seguridad 🔒

Prueba que la seguridad funciona intentando acceder a un endpoint de OPERADOR:

**Método**: `GET`

**URL**: 
```
http://localhost:8082/api/ciudades
```

**Headers**:
```
Authorization: Bearer TU_ACCESS_TOKEN_AQUI
```

**Respuesta Esperada**: ❌ 403 Forbidden
```json
{
    "error": "Forbidden",
    "message": "Access Denied"
}
```

✅ **¡Perfecto!** Esto confirma que el TRANSPORTISTA NO puede acceder a endpoints de OPERADOR.

---

## ✅ Checklist de Verificación

### Keycloak Configurado
- [ ] Realm `contenedores-app` creado
- [ ] Cliente `contenedores-api` creado y configurado
- [ ] Rol `TRANSPORTISTA` creado
- [ ] Usuario `transportista1` creado con contraseña
- [ ] Rol `TRANSPORTISTA` asignado al usuario
- [ ] Mapper de roles configurado
- [ ] (Opcional) Atributo `dominio_camion` agregado

### Autenticación Funciona
- [ ] Puedo obtener token con Postman
- [ ] El token contiene el rol TRANSPORTISTA (verificar en jwt.io)
- [ ] El token contiene el dominio_camion (si lo agregaste)

### Endpoints Funcionan
- [ ] GET /api/transportista/mis-tramos → 200 OK ✅
- [ ] GET /api/transportista/mi-camion → 200 OK ✅
- [ ] PUT /api/transportista/tramos/{id}/iniciar → 200 OK ✅
- [ ] PUT /api/transportista/tramos/{id}/finalizar → 200 OK ✅

### Seguridad Funciona
- [ ] Sin token → 401 Unauthorized ❌
- [ ] Token inválido → 401 Unauthorized ❌
- [ ] Token expirado → 401 Unauthorized ❌
- [ ] Acceso a endpoint de OPERADOR → 403 Forbidden ❌
- [ ] Acceso a endpoint de CLIENTE → 403 Forbidden ❌

---

## 🐛 Problemas Comunes

### ❌ Error: "401 Unauthorized"

**Causa**: Token ausente, inválido o expirado

**Solución**:
1. Verifica que incluiste el header `Authorization: Bearer {token}`
2. Verifica que el token no esté expirado (expira en 5 minutos)
3. Obtén un nuevo token si es necesario

### ❌ Error: "403 Forbidden"

**Causa**: El usuario no tiene permisos para ese endpoint

**Solución**:
1. Verifica que el usuario tenga el rol correcto en Keycloak
2. Verifica que el rol esté en el token (usa https://jwt.io)
3. Verifica que el mapper de roles esté configurado

### ❌ Error: "Connection refused" al intentar obtener token

**Causa**: Keycloak no está corriendo

**Solución**:
```bash
# Verifica que Keycloak esté corriendo
docker-compose ps

# Si no está corriendo
docker-compose up -d keycloak

# Espera 30 segundos y verifica logs
docker logs keycloak
```

### ❌ Error: "Invalid client credentials"

**Causa**: El client_secret es incorrecto

**Solución**:
1. Ve a Keycloak Admin → Clients → contenedores-api → Credentials
2. Copia el Client Secret
3. Úsalo en Postman

### ❌ El token no incluye los roles

**Causa**: El mapper no está configurado correctamente

**Solución**:
1. Ve a Keycloak Admin → Clients → contenedores-api
2. Ve a Client scopes → contenedores-api-dedicated
3. Verifica que existe el mapper `realm-roles`
4. Verifica que el Token Claim Name sea `realm_access.roles`

---

## 🎓 Próximos Pasos

Una vez que hayas verificado que Keycloak funciona:

1. **Crea usuarios para otros roles**:
   - Usuario OPERADOR para gestionar ciudades, depósitos, etc.
   - Usuario CLIENTE para crear solicitudes

2. **Prueba todos los endpoints según el rol**:
   - Ver matriz de permisos en [README.md](./README.md)

3. **Implementa el frontend**:
   - Integra el flujo de autenticación
   - Almacena el token en localStorage o cookies

4. **Configura para producción**:
   - Usa HTTPS
   - Cambia contraseñas por defecto
   - Configura base de datos PostgreSQL para Keycloak
   - No uses `grant_type=password` (usa Authorization Code Flow)

---

## 📞 Necesitas Más Ayuda?

Consulta las guías detalladas:

- 📖 **[KEYCLOAK_TESTING_GUIDE.md](./KEYCLOAK_TESTING_GUIDE.md)** - Guía paso a paso completa
- 📬 **[POSTMAN_COLLECTION_GUIDE.md](./POSTMAN_COLLECTION_GUIDE.md)** - Guía de Postman con scripts
- 🔄 **[FLUJO_AUTENTICACION.md](./FLUJO_AUTENTICACION.md)** - Explicación técnica del flujo
- 📋 **[README.md](./README.md)** - Información general del proyecto

---

## 🔗 URLs de Referencia

| Servicio | URL |
|----------|-----|
| Keycloak Admin | http://localhost:8080 |
| Realm Account | http://localhost:8080/realms/contenedores-app/account/ |
| Token Endpoint | http://localhost:8080/realms/contenedores-app/protocol/openid-connect/token |
| API Base | http://localhost:8082 |
| JWT Decoder | https://jwt.io |

---

## 📊 Endpoints del TRANSPORTISTA

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/transportista/mis-tramos` | GET | Obtener tramos asignados |
| `/api/transportista/mi-camion` | GET | Obtener info del camión |
| `/api/tramos/{dominio}` | GET | Obtener tramos por dominio |
| `/api/transportista/tramos/{id}/iniciar` | PUT | Iniciar un tramo |
| `/api/transportista/tramos/{id}/finalizar` | PUT | Finalizar un tramo |

---

**¡Éxito! 🎉** Con esta guía deberías poder probar completamente si Keycloak está funcionando correctamente.
