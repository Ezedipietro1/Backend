# TPI - Sistema de Gestión de Contenedores

Sistema de gestión de contenedores con autenticación y autorización mediante Keycloak.

## 🏗️ Arquitectura

El sistema está compuesto por tres servicios principales:

- **Keycloak**: Servidor de autenticación y autorización (puerto 8080)
- **SolicitudTraslado**: API principal del sistema (puerto 8082)
- **Clientes**: Servicio de gestión de clientes (puerto 8081)

## 🚀 Inicio Rápido

### 1. Levantar los servicios

```bash
cd TPI
docker-compose up -d
```

### 2. Verificar que los servicios estén corriendo

```bash
docker-compose ps
```

### 3. Configurar Keycloak

Sigue la guía completa en [KEYCLOAK_TESTING_GUIDE.md](./KEYCLOAK_TESTING_GUIDE.md)

## 📚 Documentación Disponible

### 📖 [Guía Completa de Prueba de Keycloak](./KEYCLOAK_TESTING_GUIDE.md)

Guía detallada paso a paso que incluye:
- ✅ Configuración inicial de Keycloak
- ✅ Creación de realm, cliente y usuarios
- ✅ Configuración de roles y permisos
- ✅ Obtención de tokens con Postman
- ✅ Prueba de endpoints protegidos
- ✅ Troubleshooting y solución de problemas

### 📬 [Guía de Postman](./POSTMAN_COLLECTION_GUIDE.md)

Guía práctica para usar Postman con Keycloak:
- ✅ Configuración de variables de entorno
- ✅ Colección completa de peticiones
- ✅ Scripts automáticos para refrescar tokens
- ✅ Tests automáticos
- ✅ Checklist completo de pruebas

## 👥 Roles del Sistema

El sistema implementa tres roles principales:

### 🚚 TRANSPORTISTA (Camionero/Chofer)

**Permisos**:
- Ver tramos asignados a su camión
- Obtener información de su camión
- Iniciar tramos
- Finalizar tramos

**Endpoints**:
- `GET /api/transportista/mis-tramos`
- `GET /api/transportista/mi-camion`
- `GET /api/tramos/{dominio}`
- `PUT /api/transportista/tramos/{id}/iniciar`
- `PUT /api/transportista/tramos/{id}/finalizar`

### 👨‍💼 OPERADOR

**Permisos**:
- Gestionar ciudades
- Gestionar depósitos
- Gestionar tarifas
- Gestionar camiones
- Gestionar contenedores

**Endpoints**:
- `/api/ciudades/**`
- `/api/depositos/**`
- `/api/tarifas/**`
- `/api/camiones/**`
- `/api/contenedores/**`

### 🧑‍💼 CLIENTE

**Permisos**:
- Crear solicitudes de traslado
- Ver sus propias solicitudes
- Ver información de sus contenedores

**Endpoints**:
- `POST /api/solicitudes/**`
- `GET /api/solicitudes/{numero}/**`
- `GET /api/contenedores/{id}/**`

## 🔐 Seguridad

### Autenticación

El sistema utiliza **OAuth 2.0 / OpenID Connect** a través de Keycloak:

1. El usuario se autentica en Keycloak
2. Keycloak emite un JWT (JSON Web Token)
3. El token se incluye en el header `Authorization: Bearer {token}` de cada petición
4. El servidor valida el token y verifica los permisos

### Configuración de Seguridad

La configuración de seguridad se encuentra en:
```
SolicitudTraslado/src/main/java/com/SolicitudTraslado/config/SecurityConfig.java
```

### Propiedades de Keycloak

Las propiedades de conexión se encuentran en:
```
SolicitudTraslado/src/main/resources/application.properties
```

Propiedades clave:
```properties
# JWT Validation
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/contenedores-app
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/contenedores-app/protocol/openid-connect/certs

# Keycloak Connection
keycloak.auth-server-url=http://localhost:8080
keycloak.realm=contenedores-app
keycloak.resource=contenedores-api
keycloak.credentials.secret=TU_CLIENT_SECRET_AQUI
```

## 🧪 Testing

### Pruebas Manuales con Postman

1. **Obtener Token**:
   ```
   POST http://localhost:8080/realms/contenedores-app/protocol/openid-connect/token
   
   Body (x-www-form-urlencoded):
   - client_id: contenedores-api
   - client_secret: {tu_secret}
   - username: transportista1
   - password: transportista123
   - grant_type: password
   ```

2. **Usar el Token**:
   ```
   GET http://localhost:8082/api/transportista/mis-tramos
   
   Headers:
   - Authorization: Bearer {tu_token}
   ```

