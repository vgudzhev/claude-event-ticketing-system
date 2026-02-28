# Event Ticketing — Backend

Kotlin + Spring Boot 3 REST API backed by an H2 file database.

## Running

```bash
./gradlew bootRun
# API available at http://localhost:8080
# H2 console at  http://localhost:8080/h2-console
```

---

## Source structure

```
src/main/kotlin/com/ticketing/
├── config/
│   └── DataSeeder.kt          ← seeds sample events on first startup
├── controller/
│   ├── EventController.kt     ← REST endpoints
│   └── GlobalExceptionHandler.kt ← maps exceptions to HTTP responses
├── dto/
│   └── PurchaseRequest.kt     ← request body for ticket purchase
├── entity/
│   ├── Event.kt               ← events table
│   └── Ticket.kt              ← tickets table
├── exception/
│   └── TicketingExceptions.kt ← domain exceptions
├── repository/
│   ├── EventRepository.kt     ← JPA repo with pessimistic lock query
│   └── TicketRepository.kt    ← JPA repo for tickets
└── service/
    └── TicketService.kt       ← purchase logic with transaction + lock
```

---

## Database

H2 is used in **file mode** so data persists between restarts in `ticketing-db.mv.db` at the project root.

```properties
spring.datasource.url=jdbc:h2:file:./ticketing-db
spring.jpa.hibernate.ddl-auto=update
```

`ddl-auto=update` means Hibernate creates or alters tables automatically at startup — no migration scripts needed. The H2 web console is enabled at `/h2-console` for ad-hoc inspection.

### Tables

**`events`**

| Column | Type | Notes |
|--------|------|-------|
| `id` | BIGINT (PK, auto) | |
| `title` | VARCHAR | |
| `event_date` | TIMESTAMP | named `event_date` to avoid SQL reserved word `date` |
| `total_tickets` | INT | decremented on each purchase |
| `price` | DECIMAL(10,2) | |

**`tickets`**

| Column | Type | Notes |
|--------|------|-------|
| `id` | BIGINT (PK, auto) | |
| `event_id` | BIGINT | plain FK value, no JPA association |
| `customer_name` | VARCHAR | |
| `purchase_date` | TIMESTAMP | |

### Seed data

`DataSeeder` runs on startup and inserts three events if the table is empty — making it idempotent across restarts:

| Title | Tickets | Price |
|-------|---------|-------|
| Spring Boot Conference 2025 | 100 | €49.99 |
| Kotlin Summit | 50 | €79.99 |
| Cloud Native Day | 2 | €29.99 |

---

## REST API

Base path: `/api`

### `GET /api/events`

Returns all events.

**Response `200 OK`**
```json
[
  {
    "id": 1,
    "title": "Spring Boot Conference 2025",
    "date": "2025-06-15T09:00:00",
    "totalTickets": 100,
    "price": 49.99
  }
]
```

---

### `POST /api/tickets/purchase`

Purchases one ticket for an event.

**Request body**
```json
{ "eventId": 1, "customerName": "Jane Doe" }
```

**Response `201 Created`**
```json
{
  "id": 42,
  "eventId": 1,
  "customerName": "Jane Doe",
  "purchaseDate": "2025-06-01T14:23:00"
}
```

**Error responses**

| Status | When | Body |
|--------|------|------|
| `404 Not Found` | event ID does not exist | `{ "status": 404, "message": "Event not found: id=99" }` |
| `409 Conflict` | no tickets remaining | `{ "status": 409, "message": "No tickets available for event id=3" }` |

---

## Key layers

### `EventController`

Thin REST layer — maps HTTP verbs/paths to either a direct repository call (`findAll`) or the service layer. Annotated with `@RestController` and `@RequestMapping("/api")`.

### `TicketService`

Contains the only piece of real business logic: purchasing a ticket atomically.

1. `@Transactional` wraps the whole method in a single DB transaction.
2. `eventRepository.findByIdWithLock()` issues `SELECT … FOR UPDATE`, acquiring a **pessimistic write lock** on the event row. Any concurrent request for the same event blocks here until the first transaction commits.
3. If `totalTickets` is already 0, throws `NoTicketsAvailableException` (rolls back cleanly).
4. Otherwise decrements `totalTickets`, saves the event, creates and saves the `Ticket`, then commits — releasing the lock.

This ensures two simultaneous purchases of the last ticket can never both succeed (no overselling).

### `GlobalExceptionHandler`

`@RestControllerAdvice` that intercepts domain exceptions thrown anywhere in the call stack and converts them to structured JSON error responses with the correct HTTP status code, so the frontend always gets `{ status, message }` on errors.

### `EventRepository`

Extends `JpaRepository<Event, Long>` and adds one custom query: `findByIdWithLock`, annotated with `@Lock(LockModeType.PESSIMISTIC_WRITE)`. Spring Data JPA translates this into `SELECT … FOR UPDATE` at the SQL level.

### `TicketRepository`

Plain `JpaRepository<Ticket, Long>` — no custom queries needed; only `save()` is used.
