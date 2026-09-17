# Fleet Management & IoT Telemetry System

A modern, high-throughput, enterprise-grade Fleet Management and IoT Telemetry platform built with **Java 21**, **Spring Boot 3.5**, **MySQL**, **MongoDB**, **MQTT**, **WebSockets (STOMP)**, and **Firebase Cloud Messaging (FCM)**.

The system is designed for commercial fleets, public service vehicles (PSVs), and transport cooperatives (SACCOs). It provides real-time GPS tracking, dual-database storage (relational + time-series), automated alert rule engines (overspeeding, fuel theft, battery discharge, geofence breaches), trip lifecycle monitoring, and multi-tenant role-based access control.

---

## Architecture Overview

```
                          ┌───────────────────────────┐
                          │   IoT Hardware / Trackers │
                          │  (ESP32, Teltonika, GPS)  │
                          └─────────────┬─────────────┘
                                        │ MQTT (TCP 1883)
                                        ▼
                          ┌───────────────────────────┐
                          │    Eclipse Mosquitto      │
                          │       MQTT Broker         │
                          └─────────────┬─────────────┘
                                        │ Telemetry Ingestion
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       SPRING BOOT BACKEND (Java 21)                         │
│                                                                             │
│   ┌───────────────────┐   ┌────────────────────┐   ┌────────────────────┐   │
│   │  MQTT Subscriber  │──▶│ Alert Rules Engine │──▶│ Firebase FCM Push  │   │
│   │  & Message Handler│   │  & Geofence Engine │   │   Notifications    │   │
│   └─────────┬─────────┘   └─────────┬──────────┘   └────────────────────┘   │
│             │                       │                                       │
│             │ MongoDB Document      │ MySQL JPA                             │
│             ▼                       ▼                                       │
│   ┌───────────────────┐   ┌────────────────────┐   ┌────────────────────┐   │
│   │      MongoDB      │   │       MySQL        │   │ WebSocket (STOMP)  │   │
│   │ (High-throughput  │   │ (Relational Core,  │──▶│  Broker / Topic    │   │
│   │  GPS Telemetry)   │   │  Users, Trips, DB) │   │  Broadcaster       │   │
│   └───────────────────┘   └────────────────────┘   └─────────┬──────────┘   │
└──────────────────────────────────────────────────────────────┼──────────────┘
                                                               │ Live Updates
                                                               ▼
                                                  ┌───────────────────────────┐
                                                  │  Clients (Flutter Mobile, │
                                                  │   React / Next.js Web)    │
                                                  └───────────────────────────┘
```

### Dual-Database Architecture
* **MySQL 8.0+ (Flyway Versioned)**: Stores transactional and relational data including Users, Vehicle Registry, Hardware Devices, SACCO Cooperatives, Geofences, Alerts, Trips, Energy logs, and Audit trails.
* **MongoDB (Time-Series / Unstructured)**: Stores continuous high-volume raw GPS telemetry data packets ingested over MQTT for millisecond-level location lookups and historical path reconstruction.

---

## Key Features

### 1. Authentication & Security
* **Stateless JWT**: Access tokens (15-min expiry) paired with database-persisted refresh token rotation.
* **Role-Based Access Control (RBAC)**: Support for `SUPER_ADMIN`, `ADMIN`, `DISPATCHER`, `DRIVER`, and `CUSTOMER`.
* **User Management**: Driver profile management, user suspension, and password update workflows.

### 2. Fleet & SACCO Operations
* **Vehicle Onboarding**: Register vehicles by VIN, plate number, powertrain (`ELECTRIC`, `ICE`, `HYBRID`), and vehicle category (`PSV`, `COMMERCIAL`, `PRIVATE`).
* **SACCO Management**: Multi-tenant cooperative management allowing vehicle assignment, route tracking, and SACCO fleet monitoring.
* **Hardware Device Registry**: Pair GPS trackers (IMEI, model, serial number) to specific vehicles with status monitoring.

### 3. Real-Time Telemetry & Tracking
* **MQTT Ingestion Pipeline**: High-throughput ingestion on `fleet/vehicles/{deviceId}/telemetry`.
* **Live WebSocket Broadcast**: Instant location push to frontend maps via STOMP topics (`/topic/vehicle/{id}/location`).
* **Current Position & Spatial Queries**: Real-time position query endpoints with coordinates, heading, altitude, and ignition status.

