# Flow: Status Lookup

`GET /notification?requestId=<id>` — the only read path over the `notifications`
table.

```mermaid
flowchart LR
    A[GET /notification?requestId] --> B[NotificationService.getNotification]
    B --> C["findByID — native:<br/>SELECT * FROM notifications<br/>WHERE notification_id = :requestId"]
    C --> D[200 { data: row-or-null, status: 200 }]
```

`requestId` is `@NotBlank`; omitting the parameter entirely yields `400` via the
`MissingServletRequestParameterException` handler.

## Response

```json
{
  "data": {
    "notificationId": "3f0c...-1754380800000",
    "referenceId": "1152921515173678610",
    "sentTo": "someone@example.com",
    "type": "EMAIL",
    "status": "REQUESTED",
    "retryCount": 0
  },
  "status": 200
}
```

## Reading the result

**`data: null` is normal, not an error.** The row is created by the Kafka
consumer, not by the REST call, so a lookup issued immediately after the 201
races the consumer. `findByID` returns `null` for a miss and the endpoint still
answers `200`. Null means "unknown or not yet processed" — indistinguishable
from a bad id.

**`status` interpretation:**

| Value | Meaning |
|-------|---------|
| `REQUESTED` | The provider accepted the message and returned a reference id. This is the terminal success state. |
| `FAILED` | The most recent attempt threw. If `retry_count` < 3 a retry may still be in flight and flip it to `REQUESTED`. |
| `QUEUED` | The DB default for the column. Never written by application code. |

There is no `SUCCESS` state — `NotificationStatusEnum` is
`{FAILED, REQUESTED, QUEUED}`. `REQUESTED` doubles as "handed off successfully".

**`referenceId`** is the vendor's id — Mailjet `MessageID`, Twilio `sid`, or the
generated UUID for in-app. On failure it is the literal string
`FAILED_REFERENCE`.

**`retryCount`** is `deliveryAttempt - 1`, so `0` on the first pass and `3` after
the retry budget is spent.

## Polling

Terminal states are not distinguishable from in-flight ones by the row alone. A
`FAILED` with `retry_count = 3` is final; anything else may still change. With a
1s backoff and 3 retries, everything settles within roughly 4 seconds of the
consumer picking the event up — poll for a few seconds, then treat the last
value as final.

## Limits

- Single-row lookup only — no listing, filtering by recipient, or date range.
- No `trigger_date` in the entity, though the column exists and defaults to
  `CURRENT_TIMESTAMP`, so the response carries no timing information.
- The OTP `otpId` is **not** a valid `requestId` here. `OtpService` throws away
  the request ids of the notifications it sends, so OTP deliveries cannot be
  traced through this endpoint.
