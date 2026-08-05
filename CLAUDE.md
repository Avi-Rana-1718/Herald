# Herald - Notification Microservice

## Project Overview

Herald is a Spring Boot microservice for multi-channel notifications (Email and SMS) using a Kafka-driven event streaming architecture. It integrates with Mailjet (email), Twilio (SMS), Redis (OTP caching), and PostgreSQL (persistence).

## Tech Stack

- **Java 21**, **Spring Boot 3.5.9**
- **Apache Kafka 3.7.0** — event streaming
- **PostgreSQL** with **Flyway** migrations
- **Redis** — OTP caching
- **Mailjet** — email delivery
- **Twilio** — SMS delivery
- **Elastic APM + Logstash + Kibana** — observability
- **Maven** — build tool

## Common Commands

```bash
# Build
./mvnw clean package

# Run
./mvnw spring-boot:run

# Start infrastructure (Kafka, Redis, ELK stack)
docker-compose up -d

# Stop infrastructure
docker-compose down
```

App runs on **port 9500**.

## Required Environment Variables

```
MAILJET_APIKEY
MAILJET_SECRET
TWILIO_BASEURL
TWILIO_USERNAME
TWILIO_PASSWORD
TWILIO_SERVICE_ID
```

## Project Structure

```
src/main/java/com/notification/herald/
├── configurations/       # Kafka, Redis, RestClient, exception handler
├── controllers/          # NotificationController, OtpController
├── services/             # NotificationService, OtpService, KafkaProviderService
├── consumers/            # EmailConsumer, SMSConsumer (Kafka listeners)
├── providers/
│   ├── mail/             # MailProvider interface + MailjetImpl
│   └── sms/              # SMSProvider interface + TwilioImpl
├── dto/                  # Request/response DTOs
├── entities/             # NotificationEntity (JPA)
├── enums/                # NotifTypeEnum, NotificationStatusEnum, etc.
├── repository/           # NotificationRepository (Spring Data JPA)
└── utils/                # MailUtil, SMSUtil, RequestUtils
```

## API Endpoints

**Notifications** (`/notification`):
- `POST /notification` — trigger notifications (returns request IDs, HTTP 201)
- `GET /notification?requestId=xxx` — get notification status

**OTP** (`/otp`):
- `POST /otp/request` — generate and send OTP
- `POST /otp/validate` — validate submitted OTP

## Architecture

Event-driven flow:
1. REST request → `NotificationService` (persists with REQUESTED status, generates request ID)
2. Kafka publish → EMAIL or SMS topic
3. Kafka consumer → calls Mailjet/Twilio API
4. Result persisted via `CommonPersistanceService`
5. Retry: 3 attempts with 1s delay on failure

OTP flow: generate 5-digit OTP → BCrypt hash → store in Redis with TTL → deliver via notification service → validate via BCrypt compare → delete on success.

## Kafka Topics

- `EMAIL` — email notification events
- `SMS` — SMS notification events

## Database Migrations

Flyway migrations in `src/main/resources/db/migration/`:
- `V1` — create notifications table
- `V2` — change notification_id to VARCHAR(255)
- `V3` — drop user_id column

## Infrastructure Ports

| Service       | Port  |
|---------------|-------|
| App           | 9500  |
| Kafka         | 9092  |
| Redis         | 6379  |
| Elasticsearch | 9200  |
| Kibana        | 5601  |
| APM Server    | 8200  |

## Notes

- No automated tests exist yet (`src/test` is empty)
- Database connection string in `application.yml` should be moved to an environment variable
- APM agent attach is commented out in `HeraldApplication.java`