### 4. Smart Alert Engine & Geofencing
* **Automated Violation Detection**:
  * **Overspeeding**: Speed threshold violations (> 80 km/h configurable).
  * **Low Battery / Fuel**: Critical energy level drop notifications.
  * **Fuel Theft Detection**: Triggers when sudden negative fuel drop occurs without distance movement.
  * **Geofence Breach**: Automatic entry and exit detection against circular geofences.
* **Alert Resolution Lifecycle**: View active alerts, unacknowledged counters, acknowledge alerts, and log resolution notes.
* **Firebase Push Notifications**: Instant background notifications delivered via FCM Admin SDK.

### 5. Trips & Energy Analytics
* **Trip Lifecycle**: Start and stop trips with odometer tracking, duration calculation, and automated route point capture.
* **Energy Management**: Battery state-of-charge (SoC) tracking for EVs and fuel consumption metrics for combustion engines.

---

## Tech Stack

| Component | Technology / Library |
| :--- | :--- |
| **Runtime & Language** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.5 (Spring MVC, Spring Security, Spring Integration) |
| **Relational Database** | MySQL 8.x + Hibernate / Spring Data JPA |
| **Database Migrations** | Flyway (13 incremental versioned SQL migrations) |
| **Time-Series / NoSQL** | MongoDB 6.x + Spring Data MongoDB |
| **IoT / Messaging** | Eclipse Paho MQTT Client, Eclipse Mosquitto Broker |
| **Real-time Streaming** | Spring WebSocket (STOMP Broker) |
| **Push Notifications** | Google Firebase Admin SDK (FCM) |
| **Security & Token** | Spring Security + JJWT 0.12.6 |
| **Code Generation & Utils** | MapStruct 1.6.3, Project Lombok, Spring Dotenv |

---

## Project Structure

```text
src/main/java/com/vibran/
├── auth/                       # JWT Authentication, tokens, security filters & controller
├── domain/                     # Domain modules (Clean Architecture)
│   ├── alert/                  # Alert rules engine, alert entity, repository & controller
│   ├── device/                 # Hardware tracker device registry & management
│   ├── energy/                 # Energy and fuel tracking services & controller
│   ├── geofence/               # Geofence boundary calculations & controller
│   ├── notification/           # FCM service, notification persistence & controller
│   ├── sacco/                  # SACCO cooperatives & member fleet management
│   ├── trip/                   # Trip lifecycle, route recording & controller
│   ├── user/                   # User entities, profile management & controller
│   └── vehicle/                # Vehicle registry, models & controller
├── telemetry/                  # MQTT ingestion handler, telemetry processing & controller
├── shared/                     # Global exception handling, API wrappers, pagination
└── config/                     # Security, WebSocket, MQTT, Mongo, and Firebase configs
```

---

## Getting Started

### Prerequisites
* **Java**: OpenJDK 21 or later
* **Build Tool**: Maven 3.9+ (or included `./mvnw`)
* **Databases**: 
  * MySQL Server 8.0+ running on port `3306`
  * MongoDB Server 6.0+ running on port `27017`
* **MQTT Broker**: Eclipse Mosquitto running on port `1883`
* **Firebase** *(Optional)*: Service Account JSON key for push notifications

---

### Environment Configuration

Create a `.env` file in the root directory (or set environment variables):

```properties
# Server
SERVER_PORT=8080
APP_PROFILE=dev

# MySQL Database
DB_URL=jdbc:mysql://localhost:3306/fleet_management_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

# MongoDB
MONGO_URI=mongodb://localhost:27017/fleetdb

# JWT Security
JWT_SECRET=your-256-bit-secret-key-make-sure-it-is-sufficiently-long-and-secure
JWT_ACCESS_EXPIRY_MS=900000        # 15 minutes
JWT_REFRESH_EXPIRY_MS=604800000    # 7 days

# MQTT Broker Configuration
MQTT_BROKER_URL=tcp://localhost:1883
MQTT_CLIENT_ID=fleet-spring-backend
MQTT_USERNAME=
MQTT_PASSWORD=

# Firebase Cloud Messaging
FIREBASE_CREDENTIALS_PATH=classpath:firebase-service-account.json
```

---

### Database Setup & Migrations

1. Ensure MySQL and MongoDB services are active.
2. The application uses **Flyway** to automatically apply database schema migrations on startup (found in `src/main/resources/db/migration/`).
3. To populate your database with test data (Users, Vehicles, Saccos, Devices), you can execute the provided seed script:
   ```bash
   mysql -u root -p fleet_management_db < seed_data.sql
   ```

---

### Building and Running

