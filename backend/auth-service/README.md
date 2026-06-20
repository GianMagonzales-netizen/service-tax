# 🚖 Taxi Orchestrator - Automatización de Asignación de Servicios

Sistema de orquestación para la asignación automática de servicios de taxi que integra gestión de conductores, procesamiento de pagos, notificaciones y seguimiento de viajes en tiempo real.

---

## 📋 Descripción del Proyecto

El sistema orquesta de manera automática todo el ciclo de vida de un servicio de taxi:

| Paso | Acción                                                  |
| ---- | ------------------------------------------------------- |
| 1    | Creación de solicitud de viaje                          |
| 2    | Selección inteligente del mejor conductor (4 criterios) |
| 3    | Asignación del servicio                                 |
| 4    | Procesamiento de pagos                                  |
| 5    | Completar viaje                                         |
| 6    | Notificaciones simuladas                                |
| 7    | Auditoría de operaciones                                |

### Flujo Completo

```text
Cliente → Solicitud Viaje → Orquestador
                              ↓
                    Buscar Mejor Conductor
                    (distancia, calificación, 
                     disponibilidad, tipo servicio)
                              ↓
                    Crear Asignación
                              ↓
                    Conductor Acepta
                              ↓
                    Procesar Pago
                              ↓
                    Completar Viaje
                              ↓
                    Notificaciones + Auditoría
```

---

## 🛠️ Tecnologías Utilizadas

| Tecnología       | Versión     |
| ---------------- | ----------- |
| Java             | 17          |
| Spring Boot      | 2.7.x / 3.x |
| Spring Security  | -           |
| JWT              | -           |
| Spring Data JPA  | -           |
| Hibernate        | -           |
| MySQL/PostgreSQL | -           |
| Gradle/Maven     | -           |

---

## 🏗️ Estructura de Módulos

```text
auth-service/
├── auth/                    # Autenticación JWT
│   ├── entity/             # UsuarioEntity
│   ├── repository/         # UsuarioRepository
│   ├── security/           # JwtTokenProvider, SecurityConfig
│   └── service/            # AuthService
├── driver/                  # Gestión de Conductores
│   ├── controller/         # DriverController
│   ├── dto/                # DriverRequestDTO, DriverResponseDTO
│   ├── entity/             # DriverEntity
│   ├── enums/              # DriverStatus, ServiceType
│   ├── exception/          # DriverNotFoundException
│   ├── repository/         # DriverRepository
│   └── service/            # DriverService
├── ride_request/            # Solicitudes de Viaje
│   ├── controller/         # RideRequestController
│   ├── dto/                # RideRequestDTO, RideResponseDTO
│   ├── entity/             # RideRequestEntity
│   ├── enums/              # RideStatus
│   ├── exception/          # RideException
│   ├── repository/         # RideRequestRepository
│   └── service/            # RideRequestService
├── ride_assignment/         # Asignación de Servicios
│   ├── controller/         # RideAssignmentController
│   ├── dto/                # AssignmentRequestDTO, AssignmentResponseDTO
│   ├── entity/             # RideAssignmentEntity
│   ├── enums/              # AssignmentStatus
│   ├── exception/          # AssignmentException
│   ├── repository/         # RideAssignmentRepository
│   └── service/            # RideAssignmentService
├── orchestrator/            # ORQUESTADOR CENTRAL ⭐
│   ├── controller/         # TaxiOrchestratorController
│   ├── dto/                # RideRequestDTO, OrchestratorResponseDTO, TripStatusDTO
│   ├── exception/          # OrquestadorException
│   └── service/            # TaxiOrchestratorService
├── payment/                 # Procesamiento de Pagos
│   ├── controller/         # PaymentController
│   ├── dto/                # PaymentRequestDTO, PaymentResponseDTO
│   ├── entity/             # PaymentEntity
│   ├── enums/              # PaymentMethod, PaymentStatus
│   ├── exception/          # PagoException
│   ├── repository/         # PaymentRepository
│   └── service/            # PaymentService
├── notification/            # Sistema de Notificaciones
│   ├── controller/         # NotificationController
│   ├── dto/                # NotificationEventDTO, NotificationRequestDTO
│   ├── entity/             # NotificationEntity
│   ├── enums/              # NotificationStatus, NotificationType
│   ├── exception/          # NotificacionException
│   ├── repository/         # NotificationRepository
│   └── service/            # NotificationService
└── location/                # Geolocalización
    ├── service/            # DistanceService
    └── util/               # CoordinatesUtil
```

---

## 🚀 Cómo Ejecutar

### Prerrequisitos

```bash
Java 17+
MySQL 8+ o PostgreSQL
Gradle 7+ o Maven 3.8+
```

### Configuración de Base de Datos

```properties
# src/main/resources/application.properties

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/taxi_db?useSSL=false&serverTimezone=America/Lima
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT
jwt.secret=tuClaveSecretaParaJWTDebeSerLargaYSegura
jwt.expiration=86400000

# Puerto
server.port=8080
```

### Ejecutar la Aplicación

