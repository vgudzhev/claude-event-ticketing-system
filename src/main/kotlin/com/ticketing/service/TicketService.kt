package com.ticketing.service

import com.ticketing.entity.Ticket
import com.ticketing.exception.EventNotFoundException
import com.ticketing.exception.NoTicketsAvailableException
import com.ticketing.repository.EventRepository
import com.ticketing.repository.TicketRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TicketService(
    private val eventRepository: EventRepository,
    private val ticketRepository: TicketRepository
) {

    /**
     * Atomically purchases one ticket for the given event.
     *
     * Thread-safety strategy:
     * 1. `@Transactional` wraps the entire method in a single DB transaction.
     * 2. `findByIdWithLock` issues `SELECT … FOR UPDATE`, acquiring a
     *    row-level PESSIMISTIC_WRITE lock on the event row.
     * 3. Any concurrent call for the same event blocks at step 2 until the
     *    first transaction commits, at which point the second thread reads the
     *    already-decremented [totalTickets] value — preventing overselling.
     *
     * @throws EventNotFoundException       if no event exists with [eventId]
     * @throws NoTicketsAvailableException  if [Event.totalTickets] is already 0
     */
    @Transactional
    fun purchaseTicket(eventId: Long, customerName: String): Ticket {
        val event = eventRepository.findByIdWithLock(eventId)
            .orElseThrow { EventNotFoundException(eventId) }

        if (event.totalTickets <= 0) {
            throw NoTicketsAvailableException(eventId)
        }

        event.totalTickets -= 1
        eventRepository.save(event)

        return ticketRepository.save(
            Ticket(
                eventId = eventId,
                customerName = customerName,
                purchaseDate = LocalDateTime.now()
            )
        )
    }
}
