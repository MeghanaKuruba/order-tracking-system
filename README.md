# order-tracking-system

A scalable, event-driven food delivery backend system built using Java and Spring Boot microservices architecture.

This project simulates a real-world production system with features like asynchronous communication (Kafka), real-time tracking (WebSockets), scheduler-based automation, and secure authentication (JWT).

## 🚀 Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- Apache Kafka (KRaft Mode)
- H2 Database
- Maven
- REST APIs
- RestTemplate
---

## 📦 Services Created

- Auth Service
- Cart Service
- Delivery Service
- Order Service
- Restaurant Service
- Payment Service (basic setup)
- Tracking Service

  The services are currently being developed independently and will be expanded further as the project grows.
---

## ✅ Features Implemented

### 🔐 Authentication (Auth Service)
- JWT token generation
- Spring Security setup
- Basic authentication and authorization flow


### 🛒 Cart Management (Cart Service)
- Add items to cart
- Update item quantity
- Remove items from cart (in progress)
- Cart total calculation
- Validation and custom exception handling
- Restaurant-based cart restriction (single restaurant logic)

### 🍽️ Restaurant Service
- Fetch restaurant details
- Menu item retrieval
- Integration with Order Service for order validation

### 📦 Order Management (Order Service)
- Order creation and management
- Order status tracking (Created, Confirmed, Ready for Pickup, etc.)
- Integration with Cart and Restaurant services
- Event publishing for order lifecycle updates

### 🚚 Delivery Service
- Delivery assignment logic
- Delivery status updates (Assigned, Picked Up, Out for Delivery, Delivered)
- Retry mechanism for delivery partner assignment

#### ✅ Scheduler-Based Automation
Implemented background schedulers for automated delivery handling:
```text
scheduler/
├── DeliveryAssignmentScheduler
├── DeliveryMonitoringScheduler
├── MonitorAssignedTimeouts
├── MonitorSearchingTimeouts
```
- Scheduler-based automated workflows for delivery management:
    - Retry assigning delivery partners for pending orders
    - Monitor offline delivery partners and reassign deliveries
    - Handle assignment timeout (if partner does not pick up order)
    - Handle searching timeout (no partner found within time limit)
- Exception handling and validation for delivery operations
- Kafka event publishing for delivery status updates



### 💳 Payment Service (Basic Setup)
- Initial payment flow setup
- Payment success event handling (Kafka-based)

### 📍 Tracking Service
- Real-time delivery location updates
- WebSocket integration for live tracking
- Location update APIs
- Event-driven tracking updates

### 🔄 Kafka Event-Driven Communication
- Kafka setup using KRaft mode
- Producer implementation for events:
    - order-created
    - order-confirmed
    - order-ready-for-pickup
    - delivery-assigned
    - delivery-status-updated
    - payment-success
    - restaurant-order-status
- Consumer implementation across services
- Loose coupling between microservices using events

### 🔗 Inter-Service Communication
- REST-based communication using RestTemplate
- Service-to-service communication between:
    - Order ↔ Restaurant
    - Order ↔ Delivery
- Decoupled service architecture design

### 🏗️ Backend Architecture
- Microservices-based architecture
- Layered design (Controller, Service, Repository)
- DTO-based request/response handling
- Custom exception handling with global handler
- Clean separation of responsibilities across services
---

## 🚀 System Design Highlights

- Event-driven architecture using Kafka for loose coupling
- Asynchronous communication between Restaurant, Order, Payment, Delivery and Tracking services
- Scheduler-based background processing for reliability and failure handling
- Real-time tracking using WebSocket communication
- Microservices independently deployable and scalable

---

## 🔄 High-Level Workflow

1. User adds items to the cart and places an order *(cart-to-order flow currently in progress)*
2. Order Service creates the order
3. Order Service publishes an **"order-created"** event to Kafka
4. Payment Service consumes the event and marks the payment as successful and publishes an **"order-confirmed** event to Kafka
   *(currently basic implementation; will be enhanced with real payment processing later)*