```bash
# Clonar el repositorio
git clone https://github.com/GianMagonzales-netizen/automatizacion-asignacion-servicios.git

# Entrar al directorio
cd automatizacion-asignacion-servicios

# Ejecutar con Gradle
./gradlew bootRun

# O con Maven
./mvnw spring-boot:run
```

La aplicación correrá en: `http://localhost:8080`

---

## 📍 Endpoints de la API

### Autenticación

| Método | Endpoint             | Descripción                  |
| ------ | -------------------- | ---------------------------- |
| POST   | `/api/auth/login`    | Iniciar sesión (retorna JWT) |
| POST   | `/api/auth/register` | Registrar nuevo usuario      |

### Orquestador (Flujo Completo) ⭐

| Método | Endpoint                                                | Descripción                          |
| ------ | ------------------------------------------------------- | ------------------------------------ |
| POST   | `/api/orchestrator/ride`                                | Procesar solicitud COMPLETA de viaje |
| GET    | `/api/orchestrator/trip/{rideRequestId}`                | Obtener estado COMPLETO del viaje    |
| GET    | `/api/orchestrator/status/{rideRequestId}`              | Obtener estado SIMPLE del viaje      |
| POST   | `/api/orchestrator/accept/{rideRequestId}/{driverId}`   | Conductor acepta viaje               |
| POST   | `/api/orchestrator/reject/{rideRequestId}/{driverId}`   | Conductor rechaza viaje              |
| POST   | `/api/orchestrator/complete/{rideRequestId}/{driverId}` | Completar viaje                      |
| POST   | `/api/orchestrator/cancel/{rideRequestId}/{clientId}`   | Cancelar viaje                       |
| POST   | `/api/orchestrator/reassign/{rideRequestId}`            | Reasignar viaje a otro conductor     |
| POST   | `/api/orchestrator/rate`                                | Calificar conductor                  |

### Conductores

| Método | Endpoint                     | Descripción                      |
| ------ | ---------------------------- | -------------------------------- |
| GET    | `/api/drivers`               | Listar todos los conductores     |
| GET    | `/api/drivers/{id}`          | Obtener conductor por ID         |
| GET    | `/api/drivers/available`     | Listar conductores disponibles   |
| GET    | `/api/drivers/nearby`        | Conductores cercanos a ubicación |
| POST   | `/api/drivers`               | Registrar nuevo conductor        |
| PUT    | `/api/drivers/{id}/status`   | Cambiar estado del conductor     |
| PUT    | `/api/drivers/{id}/location` | Actualizar ubicación             |

### Pagos

| Método | Endpoint                             | Descripción                  |
| ------ | ------------------------------------ | ---------------------------- |
| POST   | `/api/payments/process`              | Procesar pago                |
| GET    | `/api/payments/{id}`                 | Obtener pago por ID          |
| GET    | `/api/payments/ride/{rideRequestId}` | Obtener pago por ID de viaje |

### Notificaciones

| Método | Endpoint                               | Descripción                 |
| ------ | -------------------------------------- | --------------------------- |
| GET    | `/api/notifications/client/{clientId}` | Notificaciones de cliente   |
| GET    | `/api/notifications/driver/{driverId}` | Notificaciones de conductor |
| PUT    | `/api/notifications/{id}/read`         | Marcar como leída           |

---

## 📤 Ejemplos de Solicitudes

### 1. Registrar Usuario

```json
POST /api/auth/register
Content-Type: application/json

{
  "email": "cliente@example.com",
  "password": "123456",
  "name": "Juan Pérez",
  "role": "CLIENT"
}
```

### 2. Iniciar Sesión

```json
POST /api/auth/login
Content-Type: application/json

{
  "email": "cliente@example.com",
  "password": "123456"
}
```

### 3. Solicitar Viaje (Flujo Completo)

```json
POST /api/orchestrator/ride
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "clientId": 1,
  "serviceType": "STANDARD",
  "pickupLat": -12.046374,
  "pickupLng": -77.042793,
  "pickupAddress": "Av. Arequipa 123, Lima",
  "destinationLat": -12.093505,
  "destinationLng": -77.062674,
  "destinationAddress": "Av. Larco 456, Miraflores",
  "paymentMethod": "CARD"
}
```

### Respuesta Exitosa

```json
{
  "success": true,
  "message": "Ride completed successfully",
  "rideRequestId": 10,
  "assignmentId": 5,
  "paymentId": 3,
  "status": "COMPLETED",
  "driverName": "Carlos Pérez",
  "driverVehiclePlate": "ABC-123",
  "serviceType": "STANDARD",
  "estimatedFare": 25.50,
  "finalFare": 25.50,
  "transactionCode": "TXN-20241201-001",
  "distanceKm": 5.2,
  "estimatedTimeMinutes": 15
}
```

### 4. Obtener Estado Completo del Viaje

```json
GET /api/orchestrator/trip/10
Authorization: Bearer {JWT_TOKEN}
```

### Respuesta de Estado

