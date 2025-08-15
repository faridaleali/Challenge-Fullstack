# Marvel REST API

API REST desarrollada con Spring Boot para consultar información de personajes de Marvel Comics utilizando la API oficial de Marvel.

## Características

- **Autenticación JWT**: Sistema de login y registro de usuarios con tokens JWT
- **Integración con Marvel API**: Consulta de personajes de Marvel en tiempo real
- **Bitácora de llamadas**: Registro completo de todas las llamadas a la API
- **Base de datos H2**: Almacenamiento en memoria para desarrollo
- **Documentación Swagger**: Documentación interactiva de todos los endpoints
- **Tests unitarios**: Cobertura completa con JUnit y Mockito
- **Seguridad**: Endpoints protegidos con Spring Security

## Tecnologías utilizadas

- Java 11
- Spring Boot 2.7.14
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (JSON Web Tokens)
- Lombok
- Swagger/OpenAPI 3
- JUnit 5 y Mockito

## Configuración

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd marvel-rest-api
```

### 2. Configurar las credenciales de Marvel API

Editar el archivo `src/main/resources/application.properties`:

```properties
# Marvel API Configuration
marvel.api.public-key=tu_clave_publica_de_marvel
marvel.api.private-key=tu_clave_privada_de_marvel
```

**Nota**: Puedes obtener las credenciales en [Marvel Developer Portal](https://developer.marvel.com/)

### 3. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación se ejecutará en `http://localhost:8080`

## Usuarios por defecto

La aplicación crea automáticamente dos usuarios de prueba:

### Usuario Administrador
- **Username**: admin
- **Password**: admin123
- **Email**: admin@marvel.com
- **Role**: ADMIN

### Usuario de Prueba
- **Username**: testuser
- **Password**: test123
- **Email**: test@marvel.com
- **Role**: USER

## Endpoints

### Autenticación

#### POST /auth/register
Registrar un nuevo usuario
```json
{
  "username": "nuevousuario",
  "password": "password123",
  "email": "usuario@email.com",
  "fullName": "Nombre Completo"
}
```

#### POST /auth/login
Iniciar sesión
```json
{
  "username": "testuser",
  "password": "test123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "testuser",
  "email": "test@marvel.com",
  "fullName": "Test User"
}
```

### Personajes de Marvel (Requiere autenticación)

#### GET /characters
Obtener lista de personajes con paginación

**Parámetros de consulta:**
- `offset`: Número de resultados a omitir (por defecto: 0)
- `limit`: Número de resultados a devolver (por defecto: 20, máximo: 100)
- `name`: Filtrar por nombre que comience con el texto proporcionado

**Ejemplo:**
```bash
GET /characters?offset=0&limit=20&name=Spider
```

#### GET /characters/{id}
Obtener un personaje específico por ID

**Ejemplo:**
```bash
GET /characters/1011334
```

### Bitácora de llamadas (Requiere autenticación)

#### GET /api-logs
Obtener todas las llamadas a la API con paginación

**Parámetros de consulta:**
- `page`: Número de página (por defecto: 0)
- `size`: Tamaño de página (por defecto: 20, máximo: 100)

#### GET /api-logs/user/{username}
Obtener llamadas por usuario específico

#### GET /api-logs/endpoint?endpoint={endpoint}
Obtener llamadas por endpoint específico

## Autenticación

Todos los endpoints (excepto `/auth/**`) requieren autenticación mediante JWT token.

### Incluir el token en las peticiones:

```bash
curl -H "Authorization: Bearer tu_jwt_token" http://localhost:8080/characters
```

## Documentación interactiva

Una vez iniciada la aplicación, puedes acceder a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Base de datos

### H2 Console
Accede a la consola H2 en: http://localhost:8080/h2-console

**Configuración de conexión:**
- **JDBC URL**: jdbc:h2:mem:marveldb
- **Username**: sa
- **Password**: password

### Tablas principales
- `users`: Usuarios del sistema
- `api_call_logs`: Bitácora de llamadas a la API

## Ejecutar tests

```bash
mvn test
```

Los tests incluyen:
- Tests unitarios de controladores
- Tests de integración con MockMvc
- Tests de servicios con Mockito

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/alkemy/marvel/
│   │   ├── config/          # Configuraciones
│   │   ├── controller/      # Controladores REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entidades JPA
│   │   ├── repository/      # Repositorios
│   │   ├── security/        # Configuración de seguridad
│   │   └── service/         # Lógica de negocio
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/alkemy/marvel/
        └── controller/      # Tests de controladores
```

## Dependencias principales

El proyecto utiliza las siguientes dependencias:

- **marvel-api-client**: Cliente JAR personalizado para la API de Marvel
- **Spring Boot Starter Web**: Framework web
- **Spring Boot Starter Security**: Seguridad y autenticación
- **Spring Boot Starter Data JPA**: Acceso a datos
- **H2 Database**: Base de datos en memoria
- **JJWT**: Librería JWT para Java
- **Lombok**: Reducir código boilerplate
- **SpringDoc OpenAPI**: Documentación Swagger

## Notas de desarrollo

1. **Seguridad JWT**: Los tokens tienen una expiración de 24 horas por defecto
2. **Límites de API**: Las consultas están limitadas a máximo 100 resultados por petición
3. **Logging**: Se registran todas las llamadas a endpoints protegidos en la bitácora
4. **Validación**: Se validan todos los datos de entrada con Bean Validation
5. **Manejo de errores**: Respuestas consistentes de error en formato JSON

## Próximos pasos

- Configurar perfil de producción con base de datos PostgreSQL/MySQL
- Implementar cache para consultas frecuentes
- Agregar métricas y monitoreo con Actuator
- Configurar CI/CD pipeline
- Implementar rate limiting para la API

## Licencia

Este proyecto es solo para fines educativos y evaluación.