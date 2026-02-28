package com.ticketing.controller

import com.ticketing.dto.PurchaseRequest
import com.ticketing.entity.Event
import com.ticketing.entity.Ticket
import com.ticketing.repository.EventRepository
import com.ticketing.service.TicketService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class EventController(
    private val eventRepository: EventRepository,
    private val ticketService: TicketService
) {

    /** GET /api/events — returns all events */
    @GetMapping("/events")
    fun getAllEvents(): List<Event> = eventRepository.findAll()

    /**
     * POST /api/tickets/purchase — purchases one ticket for an event.
     *
     * Request body:
     * ```json
     * { "eventId": 1, "customerName": "Jane Doe" }
     * ```
     *
     * Returns 201 Created with the new [Ticket] on success.
     * Returns 404 if the event does not exist.
     * Returns 409 if no tickets remain.
     */
    @PostMapping("/tickets/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    fun purchaseTicket(@RequestBody request: PurchaseRequest): Ticket =
        ticketService.purchaseTicket(request.eventId, request.customerName)
}