5. Restaurant Service consumes the **"order-confirmed"** event and begins order preparation
6. Restaurant Service publishes an **"order-ready-for-pickup"** event to Kafka
7. Delivery Service consumes the event and initiates delivery partner assignment
8. Scheduler continuously monitors delivery assignment and retries if required
9. Tracking Service provides real-time delivery location updates using WebSocket *(in progress)*

#### ✅ Kafka is used to **synchronize and propagate order status updates across Order, Restaurant, and Delivery services**, ensuring consistency and loose coupling between microservices.

---

## 🧩 High-Level Architecture


```text
               ┌───────────────┐
               │     User      │
               └──────┬────────┘
                      │
                      ▼
               ┌───────────────┐
               │  API Requests │
               └──────┬────────┘
                      │
                      ▼
     ┌─────────────────────────────────────┐
     │           Microservices             │
     └─────────────────────────────────────┘
     │  Auth Service       │ Cart Service  │
     │  Order Service      │ Restaurant    │
     │  Payment Service    │ Delivery      │
     │  Tracking Service                   │
     └─────────────────────────────────────┘
                       │
                       ▼
             ┌────────────────────┐
             │    Apache Kafka    │
             └────────────────────┘
                       │
                       ▼
        ┌───────────────────────────────┐
        │   Scheduler (Background Jobs) │
        └───────────────────────────────┘
                       │
                       ▼
        ┌───────────────────────────────┐
        │   Tracking Service (WS)       │
        └───────────────────────────────┘
```
---
## 🔄 Order to Delivery Flow

```text
User
  │
  ▼
Cart Service
  │
  ▼
Order Service
  │
  ├──► (order-created event) ─────► Kafka
  │
  ▼
Payment Service
  │
  ├──► (payment-success) ────────► Kafka
  │
  ▼
Restaurant Service
  │
  ├──► (order-ready-for-pickup) ► Kafka
  │
  ▼
Delivery Service
  │
  ├── Assign Delivery Partner
  │
  ▼
Scheduler
  │
  ├── Retry Assignment
  ├── Timeout Handling
  │
  ▼
Tracking Service
  │
  ▼
WebSocket → Live Location Updates
```
---

## ⏱️ Delivery Scheduler Workflow

```text
                         Scheduler Engine
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
        ▼                      ▼                      ▼

DeliveryAssignment      DeliveryMonitoring     Timeout Handlers
   Scheduler                Scheduler
(retry logic)         (partner monitoring)

        │                      │
        ▼                      ▼

  Retry Partner        Check Partner Status
   Assignment          (Active / Inactive)
        │                      │
        ▼                      ▼
  Assign Partner    ┌───────────────────────────────┐
  (if available)    │   Partner becomes inactive?   │
        │           └───────────────┬───────────────┘
        ▼                           │
     SUCCESS                    YES │ NO
        │                           ▼
        ▼                 ┌────────────────────┐
       DONE               │ Delivery Status?   │
                          └────────┬──────────┘
                                   │
                    ┌──────────────┼───────────────┐
                    │                              │
                    ▼                              ▼
             ASSIGNED STATUS             PICKED_UP / OUT_FOR_DELIVERY
                    │                              │
                    ▼                              ▼
            Reassign Partner             Mark DELIVERY_EXCEPTION

──────────────────────────────────────────────────────────────────────────

             ⏳ Timeout Handling (Separate Schedulers)

                  ┌───────────────────────────────┐
                  │    MonitorAssignedTimeouts    │
                  └───────────────┬───────────────┘
                                  │
                                  ▼
                 Assigned but not picked up in time?
                                  │
                     ┌────────────┴────────────┐
                     │                         │
                     NO                       YES
                     │                         ▼
                     │              Make Partner Available
                     │              Remove Assignment
                     │              Set SEARCHING_FOR_PARTNER
                     │              Retry Assignment
                     ▼
                    END


                 ┌───────────────────────────────┐
                 │    MonitorSearchingTimeouts   │
                 └──────────────┬────────────────┘
                                │
                                ▼
                Searching too long without partner?
                                │
                   ┌────────────┴────────────┐
                   │                         │
                   NO                       YES
                   │                         ▼
                   │              Mark DELIVERY_EXCEPTION
                   ▼
                  END

```
---
## Current Project Status
- Backend core services implemented
- Kafka-based event-driven communication working
- Delivery workflow and scheduler logic implemented
- WebSocket-based live tracking integrated
- Actively enhancing system with advanced features
---
## Database
Currently using:
- H2 Database for development and testing
  Planned:
