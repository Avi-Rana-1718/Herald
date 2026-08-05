# Herald — Bruno API Collection

Bruno collection covering every REST endpoint Herald exposes.

## Usage

1. Install [Bruno](https://www.usebruno.com/).
2. **Open Collection** → select this `apis/` folder.
3. Pick the **local** environment (top-right).
4. Start the app (`./mvnw spring-boot:run`) — it listens on port **6069**
   (`server.port` in `application.yml`).

CLI:

```bash
npm install -g @usebruno/cli
cd apis
bru run --env local
```

## Environment variables

| Var             | Purpose                                                     |
|-----------------|-------------------------------------------------------------|
| `baseUrl`       | `http://localhost:6069`                                     |
| `requestId`     | auto-set by any send/trigger request; read by status lookup |
| `otpRequestId`  | auto-set by **Request OTP**; read by **Validate OTP**       |
| `templateId`    | auto-set by the template creation requests                  |
| `uuid`          | in-app recipient id                                          |
| `toEmail`, `toName`, `toMobile` | recipient defaults — change to your own    |

## Endpoints

| Folder       | Request                    | Method | Path                       |
|--------------|----------------------------|--------|----------------------------|
| Notification | Send Email                 | POST   | `/notification/email`      |
| Notification | Send SMS                   | POST   | `/notification/sms`        |
| Notification | Send In-App                | POST   | `/notification/inapp`      |
| Notification | Get Inbox                  | GET    | `/notification/inapp?uuid=`|
| Notification | Get Notification Status    | GET    | `/notification?requestId=` |
| OTP          | Request OTP                | POST   | `/otp/request`             |
| OTP          | Request OTP - Email only   | POST   | `/otp/request`             |
| OTP          | Request OTP - SMS only     | POST   | `/otp/request`             |
| OTP          | Validate OTP               | POST   | `/otp/validate`            |
| Template     | Create Email Template      | POST   | `/template/create/email`   |
| Template     | Create SMS Template        | POST   | `/template/create/sms`     |
| Template     | Trigger Email Template     | POST   | `/template/trigger/email`  |
| Template     | Trigger SMS Template       | POST   | `/template/trigger/sms`    |

## Suggested flow

1. **Send Email** → captures `requestId`
2. **Get Notification Status** → `null` until the consumer processes it, then
   `REQUESTED` (success) or `FAILED`
3. **Create Email Template** → **Trigger Email Template**
4. **Request OTP** → paste received code into **Validate OTP**

Flow-by-flow write-ups live in [../docs/](../docs/).

## Gotcha: `{{ }}` collision

Herald templates use `{{placeholder}}`, the same syntax Bruno uses for its own
variables. Bruno leaves unresolved placeholders untouched, so template bodies
work as-is — but do not create a Bruno env var whose name matches a template
placeholder, or Bruno will substitute it before the request is sent.
