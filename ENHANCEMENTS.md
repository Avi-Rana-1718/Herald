# Herald — Enhancements

Backlog of feature ideas and improvements for the Herald notification microservice, grouped by theme and ranked by impact.

---

## Core Notification Features

- **Push notifications** (FCM / APNs) — third channel beside email and SMS. Add `PushProvider` interface + `FirebaseImpl`.
- **Templates** — Handlebars or Thymeleaf rendering. Store templates in DB with versioning. Clients send `templateId + variables` instead of raw content.
- **Scheduled notifications** — `sendAt` timestamp on request. Quartz scheduler or DB poller picks up due rows.
- **Bulk send** endpoint — accept large recipient lists, chunk to Kafka with backpressure.
- **Email attachments** — upload to S3, send signed URL to Mailjet.

## Delivery Guarantees

- **Outbox pattern** — atomic DB write + event publish. Eliminates dual-write problem. Relay via `@Scheduled` poller or Debezium CDC.
- **Dead-letter topics** — `EMAIL.DLT` / `SMS.DLT` for events that exhausted retries. Replay endpoint for manual recovery.
- **Idempotency keys** — `Idempotency-Key` header on POST `/notification`. Dedupe via Redis (24h TTL).
- **Delivery webhooks** — Mailjet / Twilio callback endpoint updates status (`DELIVERED`, `BOUNCED`, `OPENED`, `FAILED`).

## User-Facing

- **Preferences service** — `/preferences/{userId}` endpoint. Per-channel and per-category opt-out. Enforced before send.
- **Unsubscribe links** — token-based one-click unsubscribe in email footer. Auto-update preferences.
- **Quiet hours** — suppress SMS between 10pm–7am in recipient local time.
- **Priority queues** — `HIGH` / `NORMAL` / `LOW` → separate Kafka topics, HIGH processed first.

## OTP Upgrades

- **Configurable length** — 4–8 digit OTPs per requester.
- **Rate limiting** — max 3 OTPs per phone/email per 10min. Bucket4j backed by Redis.
- **Voice fallback** — Twilio voice call if SMS fails.
- **TOTP support** — Google Authenticator compatible as an alternative flavor.

## Observability / Ops

- **Admin dashboard** — React SPA. Live feed, per-provider success rate, recent failures.
- **Audit log** — separate read-only table. Who sent what, when.
- **Cost tracking** — count sends per client for charge-back metrics.
- **Provider failover** — circuit breaker (Resilience4j). Mailjet down → fallback to SendGrid.
- **Prometheus metrics** — Micrometer + `/actuator/prometheus`. Counters per channel, status, provider.
- **Structured logging** — correlation ID (requestId) in MDC across producer + consumer threads.

## Security / Multi-Tenancy

- **API keys + tenants** — each client has a key with scoped rate limits and quotas.
- **Per-tenant sender identity** — tenant-specific "from" email / phone number.
- **Content scanning** — regex for PII leaks before send. Flag or block.
- **Encryption at rest** — PII fields (recipient address) encrypted via JPA `@Convert`.
- **Auth filter** — API key header or JWT. Currently zero auth.

## Developer Experience

- **Sandbox / dry-run mode** — `dryRun=true` flag. Skip provider call, persist with `SIMULATED` status.
- **CLI tool** — `herald-cli send --to x --msg y`. Wraps REST API.
- **OpenAPI / Swagger** — springdoc-openapi-starter-webmvc-ui. Auto-generated spec + Postman collection.
- **Contract tests** — Spring Cloud Contract or Pact for downstream consumers.
- **Dockerfile for app** — multi-stage build. Add to `docker-compose.yml` alongside infra.
- **GitHub Actions CI** — build, test, Jacoco coverage gate, SpotBugs.
- **.env.example** — list all required env vars.

---

## Recommended Portfolio Slice (SDE1)

Maximum learning with minimum scope. Build these five in order:

1. **Templates + variables** — DB design, rendering, versioning.
2. **Idempotency keys** — distributed-systems thinking, Redis TTL.
3. **Rate limiting** — Redis + Bucket4j, abuse prevention.
4. **Delivery webhooks** — async callbacks, status state machine.
5. **OpenAPI + Dockerfile + CI** — ops maturity.

Together these demonstrate backend fundamentals, distributed reliability patterns, and production readiness.

---

## Known Bugs / Technical Debt

- `SMSUtil` throws NPE on unknown provider — `MailUtil` correctly throws `BadRequestException`. Match the pattern.
- DB connection string hardcoded in `application.yml` — move to env var.
- APM agent attach commented out in `HeraldApplication` — enable or remove the dead code.
- Rename `CommonPersistanceService` → `NotificationPersistenceService` (typo + vague).
- Extract Kafka retry count + backoff from magic numbers in `KafkaConfig` to `@ConfigurationProperties`.
- Replace remaining `@Autowired` field injection with constructor injection.
- Add DB index on `notification_id` (query column) via Flyway `V4`.
