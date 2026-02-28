# Event Ticketing — Frontend

Vue 3 + Vite + TypeScript + Tailwind CSS dashboard for the Event Ticketing system.

## Running

```bash
npm install
npm run dev
# App opens at http://localhost:5173
# Backend must be running on http://localhost:8080
```

Vite proxies all `/api/*` requests to `http://localhost:8080`, so no CORS configuration is needed on the Spring Boot side.

---

## Overall structure

```
src/
├── main.ts              ← entry point, mounts the app
├── style.css            ← Tailwind directives
├── api/ticketing.ts     ← all HTTP calls
├── App.vue              ← root component, owns all state
└── components/
    ├── EventCard.vue    ← displays one event
    └── PurchaseModal.vue ← buy-ticket form
```

---

## `src/api/ticketing.ts` — HTTP layer

Two TypeScript interfaces define the data shapes (`Event`, `Ticket`). An Axios instance is created with `baseURL: '/api'` — all requests go to `/api/...` which Vite proxies to `localhost:8080`. Two exported functions cover the full API surface:

- `getEvents()` → `GET /api/events`
- `purchaseTicket(eventId, customerName)` → `POST /api/tickets/purchase`

---

## `App.vue` — root / state owner

This is the brains of the app. It holds three reactive refs:

| Ref | Type | Purpose |
|-----|------|---------|
| `events` | `Event[]` | the full list fetched from the backend |
| `selectedEvent` | `Event \| null` | non-null means the modal is open |
| `fetchError` | `string \| null` | shown as a banner if the API call fails |

On mount (`onMounted`), it calls `loadEvents()`. The template has three branches via `v-if / v-else-if / v-else`:
1. Error banner if the fetch failed
2. "Loading events…" if the array is still empty
3. A responsive CSS grid (`1 → 2 → 3 columns`) rendering an `EventCard` for each event

When a card emits `buy`, `App` sets `selectedEvent` to that event, which causes `v-if="selectedEvent"` to mount the modal. When the modal emits `purchased`, `App` re-fetches the event list so ticket counts update.

---

## `EventCard.vue` — displays one event

Receives a single `event` prop (typed as `Event`). Emits a `buy` event with no payload — the parent decides what to do with it. The card shows:

- Title + optional **Sold Out** badge (`v-if="totalTickets === 0"`)
- Date formatted via `Intl.DateTimeFormat` (e.g. "1 March 2025")
- Price formatted via `Intl.NumberFormat` as euros (e.g. "€29.99")
- Tickets remaining count
- A **Buy Ticket** button that becomes gray and disabled when `totalTickets === 0`

---

## `PurchaseModal.vue` — the buy form

Receives the selected `event` as a prop. Has its own local state:

| Ref | Purpose |
|-----|---------|
| `customerName` | bound to the text input via `v-model` |
| `loading` | true while the POST is in-flight; disables the button and input |
| `error` | API error message shown inline (e.g. "No tickets available") |
| `success` | true after a successful purchase |

**Submit flow (`onSubmit`):**
1. Sets `loading = true`, clears any previous error
2. Calls `purchaseTicket(event.id, customerName)`
3. **Success:** sets `success = true` → green message appears, then after 2 seconds emits `purchased` (App reloads events) and `close` (App clears `selectedEvent`, unmounting the modal)
4. **Error:** reads `err.response.data.message` from the backend (e.g. the 409 "No tickets available" message) and shows it in a red box

The backdrop (`@click.self`) closes the modal unless a request is in flight, preventing accidental dismissal mid-purchase.
