# Herald

A Spring Boot-based microservice that handles multi-channel notifications (Email, SMS) through a Kafka-driven event streaming architecture.

## Overview

Herald is designed to process notification events from a Kafka stream and deliver them through multiple channels including email (via Mailjet) and SMS (via Twilio). It provides a scalable, event-driven architecture for managing notifications across your system.

## Features

- **Multi-Channel Notifications**: Support for Email and SMS notifications
- **Kafka Event Streaming**: Asynchronous event processing for high throughput
- **Email Provider Integration**: Mailjet integration for reliable email delivery
- **SMS Provider Integration**: Twilio integration for SMS messaging
- **OTP Management**: One-time password generation and validation
- **Redis Caching**: Built-in caching layer for improved performance
- **Database Migrations**: Flyway-based schema management
- **Docker Support**: Containerized deployment ready

## Tech Stack

- **Framework**: Spring Boot 3.x
- **Messaging**: Apache Kafka
- **Cache**: Redis
- **Email**: Mailjet API
- **SMS**: Twilio API
- **Database**: PostgreSQL (via migrations)
- **Logging**: Logstash integration
- **Build Tool**: Maven

## Project Structure

```
Herald/
├── src/
│   ├── main/
│   │   ├── java/com/notification/herald/
│   │   │   ├── controllers/        # REST endpoints
│   │   │   ├── services/           # Business logic
│   │   │   ├── consumers/          # Kafka consumers
│   │   │   ├── providers/          # Email & SMS providers
│   │   │   ├── dto/                # Data transfer objects
│   │   │   ├── entities/           # JPA entities
│   │   │   ├── configurations/     # App configurations
│   │   │   └── utils/              # Utility classes
│   │   └── resources/
│   │       ├── application.yml     # Application configuration
│   │       └── db/migration/       # Database migrations
│   └── test/
├── docker-compose.yaml             # Local development setup
├── Dockerfile                       # Container image definition
├── pom.xml                         # Maven configuration
└── logstash/                       # Log aggregation config
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Kafka
- Redis
- PostgreSQL

## Support

For issues, questions, or contributions, please refer to the project repository or contact the development team.
