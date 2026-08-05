# Known Issues

Everything below was found by reading the current code, not by running it. Each
entry states the mechanism so it can be confirmed or dismissed quickly. Ordered
roughly by severity.

---

## Credentials committed in `application.yml`

**Severity: critical.** `src/main/resources/application.yml` contains a live
Neon Postgres URL with `user=` and `password=` inline, and an Aiven Kafka
bootstrap host. Both are in git history.

```yaml
url: jdbc:postgresql://ep-...neon.tech/neondb?user=neondb_owner&password=npg_...
bootstrap-servers: kafka-...aivencloud.com:21507
```

Vendor credentials are already externalised (`${MAILJET_APIKEY}` etc.); the
datasource and broker were not. Rotate the Neon password, move both to
environment variables. Note that rotation is the fix — removing the line from
HEAD leaves the secret in history.

---

## `FAILED_REFERENCE` collides with a unique constraint

**Severity: high.** `notifications.reference_id` is `UNIQUE` (V1). On failure
every consumer writes the same literal:

```java
private final String FAILED_REFERENCE = "FAILED_REFERENCE";
```

The first failed notification takes that value. **Every subsequent failure, for
any channel, violates the unique constraint** — so the second failure cannot
record its status at all. The insert throws inside the catch block, replacing
the original exception, and the row is never written. The caller polling
`GET /notification` gets `data: null` forever.

Fix: make the failure marker unique per request (e.g. `"FAILED-" + requestId`),
or drop the uniqueness requirement for failure rows.

---

## In-app inbox insert precedes its FK parent

**Severity: high.** In `InAppConsumer`, `inAppProvider.sendNotification()` runs
*before* `saveOrUpdateNotification()`. The provider inserts into
`in_app_notifications` with `notification_id = requestId`, but the parent
`notifications` row with that primary key does not exist yet, and
`saveInAppNotification` is `@Transactional` so it commits on its own.

The FK from V7 is not `DEFERRABLE`, so the insert should fail immediately on the
first delivery attempt.

The interesting part is what follows: the catch block writes the `notifications`
row with status `FAILED` — which creates the missing parent. On retry attempt 2
the inbox insert then succeeds. So the flow likely self-heals at the cost of one
wasted attempt, a spurious `FAILED` write, and `retry_count = 1` on every in-app
notification.

Combined with the `FAILED_REFERENCE` issue above, the parent-creating write is
itself blocked once any other notification has already failed — in which case
in-app delivery never recovers.

Fix: persist the `notifications` row before invoking the provider, or drop the
FK.

---

## Kafka publish result is discarded

**Severity: high.**

```java
public void sendMessage(String topic, Object message) {
    kafkaTemplate.send(topic, message);   // future dropped
}
```

Broker unreachable, topic missing with auto-create disabled, serialization
failure — none of it is observed. The caller has already been told `201` with a
`requestId` that will never resolve. Attach a `whenComplete` callback and at
minimum log; ideally fail the request.

---

## Terminal delivery failures are dropped

**Severity: high.** No DLT, no `DeadLetterPublishingRecoverer`. After 4 attempts
`DefaultErrorHandler` logs and advances the offset. The only trace is a
`notifications` row at `FAILED` with `retry_count = 3`, and nothing scans for
those. See [error-handling.md](error-handling.md).

---

## No authentication on any endpoint

**Severity: high.** No Spring Security dependency, no filter, no API key check.
Anyone who can reach port 6069 can send mail from the configured Mailjet
identity, send SMS on the configured Twilio service, issue OTPs, and read any
user's in-app inbox by guessing a uuid.

---

## OTP has no attempt limiting

**Severity: high.** `validateOtp` returns 401 and leaves the Redis key intact.
A 5-digit numeric code (100,000 values) with no lockout, no attempt counter, and
no rate limiting anywhere in the service is brute-forceable within the TTL.
`BCrypt.gensalt(5)` slows an attacker down per guess but is well below the
default work factor of 10.

Add a per-`otpId` attempt counter in Redis and delete the key after N failures.

---

## `spring.kafka.listener.*` properties are silently ignored

**Severity: medium.** `KafkaConfig` builds the factory with
`new ConcurrentKafkaListenerContainerFactory<>()` and never applies Boot's
`ConcurrentKafkaListenerContainerFactoryConfigurer`. So this in
`application.yml` has no effect:

```yaml
listener:
  ack-mode: MANUAL_IMMEDIATE
```

Containers run with the default `BATCH` ack mode. (This is arguably fortunate —
`MANUAL_IMMEDIATE` requires listeners to accept an `Acknowledgment` parameter,
which none of them do.) Either delete the property or apply the configurer and
add the parameter.

---

## The inbox GET mutates state

**Severity: medium.** `GET /notification/inapp` marks every returned row
`is_read = true` inside the same transaction. A `GET` with side effects is not
cacheable, not safely retryable, and not idempotent. A client that drops the
response has permanently lost those notifications — there is no history read and
no separate mark-as-read endpoint.

---

## `langCode` casing is validated loosely, matched strictly

**Severity: medium.** `LangCode.validate` uses `equalsIgnoreCase`, but the value
is stored verbatim and looked up with an exact-match derived query. Creating
with `EN` and triggering with `en` yields `404 no template found`, and the
uniqueness constraint treats them as distinct rows. Normalise to lowercase
before persisting.

---

## No timeouts on outbound HTTP

**Severity: medium.** Neither `mailjetClient` nor `twilioClient` sets connect or
read timeouts. An unresponsive vendor blocks the consumer thread indefinitely;
with default concurrency of 1, that stalls the partition. Set both via a
`ClientHttpRequestFactory`.

---

## Permanent errors consume the full retry budget

**Severity: low.** `catch (Exception e)` treats a malformed recipient the same
as a network blip: 4 attempts, ~4 seconds. Use
`DefaultErrorHandler.addNotRetryableExceptions` for client-side 4xx.

---

## Validation is inconsistent across channels

**Severity: low.** In-app uses `@NotBlank` on all fields. Email and SMS DTOs
carry no annotations at all and rely on a single hand-rolled null check, so
`""` passes and fails downstream at the vendor. Nothing validates email or
phone-number format.

---

## `SMSUtil` skips the null-provider check

**Severity: low.** `MailUtil` throws `BadRequestException` for an unmapped
provider; `SMSUtil` dereferences directly and would NPE. Unreachable today
(`TWILIO` is the only constant), but it is a trap for the next provider added.

---

## Schema/enum drift

**Severity: low.**

- `notification_type` includes `WHATSAPP`, absent from `NotifTypeEnum` — reading
  such a row fails to deserialize.
- `NotificationStatusEnum.QUEUED` is never written by application code.
- `notifications.trigger_date` exists but is unmapped on `NotificationEntity`,
  so the API exposes no timing information.
- `templates.version` / `is_active` are written once and never mutated; no
  endpoint implements the lifecycle they imply.

---

## Migrations V2 and V6 are destructive

**Severity: low** (already applied). V2 runs `TRUNCATE TABLE notifications`; V6
adds a `NOT NULL` column with no default, which fails outright against a
populated table. Worth knowing before replaying migrations onto a restored dump.

---

## No tests

**Severity: medium.** `src/test` is empty. Every behaviour on these pages —
retry counting, upsert semantics, the render contract, OTP expiry — is
unverified.

---

## Generic 500 handler leaks exception messages

**Severity: low.** The catch-all returns `ex.getMessage()` verbatim in the
response body. Driver and constraint exceptions can carry schema details or
connection information. Log the detail, return a generic message.
