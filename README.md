# Event Ticketing System

A full-stack event ticketing application with a Kotlin/Spring Boot REST API and a Vue 3 frontend.

## Documentation

- [Backend — API, database, and architecture](./backend-readme.md)
- [Frontend — Vue 3 components and UI](./frontend-readme.md)

## Stack

- **Kotlin** + **Spring Boot 3.4**
- **Spring Data JPA** + **H2** (file-based, persists between restarts)
- **Gradle** (Kotlin DSL)

## Running

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`.
The H2 console is available at `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:file:./ticketing-db`, user: `sa`, password: empty).

## API

### Get all events

```
GET /api/events
```

```json
[
  { "id": 1, "title": "Spring Boot Conference 2025", "date": "2025-06-15T09:00:00", "totalTickets": 99, "price": 49.99 },
  { "id": 2, "title": "Kotlin Summit",               "date": "2025-07-20T10:00:00", "totalTickets": 50, "price": 79.99 },
  { "id": 3, "title": "Cloud Native Day",             "date": "2025-08-10T09:00:00", "totalTickets": 2,  "price": 29.99 }
]
```

### Purchase a ticket

```
POST /api/tickets/purchase
Content-Type: application/json

{ "eventId": 1, "customerName": "Jane Doe" }
```

**201 Created**
```json
{ "id": 1, "eventId": 1, "customerName": "Jane Doe", "purchaseDate": "2026-02-28T14:55:27" }
```

**404** — event not found
**409** — no tickets remaining

## Thread Safety

`TicketService.purchaseTicket` is annotated `@Transactional` and acquires a
`PESSIMISTIC_WRITE` (`SELECT … FOR UPDATE`) row-level lock on the event before
decrementing `totalTickets`. Concurrent purchase requests for the same event are
serialised at the database level, preventing overselling.

## Tests

```bash
./gradlew test
```
