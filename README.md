# Shipment Tracking API

Spring Boot API for tracking shipment events and current status.

## Tech Stack
- Java 17+
- Spring Boot 2.7+
- Spring Security
- PostgreSQL
- Maven

## Deliverables Included
- `API_DESIGN.md`
- `ARCHITECTURE.md`
- `db/schema.sql`
- `src/main/java`
- `src/test/java`
- `pom.xml`

## Prerequisites
- Java 17
- Maven 3.8+
- PostgreSQL 12+

## Database Setup
1. Create database:
   ```sql
   CREATE DATABASE shipment_tracking;
   ```
2. Update credentials in `src/main/resources/application.yml` if needed.
3. Apply schema manually:
   ```bash
   psql -U postgres -d shipment_tracking -f db/schema.sql
   ```

## Run
```bash
mvn spring-boot:run
```

## Test
```bash
mvn test
```

## JWT Requirement
All `/api/**` endpoints require a bearer token with:
- `sub` claim (user id)
- `companyId` claim (tenant id)

Example header:
```http
Authorization: Bearer <token>
```

Quick reminder: the token must carry `companyId`, otherwise request auth fails.

## API Examples

Create event:
```bash
curl -X POST http://localhost:8080/api/v1/shipments/SHP-123/events \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "eventType":"IN_TRANSIT",
    "timestamp":"2026-04-17T14:30:00Z",
    "location":{"latitude":40.7128,"longitude":-74.0060,"address":"New York, NY"},
    "metadata":{"carrier":"FastFreight"}
  }'
```

Get status:
```bash
curl http://localhost:8080/api/v1/shipments/SHP-123/status \
  -H "Authorization: Bearer <token>"
```

Register webhook:
```bash
curl -X POST http://localhost:8080/api/v1/webhooks \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"targetUrl":"https://example.com/webhook","secret":"abc"}'
```