#### 1. Compile & Package
```bash
./mvnw clean package -DskipTests
```

#### 2. Run the Application
```bash
./mvnw spring-boot:run
```
The server will start at `http://localhost:8080`.

---

## API Reference Summary

### Authentication (`/api/v1/auth`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Authenticate user, returns JWT tokens | No |
| `POST` | `/api/v1/auth/refresh` | Rotate refresh token and get new access token | No |
| `POST` | `/api/v1/auth/logout` | Revoke user refresh tokens | Yes |

### Vehicle & Device Management (`/api/v1/vehicles`, `/api/v1/devices`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/vehicles` | Register a new vehicle | Admin / Dispatcher |
| `GET` | `/api/v1/vehicles` | List vehicles (paginated, filter by owner) | Yes |
| `GET` | `/api/v1/vehicles/{id}` | Get detailed vehicle information | Yes |
| `POST` | `/api/v1/devices` | Register a new IoT tracker device | Admin |
| `POST` | `/api/v1/devices/pair` | Pair IoT device with a vehicle | Admin |

### SACCO Fleet Operations (`/api/v1/saccos`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/saccos` | Register a SACCO cooperative | Admin |
| `GET` | `/api/v1/saccos` | List all SACCOs | Yes |
| `POST` | `/api/v1/saccos/{id}/join` | Assign vehicle & owner to SACCO | Yes |
| `GET` | `/api/v1/saccos/{id}/fleet` | Get all vehicles in a SACCO fleet | Yes |

### Real-Time Telemetry & Tracking (`/api/v1/telemetry`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/telemetry/{vehicleId}/position` | Get latest known GPS position | Yes |
| `GET` | `/api/v1/telemetry/{vehicleId}/history` | Get historical telemetry points | Yes |

### Geofencing & Alerts (`/api/v1/geofences`, `/api/v1/alerts`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/geofences` | Create a circular geofence | Admin / Dispatcher |
| `GET` | `/api/v1/alerts/vehicle/{vehicleId}/unacknowledged` | Get active unacknowledged alerts | Yes |
| `POST` | `/api/v1/alerts/{id}/acknowledge` | Mark an alert as acknowledged | Yes |
| `POST` | `/api/v1/alerts/{id}/resolve` | Resolve alert with resolution notes | Yes |

### Trips & Energy (`/api/v1/trips`, `/api/v1/energy`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/trips/start` | Start a trip session | Driver / Dispatcher |
| `POST` | `/api/v1/trips/{id}/end` | End a trip and calculate summary | Driver / Dispatcher |
| `GET` | `/api/v1/energy/vehicle/{vehicleId}/latest` | Get latest energy & fuel metrics | Yes |

---

## Real-Time Messaging & IoT Ingestion

### MQTT Telemetry Payload Format
IoT devices publish telemetry packets to topic `fleet/vehicles/{deviceId}/telemetry`:
```json
{
  "deviceId": "DEV-ESP32-001",
  "vehicleId": 1,
  "timestamp": "2026-05-16T12:00:00Z",
  "latitude": -1.286389,
  "longitude": 36.817223,
  "speed": 65.5,
  "heading": 180.0,
  "altitude": 1680.0,
  "ignitionOn": true,
  "fuelLevelPercentage": 78.5,
  "batteryLevelPercentage": 92.0,
  "engineRpm": 2200,
  "odometerKm": 15420.5
}
```

### WebSocket (STOMP) Channels
* **Connection URL**: `ws://localhost:8080/ws` (with `Authorization: Bearer <JWT>` header)
* **Live Location Channel**: `/topic/vehicle/{vehicleId}/location`
* **Live Alert Channel**: `/topic/vehicle/{vehicleId}/alerts`
* **Fleet Alert Channel**: `/topic/owner/{ownerId}/alerts`

---

## Client & Mobile Integration

Detailed guides for building frontends and mobile clients connecting to this backend are available in the project documentation:
* **Flutter / Dart Mobile Guide**: Riverpod/Provider state, Google Maps markers, secure JWT storage, and background FCM setup.
* **React & Next.js Web Guide**: STOMP client configuration, Leaflet/Google Maps live vehicle tracking, and SSR dashboard implementations.
* **Testing & Postman Guide**: Complete Postman collection setup, WebSocket handshake configuration, and MQTT simulation scripts.

*(Refer to `PROJECT_DOCS_COMPILED.md` for full implementation walkthroughs).*

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.
#   F l e e t M a n a g e m e n t S p r i n g B a c k e n d 
 
 