```json
{
  "rideRequestId": 10,
  "rideStatus": "IN_PROGRESS",
  "createdAt": "2024-12-01T10:30:00",
  "completedAt": null,
  "driverName": "Carlos Pérez",
  "driverVehiclePlate": "ABC-123",
  "serviceType": "STANDARD",
  "pickupAddress": "Av. Arequipa 123, Lima",
  "destinationAddress": "Av. Larco 456, Miraflores",
  "distanceKm": 5.2,
  "estimatedFare": 25.50,
  "finalFare": null,
  "paymentMethod": "CARD",
  "currentStatus": "IN_PROGRESS",
  "assignmentAttempts": 1,
  "estimatedArrivalTime": "14:45"
}
```

### 5. Conductor Acepta Viaje

```json
POST /api/orchestrator/accept/10/5
Authorization: Bearer {JWT_TOKEN}
```

### 6. Conductor Rechaza Viaje (Se reasigna automáticamente)

```json
POST /api/orchestrator/reject/10/5?reason=Too far away
Authorization: Bearer {JWT_TOKEN}
```

### 7. Completar Viaje

```json
POST /api/orchestrator/complete/10/5
Authorization: Bearer {JWT_TOKEN}
```

### 8. Cancelar Viaje

```json
POST /api/orchestrator/cancel/10/1
Authorization: Bearer {JWT_TOKEN}
```

### 9. Calificar Conductor

```json
POST /api/orchestrator/rate
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "rideRequestId": 10,
  "clientId": 1,
  "driverId": 5,
  "rating": 5,
  "comment": "Excelente servicio, muy puntual"
}
```

---

## 🧮 Lógica de Selección de Conductor

El sistema selecciona el mejor conductor utilizando 4 criterios:

| Criterio         | Peso | Descripción                                   |
| ---------------- | ---- | --------------------------------------------- |
| Distancia        | 40%  | Conductores más cercanos al punto de recogida |
| Calificación     | 30%  | Conductores con mejor puntuación promedio     |
| Disponibilidad   | 20%  | Conductores con estado AVAILABLE              |
| Tipo de Servicio | 10%  | Coincidencia exacta del tipo de servicio      |

### Algoritmo

```java
DriverEntity findBestMatchDriverEntity(ServiceType serviceType, Double pickupLat, Double pickupLng) {
    // 1. Filtrar por tipo de servicio y estado disponible
    // 2. Calcular distancia a punto de recogida
    // 3. Ordenar por: distancia (asc) + calificación (desc)
    // 4. Retornar el mejor candidato
}
```

---

## 🎯 Características del Sistema

### ✅ Implementado

* [x] Autenticación JWT
* [x] CRUD completo de conductores
* [x] Selección inteligente de conductor (4 criterios)
* [x] Asignación automática de servicios
* [x] Procesamiento de pagos
* [x] Reasignación automática cuando conductor rechaza
* [x] Seguimiento de viajes en tiempo real
* [x] Cálculo de distancias y tarifas
* [x] Calificación de conductores
* [x] Notificaciones simuladas
* [x] Auditoría de operaciones

### 🔄 En progreso / Mejoras futuras

* [ ] WebSockets para ubicación en tiempo real
* [ ] Integración real con pasarelas de pago (Stripe, PayPal)
* [ ] Envío real de notificaciones (email, SMS, push)
* [ ] Dashboard administrativo
* [ ] Reportes y analytics
* [ ] Cache con Redis

---

## 📊 Estados del Viaje

```text
PENDING → SEARCHING_DRIVER → ASSIGNED → ACCEPTED → IN_PROGRESS → COMPLETED
    ↓           ↓                ↓
CANCELLED   NO_DRIVERS_AVAILABLE  REJECTED → REASSIGNING → ASSIGNED
```

| Estado             | Descripción                              |
| ------------------ | ---------------------------------------- |
| `PENDING`          | Solicitud creada, esperando búsqueda     |
| `SEARCHING_DRIVER` | Buscando conductor disponible            |
| `ASSIGNED`         | Conductor asignado, esperando aceptación |
| `ACCEPTED`         | Conductor aceptó el viaje                |
| `IN_PROGRESS`      | Viaje en curso                           |
| `COMPLETED`        | Viaje completado                         |
| `CANCELLED`        | Viaje cancelado por cliente              |
| `REJECTED`         | Conductor rechazó la asignación          |
| `REASSIGNING`      | Buscando nuevo conductor                 |

---

## 🐛 Manejo de Excepciones

El sistema maneja las siguientes excepciones:

| Excepción                 | Descripción                    |
| ------------------------- | ------------------------------ |
| `DriverNotFoundException` | Conductor no encontrado        |
| `RideException`           | Error en solicitud de viaje    |
| `AssignmentException`     | Error en asignación            |
| `PagoException`           | Error en procesamiento de pago |
| `OrquestadorException`    | Error en orquestación          |

---

## 👨‍💻 Autor

**Gianmarco Gonzales**

* GitHub: https://github.com/GianMagonzales-netizen

---

## 📄 Licencia

Este proyecto es privado y de uso exclusivo para el desarrollo de la automatización de asignación de servicios.

---

## 🙏 Agradecimientos

Sistema desarrollado como parte de arquitectura de microservicios para la gestión eficiente de servicios de taxi.

---

# 🚀 ¡Gracias por usar Taxi Orchestrator!
