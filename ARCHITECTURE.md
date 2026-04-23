# Architecture

Spring Boot app with a simple `controller -> service -> repository` flow.

## Main Parts
- `controller`: API endpoints and request validation
- `service`: event/status/webhook business logic
- `repository`: Spring Data JPA queries
- `security`: JWT filter + tenant extraction from `companyId`
- `config`: security chain, rate-limit interceptor, app beans

## Request Flow
1. Client sends request with `Authorization: Bearer <jwt>`.
2. `JwtAuthenticationFilter` validates JWT and sets `CurrentUser(companyId)`.
3. `RateLimitInterceptor` checks per-company minute limit.
4. Service layer runs tenant-scoped reads/writes.
5. Controller returns response.

## Tenant Isolation
- Tenant key is `companyId` from JWT.
- Tenant-owned tables use `company_id`.
- Service/repository queries include `company_id`.

## Data Model
- `shipment_events`: append-only shipment event log (`event_id` unique)
- `shipments`: latest shipment state (status, location, ETA, condition)
- `webhooks`: company webhook registrations
- `webhook_delivery_logs`: webhook success/failure logs
- `api_rate_limits`: per-company per-minute counters

## Scale Notes
- Main event history index: `(company_id, shipment_id, event_timestamp DESC)`.
- Rate-limit uniqueness: `(company_id, window_start)`.
- Event location/metadata stored as TEXT (JSON string).

## Known Limits (v1)
- Webhook dispatch is async but basic (no retry queue).
- Rate limit is DB-backed; very high traffic can make this table hot.
