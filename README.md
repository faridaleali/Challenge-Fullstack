# Marvel API Challenge - Fullstack Java

## 📋 Descripción

Aplicación fullstack que consume la API de Marvel Comics para mostrar información de personajes. Desarrollada como challenge técnico.

## 🏗️ Arquitectura

El proyecto está dividido en 3 módulos independientes:

### 1. **marvel-api-client** (JAR Library)
- Cliente Java para consumir la API de Marvel
- Empaquetado con Maven
- Implementa autenticación MD5 requerida por Marvel
- Tests unitarios con JUnit

### 2. **marvel-rest-api** (Spring Boot API)
- API REST que usa el JAR anterior
- Autenticación JWT
- Base de datos H2 con JPA
- Bitácora de llamadas API
- Tests con Mockito
- Documentación Swagger

### 3. **marvel-frontend** (Angular 14)
- SPA con Angular y Material Design
- Listado de personajes con búsqueda
- Modal con detalles del personaje
- Pantalla de bitácora de consultas
- Autenticación JWT

## 🚀 Instalación y Ejecución

### Prerrequisitos
- Java 17+ (probado con Java 21)
- Maven 3.6+
- Node.js 14+
- npm 6+

### 1. Clonar el repositorio
```bash
git clone [url-del-repositorio]
cd evaluacion-bbva
```

### 2. Configurar credenciales de Marvel API

Obtén tus credenciales en https://developer.marvel.com/

Edita `marvel-rest-api/src/main/resources/application.properties`:
```properties
marvel.api.public-key=TU_PUBLIC_KEY
marvel.api.private-key=TU_PRIVATE_KEY
```

### 3. Compilar e instalar el JAR
```bash
cd marvel-api-client
mvn clean install
cd ..
```

### 4. Ejecutar el Backend
```bash
cd marvel-rest-api
mvn spring-boot:run
```

El backend estará disponible en http://localhost:8080

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console (usuario: sa, sin password)

### 5. Ejecutar el Frontend
```bash
cd marvel-frontend
npm install
ng serve
```

El frontend estará disponible en http://localhost:4200

## 🔐 Credenciales por defecto

### Backend API
- **Admin**: admin / admin123
- **Test User**: testuser / test123

### Base de datos H2
- **URL**: jdbc:h2:mem:testdb
- **Usuario**: sa
- **Password**: (vacío)

## 📝 Endpoints principales

### Autenticación
- `POST /api/auth/login` - Login de usuario
- `POST /api/auth/register` - Registro de usuario

### Characters
- `GET /api/characters` - Listado de personajes (requiere JWT)
- `GET /api/characters/{id}` - Detalle de personaje (requiere JWT)

### Bitácora
- `GET /api/logs` - Consultar bitácora de llamadas (requiere JWT)

## 🧪 Tests

### Backend
```bash
cd marvel-rest-api
mvn test
```

### Frontend
```bash
cd marvel-frontend
ng test
```

## 📚 Tecnologías utilizadas

### Backend
- Java 17
- Spring Boot 2.7.14
- Spring Security + JWT
- Spring Data JPA
- H2 Database
- Lombok
- Swagger/OpenAPI
- JUnit 5 + Mockito

### Frontend
- Angular 14
- Angular Material
- RxJS
- TypeScript
- SCSS

## 📁 Estructura del proyecto

```
evaluacion/
├── marvel-api-client/        # JAR Library
│   ├── src/
│   │   ├── main/java/       # Cliente Marvel API
│   │   └── test/java/       # Tests unitarios
│   └── pom.xml
│
├── marvel-rest-api/          # Backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Controladores, servicios, etc.
│   │   │   └── resources/  # Configuración
│   │   └── test/           # Tests con Mockito
│   └── pom.xml
│
└── marvel-frontend/          # Frontend Angular
    ├── src/
    │   ├── app/
    │   │   ├── components/  # Componentes UI
    │   │   ├── services/    # Servicios HTTP
    │   │   ├── models/      # Interfaces TypeScript
    │   │   └── guards/      # Auth guards
    │   └── environments/    # Configuración
    └── package.json
```

## 🎯 Funcionalidades implementadas

✅ Cliente JAR para API de Marvel con autenticación MD5  
✅ API REST con Spring Boot y arquitectura MVC  
✅ Autenticación JWT con Spring Security  
✅ Base de datos H2 con persistencia de logs  
✅ Tests unitarios con JUnit y Mockito  
✅ Frontend Angular con Material Design  
✅ Listado de personajes con búsqueda en tiempo real  
✅ Modal con información detallada del personaje  
✅ Bitácora de consultas a la API  
✅ Documentación Swagger/OpenAPI  
✅ Manejo de errores y validaciones  
✅ Diseño responsive  

## 👨‍💻 Autor

**Farid Ale Ali**  