# API_DESIGN.md

Quick API contract for Shipment Tracking API (v1).

## Base
- Base URL: `http://localhost:8080`
- Auth: Bearer JWT for all `/api/**`
- Required JWT claims: `sub`, `companyId`

## Endpoints

1. `POST /api/v1/shipments/{shipmentId}/events`
   - Add shipment event
2. `GET /api/v1/shipments/{shipmentId}/events?page=0&size=50`
   - Retrieves all past events for a shipment with pagination
3. `GET /api/v1/shipments/{shipmentId}/status`
   - Get latest shipment status
4. `POST /api/v1/webhooks`
   - Register webhook
5. `DELETE /api/v1/webhooks/{webhookId}`
   - Disable webhook

## OpenAPI 3.0 (YAML)

```yaml
openapi: 3.0.3
info:
  title: Shipment Tracking API
  version: 1.0.0
servers:
  - url: http://localhost:8080
security:
  - bearerAuth: []
paths:
  /api/v1/shipments/{shipmentId}/events:
    post:
      responses:
        '201': { description: Created }
        '400': { $ref: '#/components/responses/BadRequest' }
        '401': { $ref: '#/components/responses/Unauthorized' }
        '429': { $ref: '#/components/responses/TooManyRequests' }
    get:
      responses:
        '200': { description: OK }
        '401': { $ref: '#/components/responses/Unauthorized' }
        '429': { $ref: '#/components/responses/TooManyRequests' }
  /api/v1/shipments/{shipmentId}/status:
    get:
      responses:
        '200': { description: OK }
        '401': { $ref: '#/components/responses/Unauthorized' }
        '404': { $ref: '#/components/responses/NotFound' }
        '429': { $ref: '#/components/responses/TooManyRequests' }
  /api/v1/webhooks:
    post:
      responses:
        '201': { description: Created }
        '400': { $ref: '#/components/responses/BadRequest' }
        '401': { $ref: '#/components/responses/Unauthorized' }
        '429': { $ref: '#/components/responses/TooManyRequests' }
  /api/v1/webhooks/{webhookId}:
    delete:
      responses:
        '204': { description: No Content }
        '401': { $ref: '#/components/responses/Unauthorized' }
        '404': { $ref: '#/components/responses/NotFound' }
        '429': { $ref: '#/components/responses/TooManyRequests' }
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  responses:
    BadRequest: { description: Bad request }
    Unauthorized: { description: Unauthorized }
    NotFound: { description: Not found }
    TooManyRequests: { description: Too many requests }
```

## Request/Response Samples

### POST /api/v1/shipments/SHP-12345/events
Request:
```json
{
  "eventType": "IN_TRANSIT",
  "timestamp": "2026-04-17T14:30:00Z",
  "location": {
    "latitude": 40.7128,
    "longitude": -74.0060,
    "address": "New York, NY"
  },
  "metadata": { "carrier": "FastFreight", "vehicle": "TRUCK-789" }
}
```
Response `201`:
```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "shipmentId": "SHP-12345",
  "eventType": "IN_TRANSIT",
  "timestamp": "2026-04-17T14:30:00Z"
}
```

### GET /api/v1/shipments/SHP-12345/status
Response `200`:
```json
{
  "shipmentId": "SHP-12345",
  "status": "DELIVERED",
  "latestLocation": "{\"address\":\"New York, NY\"}",
  "eta": "2026-04-17T16:00:00Z",
  "condition": "GOOD"
}
```

### GET /api/v1/shipments/SHP-12345/events?page=0&size=2
Response `200`:
```json
{
  "content": [
    {
      "eventId": "550e8400-e29b-41d4-a716-446655440000",
      "shipmentId": "SHP-12345",
      "eventType": "DELIVERED",
      "timestamp": "2026-04-17T16:00:00Z"
    },
    {
      "eventId": "d15fd131-f772-466f-9f5f-c17ac8a65b47",
      "shipmentId": "SHP-12345",
      "eventType": "IN_TRANSIT",
      "timestamp": "2026-04-17T14:30:00Z"
    }
  ],
  "number": 0,
  "size": 2,
  "totalElements": 5,
  "totalPages": 3
}
```

### POST /api/v1/webhooks
Request:
```json
{
  "targetUrl": "https://example.com/hooks/shipment",
  "secret": "webhook-secret"
}
```
Response `201`:
```json
{
  "webhookId": "f5c0d01a-f6df-4f84-8a9d-627b6e793f3c",
  "targetUrl": "https://example.com/hooks/shipment",
  "active": true
}
```

## Error Codes
- `400` bad payload/validation
- `401` bad or missing token / missing `companyId`
- `404` shipment/webhook not found
- `429` rate limit hit
- `500` server error

## Rate Limit
- `1000 requests/minute/company`
- key = `companyId`
- response = `429` when exceeded

## Validation Rules
- `eventType`: `PICKUP | IN_TRANSIT | DELIVERED`
- `timestamp`: ISO-8601
- `location.latitude`: `-90..90`
- `location.longitude`: `-180..180`
- `location.address`: required
- `targetUrl`: starts with `http://` or `https://`
- `metadata.eta` (if present): ISO-8601
