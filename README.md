# Herald

A Spring Boot-based microservice that handles multi-channel notifications (Email, SMS, In-App) through a Kafka-driven event streaming architecture.

## Overview

Herald is designed to process notification events from a Kafka stream and deliver them through multiple channels including email (via Mailjet), SMS (via Twilio), and in-app messages (persisted to a user inbox). It provides a scalable, event-driven architecture for managing notifications across your system, and also handles OTP generation/validation and reusable notification templates.

A REST request is persisted with a generated request ID, published to a Kafka topic, and processed asynchronously by a channel-specific consumer that calls the underlying provider. Delivery results are persisted back to PostgreSQL, and failed sends are retried (3 attempts, 1s delay).

## Features

- **Multi-channel notifications** — Email (Mailjet), SMS (Twilio), and In-App (DB inbox)
- **Kafka event streaming** — async, decoupled producers and consumers per channel
- **Templates** — create and trigger reusable Email/SMS templates with variable substitution
- **OTP management** — 5-digit OTP, BCrypt-hashed, cached in Redis with TTL, delivered via the notification pipeline
- **Status tracking** — query notification status by request ID
- **Redis caching** — OTP storage
- **Flyway migrations** — versioned PostgreSQL schema management
- **Observability** — Elastic APM, Logstash, and Kibana integration
- **Docker support** — containerized app plus infra via Docker Compose

## Tech Stack

| Concern       | Technology                    |
|---------------|-------------------------------|
| Language      | Java 21                       |
| Framework     | Spring Boot 3.5.9             |
| Messaging     | Apache Kafka                  |
| Database      | PostgreSQL + Flyway           |
| Cache         | Redis (Jedis)                 |
| Email         | Mailjet API                   |
| SMS           | Twilio API                    |
| Observability | Elastic APM, Logstash, Kibana |
| Build         | Maven                         |

## Project Structure

```
src/main/java/com/notification/herald/
├── configurations/       # Kafka, Redis, RestClient, beans, exception handler
├── controllers/          # Notification, Email, SMS, InApp, Template, Otp
├── services/             # Notification, Email, SMS, InApp, Otp, Template, Kafka, persistence
├── consumers/            # EmailConsumer, SMSConsumer, InAppConsumer (Kafka listeners)
├── providers/
│   ├── mail/             # MailProvider + MailjetImpl
│   ├── sms/              # SMSProvider + TwilioImpl
│   └── inapp/            # InAppProvider + InAppImpl
├── dto/                  # Request/response DTOs (mail, sms, inapp, otp, template)
├── entities/             # NotificationEntity, InAppNotificationEntity, TemplateEntity
├── enums/                # NotifTypeEnum, NotificationStatusEnum, provider/lang enums
├── repository/           # Notification, InApp, Template (Spring Data JPA)
└── utils/                # MailUtil, SMSUtil, RequestUtils, TemplateRenderer
```

## API Endpoints

### Notifications

| Method | Path                        | Description                          |
|--------|-----------------------------|--------------------------------------|
| GET    | `/notification?requestId=`  | Get notification status by request ID |
| POST   | `/notification/email`       | Trigger an email notification         |
| POST   | `/notification/sms`         | Trigger an SMS notification           |
| POST   | `/notification/inapp`       | Trigger an in-app notification        |
| GET    | `/notification/inapp?uuid=` | Get a user's in-app inbox             |

### Templates

| Method | Path                      | Description                    |
|--------|---------------------------|--------------------------------|
| POST   | `/template/create/email`  | Create an email template       |
| POST   | `/template/create/sms`    | Create an SMS template         |
| POST   | `/template/trigger/email` | Trigger a stored email template |
| POST   | `/template/trigger/sms`   | Trigger a stored SMS template   |

### OTP

| Method | Path            | Description                   |
|--------|-----------------|-------------------------------|
| POST   | `/otp/request`  | Generate and send an OTP      |
| POST   | `/otp/validate` | Validate a submitted OTP      |

### Example requests

```jsonc
// POST /notification/email
{ "toEmail": "user@example.com", "toName": "User", "subject": "Hi", "content": "Hello world" }

// POST /notification/sms
{ "toMobile": "+15551234567", "content": "Your code is ready" }

// POST /notification/inapp
{ "uuid": "user-123", "title": "New message", "content": "You have an update" }
```

## Kafka Topics

| Topic    | Consumer       |
|----------|----------------|
| `EMAIL`  | `EmailConsumer` |
| `SMS`    | `SMSConsumer`   |
| `IN_APP` | `InAppConsumer` |

## Getting Started

### Prerequisites

- Java 21
- Maven 3.8+ (a Maven wrapper `./mvnw` is included)
- Docker & Docker Compose
- Running Kafka, Redis, and PostgreSQL (provided via `docker-compose`)

### Required environment variables

```
MAILJET_APIKEY
MAILJET_SECRET
TWILIO_BASEURL
TWILIO_USERNAME
TWILIO_PASSWORD
TWILIO_SERVICE_ID
```

### Run

```bash
# Start infrastructure (Kafka, Redis, ELK stack)
docker-compose up -d

# Build
./mvnw clean package

# Run
./mvnw spring-boot:run
```

The app runs on **port 9500**.

## Infrastructure Ports

| Service       | Port  |
|---------------|-------|
| App           | 9500  |
| Kafka         | 9092  |
| Redis         | 6379  |
| Elasticsearch | 9200  |
| Kibana        | 5601  |
| APM Server    | 8200  |

## Development

- **Formatting** — Spotless with Google Java Format runs on `verify` and fails the build if code is unformatted. Run `./mvnw spotless:apply` to fix.
- **Coverage** — JaCoCo reports are generated during the `test` phase.

## Support

For issues, questions, or contributions, refer to the project repository or contact the development team.