- Migration to MySQL in future stages of the project
---
## Kafka Setup
Kafka is configured and running in:
- KRaft Mode (without Zookeeper)
---
## Project Structure
```text
order-tracking-app/
│
├──backend/
│   ├── auth-service/
│   ├── cart-service/
│   ├── order-service/
│   ├── delivery-service/
│   ├── restaurant-service/
│   ├── payment-service/
│   └── tracking-service/
└──frontend/ (planned for future development)
```
---
## ⚡ Challenges & Solutions

- Handling delivery partner unavailability → solved using scheduler-based retry logic
- Maintaining consistency across services → solved using Kafka event-driven architecture
- Preventing data inconsistency in cart → implemented validation & exception handling
- Avoiding tight coupling between services → used asynchronous communication via Kafka
- Handling real-time updates → implemented WebSocket-based tracking service

---
## 💡 Key Highlights

- Implements real-world microservices architecture
- Uses event-driven communication with Kafka
- Handles failure scenarios with retry + timeout mechanisms
- Includes scheduler-based background processing
- Supports real-time tracking using WebSockets
- Designed for scalability and loose coupling
---
## Learning Goals Behind This Project
This project is mainly being built to understand:
- Microservices architecture
- Backend system design
- Event-driven communication using Kafka
- Secure authentication systems
- Scalable backend development practices
- Real-world API development
- Frontend design and integration using modern frameworks
---
## How to Run
### Prerequisites
- Java 17+
- Maven
- Apache Kafka

--------------------------------------------------

### ✅ Steps

1. Clone the repository

--------------------------------------------------

2. Start Kafka Server (KRaft Mode)

→ Create Kafka Cluster (only once)

bin\windows\kafka-storage.bat random-uuid

Copy the generated UUID


--------------------------------------------------

→ Format Kafka Storage (only once)

bin\windows\kafka-storage.bat format --standalone -t <your-UUID> -c config\server.properties

Replace <your-UUID> with the generated UUID

--------------------------------------------------

→ Start Kafka Server

bin\windows\kafka-server-start.bat config\server.properties

--------------------------------------------------

3. Create Kafka Topics

Option 1: Create individually

bin\windows\kafka-topics.bat --create --topic <topic-name> --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

--------------------------------------------------

Option 2 (Recommended): Use batch file

Create file: create-topics.bat

Paste:

@echo off  
echo Creating Kafka topics...

call bin\windows\kafka-topics.bat --create --topic delivery-status-updated --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1  
call bin\windows\kafka-topics.bat --create --topic delivery-assigned --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1  
call bin\windows\kafka-topics.bat --create --topic order-confirmed --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1  
call bin\windows\kafka-topics.bat --create --topic order-created --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1  
call bin\windows\kafka-topics.bat --create --topic order-ready-for-pickup --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1  
call bin\windows\kafka-topics.bat --create --topic payment-success --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1  
call bin\windows\kafka-topics.bat --create --topic restaurant-order-status --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

echo All topics created successfully!  
pause

Run it:

create-topics.bat

--------------------------------------------------

→ Verify topics

bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

--------------------------------------------------

Required Topics:

- delivery-status-updated
- delivery-assigned
- order-confirmed
- order-created
- order-ready-for-pickup
- payment-success
- restaurant-order-status

--------------------------------------------------

4. Start Microservices (in order)

- Auth Service
- Restaurant Service
- Order Service
- Payment Service
- Delivery Service
- Tracking Service
- Cart Service

--------------------------------------------------

5. Test APIs

Use Postman to test REST APIs

--------------------------------------------------

6. Optional

Use browser or WebSocket client for real-time tracking updates

---
## Future Enhancements
- API Gateway implementation
- Eureka/Service Discovery
- Frontend development using React or Angular
- Docker and containerization
- Redis caching
- Notification service
- Real-time order tracking
- CI/CD pipeline setup
---
## Status
🚀 This project is actively evolving with continuous improvements, feature additions, and production-level enhancements.
