# Visitor Management Backend

Production-ready visitor flow: WhatsApp webhook (Meta Cloud), visit create/approve/reject, QR generation, security scan toggle, filtering/paging, PII retention.

## Prerequisites
- Java 17+
- Maven
- MySQL running (default url in `application.properties`)
- ngrok for local testing of webhook

## Run locally
1. Start backend on 8080
   - `mvn spring-boot:run`
2. Start ngrok
   - `ngrok http 8080`
   - Note public URL: `https://<ngrok-id>.ngrok.io`
3. Configure application properties (see below) and restart if needed.

## Configuration (application.properties)
```
# DB
spring.datasource.url=jdbc:mysql://localhost:3307/Visitor
spring.datasource.username=root
spring.datasource.password=

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# WhatsApp / Meta Cloud
whatsapp.base-url=https://graph.facebook.com/v20.0/
whatsapp.phone-number-id=<your_phone_number_id>
whatsapp.token=<your_bearer_token>
whatsapp.app-secret=<verify_token_and_app_secret>
whatsapp.enabled=false               # true for real API calls
whatsapp.public-base-url=https://<ngrok-id>.ngrok.io

# Data retention (days)
retention.days=90
```

## Endpoints overview
- Webhook (Meta Cloud)
  - GET `/webhook/whatsapp` (verification)
  - POST `/webhook/whatsapp` (incoming events)
- Tenant decision
  - POST `/tenant/visit/approve/{visitId}`
  - POST `/tenant/visit/reject/{visitId}`
- Visits API
  - POST `/api/visits`
  - GET `/api/visits/{id}`
  - GET `/api/visits?tenantId=&status=&from=&to=&page=&size=` (paged + filter)
- Security
  - POST `/api/security/scan/{visitId}` (JWT SECURITY user)
  - GET `/api/security/active-visits`
- Static QR images
  - `/qrcodes/visit_{id}.png`

## Webhook verification (Meta UI)
- Set callback URL to: `https://<ngrok-id>.ngrok.io/webhook/whatsapp`
- Verify token must match `whatsapp.app-secret`.
- Subscribe to messages.

## Local testing with curl
Note: Signature verification is enforced. Compute X-Hub-Signature-256 or temporarily adjust the check for rapid local tests.

- Verify webhook
```
curl -G "http://localhost:8080/webhook/whatsapp" \
  --data-urlencode "hub.mode=subscribe" \
  --data-urlencode "hub.verify_token=<verify_token>" \
  --data-urlencode "hub.challenge=1234"
```

- Visitor message (creates PENDING visit and notifies tenant)
```
curl -X POST "http://localhost:8080/webhook/whatsapp" \
  -H "Content-Type: application/json" \
  -H "X-Hub-Signature-256: sha256=<valid_or_dummy>" \
  -d '{
  "entry": [{
    "changes": [{
      "value": {
        "messages": [{
          "from": "919999999999",
          "text": { "body": "John Doe\n919111111111\n2025-11-23T17:30:00\n919222222222" }
        }]
      }
    }]
  }]
}'
```
- Tenant Approve button (replace 10 with actual visit id)
```
curl -X POST "http://localhost:8080/webhook/whatsapp" \
  -H "Content-Type: application/json" \
  -H "X-Hub-Signature-256: sha256=<valid_or_dummy>" \
  -d '{
  "entry": [{
    "changes": [{
      "value": {
        "messages": [{
          "from": "919222222222",
          "interactive": { "button_reply": { "id": "APP_10", "title": "Approve" } }
        }]
      }
    }]
  }]
}'
```
- Tenant Reject button
```
curl -X POST "http://localhost:8080/webhook/whatsapp" \
  -H "Content-Type: application/json" \
  -H "X-Hub-Signature-256: sha256=<valid_or_dummy>" \
  -d '{
  "entry": [{
    "changes": [{
      "value": {
        "messages": [{
          "from": "919222222222",
          "interactive": { "button_reply": { "id": "REJ_10", "title": "Reject" } }
        }]
      }
    }]
  }]
}'
```

## PII masking policy
- Visitor phone numbers are masked in API responses after the visit expires (shows only last two digits).
- Consider rotating logs and avoiding logging full PII in custom logs.

## Data retention job
- Scheduled daily at 02:15 AM.
- Deletes visits older than `retention.days` by `createdAt`.
- Logs number of rows deleted.

## Paging and filtering
- `/api/visits` supports:
  - `tenantId`, `status`, `from`, `to` (ISO datetime)
  - `page`, `size`, `sort` (Spring Pageable)
- Example:
```
GET /api/visits?tenantId=5&status=APPROVED&from=2025-01-01T00:00:00&to=2025-12-31T23:59:59&page=0&size=20&sort=visitDateTime,desc
```

## WhatsApp message parsing
- MVP parser expects multi-line text:
  1. Visitor name
  2. Visitor phone (optional; defaults to `from`)
  3. Visit datetime (ISO; defaults to now)
  4. Tenant phone (required)
- Sends interactive buttons to tenant: Approve (APP_{visitId}) and Reject (REJ_{visitId}).
- On approve, QR is generated at `qr-codes/visit_{id}.png` and sent to visitor using `whatsapp.public-base-url` to make an absolute URL.

## Postman
- Import the curl requests here or create a Postman collection with:
  - Webhook GET verify
  - Webhook POST text
  - Webhook POST interactive (APP_xxx / REJ_xxx)
  - Security scan POST
  - Visits list with filters

## Notes / Future work
- Parse full Meta webhook structure and support more message types.
- Move webhook processing to a proper queue.
- Repository-based masking for analytics exports, if needed.
- Add integration tests (MockMvc) and unit tests for parsing and service logic.