Ver [POSTMAN_COLLECTION_GUIDE.md](./POSTMAN_COLLECTION_GUIDE.md) para más detalles.

### Verificación de Token

Puedes verificar el contenido de cualquier JWT en [jwt.io](https://jwt.io).

El token debe contener:
- `realm_access.roles`: Array con los roles del usuario
- `preferred_username`: Nombre de usuario
- `dominio_camion`: (Para TRANSPORTISTA) Dominio del camión asignado

## 🐛 Troubleshooting

### El servicio no levanta

```bash
# Ver logs
docker-compose logs keycloak
docker-compose logs solicitudtraslado

# Reiniciar servicios
docker-compose restart
```

### Error de autenticación (401)

- Verifica que el token no haya expirado (expiran en 5 minutos)
- Asegúrate de incluir el header `Authorization: Bearer {token}`
- Obtén un nuevo token

### Error de autorización (403)

- Verifica que el usuario tenga el rol correcto en Keycloak
- Confirma que el rol esté en el token (revisa en jwt.io)
- Verifica que el endpoint corresponda al rol del usuario

### Keycloak no inicia

```bash
# Verificar puerto 8080
lsof -i :8080

# Si está ocupado, detén el proceso o cambia el puerto en docker-compose.yml

# Limpiar volúmenes y reiniciar
docker-compose down -v
docker-compose up -d
```

## 📦 Estructura del Proyecto

```
TPI/
├── docker-compose.yml              # Orquestación de servicios
├── README.md                       # Este archivo
├── KEYCLOAK_TESTING_GUIDE.md      # Guía completa de Keycloak
├── POSTMAN_COLLECTION_GUIDE.md    # Guía de Postman
├── Clientes/                      # Servicio de clientes
│   ├── Dockerfile
│   ├── src/
│   └── pom.xml
└── SolicitudTraslado/             # API principal
    ├── Dockerfile
    ├── src/
    │   ├── main/
    │   │   ├── java/com/SolicitudTraslado/
    │   │   │   ├── config/
    │   │   │   │   └── SecurityConfig.java
    │   │   │   ├── controller/
    │   │   │   │   ├── RolTransportistaController.java
    │   │   │   │   └── ...
    │   │   │   └── ...
    │   │   └── resources/
    │   │       └── application.properties
    │   └── test/
    └── pom.xml
```

## 🔗 Enlaces Útiles

- **Keycloak Admin Console**: http://localhost:8080
- **Keycloak Realm Account**: http://localhost:8080/realms/contenedores-app/account/
- **API Base URL**: http://localhost:8082
- **Cliente Service**: http://localhost:8081
- **JWT Decoder**: https://jwt.io
- **Documentación Keycloak**: https://www.keycloak.org/documentation

## 📝 Notas Importantes

1. **Credenciales de Admin de Keycloak**:
   - Usuario: `admin`
   - Contraseña: `admin123`

2. **Usuarios de Prueba**:
   - Transportista: `transportista1` / `transportista123`
   - (Crear otros usuarios según necesidad)

3. **Seguridad en Producción**:
   - Cambiar las contraseñas por defecto
   - Usar HTTPS en todos los servicios
   - Configurar client secret seguro
   - Implementar rate limiting
   - Habilitar CORS correctamente
   - No usar `grant_type=password` (usar Authorization Code Flow)

4. **Base de Datos**:
   - Keycloak usa base de datos embebida (dev-file)
   - Para producción, configurar base de datos externa (PostgreSQL recomendado)

## 🚀 Próximos Pasos

1. ✅ Configurar Keycloak siguiendo [KEYCLOAK_TESTING_GUIDE.md](./KEYCLOAK_TESTING_GUIDE.md)
2. ✅ Probar autenticación con Postman
3. ✅ Crear usuarios para cada rol
4. ✅ Probar todos los endpoints según el rol
5. ⬜ Implementar tests automatizados
6. ⬜ Configurar CI/CD
7. ⬜ Documentar API con Swagger/OpenAPI

## 🤝 Contribución

Para contribuir al proyecto:

1. Crea una rama desde `main`
2. Realiza tus cambios
3. Asegúrate de que los servicios funcionen correctamente
4. Crea un Pull Request

## 📄 Licencia

[Especificar licencia del proyecto]

---

**¿Necesitas ayuda?** Consulta las guías detalladas:
- 📖 [Guía de Keycloak](./KEYCLOAK_TESTING_GUIDE.md)
- 📬 [Guía de Postman](./POSTMAN_COLLECTION_GUIDE.md)
