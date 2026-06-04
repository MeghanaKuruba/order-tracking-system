# order-tracking-system
A backend-focused food delivery application built using Java and Spring Boot with a microservices-based architecture.  
This project is being developed to gain hands-on experience with real-world backend development concepts like service separation, authentication, asynchronous communication, scalable system design, and frontend integration.
The project is currently under active development and new features are continuously being added.
---
## Tech Stack
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
## Services Created
- Auth Service
- Order Service
- Delivery Service
- Restaurant Service
- Payment Service (basic setup)
  The services are currently being developed independently and will be expanded further as the project grows.
---
## Features Implemented
### Authentication
- JWT token generation
- Spring Security basic setup
- Authentication flow structure
### Order Management
- Basic order handling APIs
- Order status management
- Initial delivery assignment flow
### Delivery Management
- Delivery status updates
- Delivery workflow handling
- Exception handling and validations
### Inter-Service Communication
- Service-to-service communication using RestTemplate
- Communication implemented between Restaurant Service and Order Service
- Service discovery integration planned for future implementation
### Kafka Integration
- Kafka integration using KRaft mode
- Basic event-driven communication setup
- Producer and consumer implementation
### Backend Architecture
- Microservices-based project structure
- Layered architecture implementation
- DTOs, validations, and exception handling
- Service-wise separation of responsibilities
---
## Current Project Status
The project is still in development and currently focuses mainly on backend architecture and service design.
Some planned improvements for future implementation:
- API Gateway
- Service Discovery using Eureka
- Complete authentication and authorization flow
- Frontend integration using React or Angular
- MySQL integration
- Docker setup
- Improved inter-service communication
- Monitoring and logging
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
│   ├── order-service/
│   ├── delivery-service/
│   ├── restaurant-service/
│   ├── payment-service/
├──frontend/ (planned for future development)
```
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
### Steps
1. Clone the repository
2. Start Kafka in KRaft mode
(Create these topics: 
   - delivery-status-updated,
   - delivery-assigned
   - order-confirmed,
   - order-created,
   - order-ready-for-pickup,
   - payment-success,
   - restaurant-order-status )
3. Run individual microservices
4. Access APIs using Postman
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
Project is actively being developed and improved as new features and requirements are explored.