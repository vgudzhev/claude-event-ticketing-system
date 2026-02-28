package com.ticketing

import com.ticketing.entity.Event
import com.ticketing.entity.Ticket
import com.ticketing.exception.EventNotFoundException
import com.ticketing.exception.NoTicketsAvailableException
import com.ticketing.repository.EventRepository
import com.ticketing.repository.TicketRepository
import com.ticketing.service.TicketService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional

class TicketServiceTest {

    private val eventRepository: EventRepository = mock()
    private val ticketRepository: TicketRepository = mock()
    private val service = TicketService(eventRepository, ticketRepository)

    private fun sampleEvent(tickets: Int) = Event(
        id = 1L,
        title = "Test Event",
        date = LocalDateTime.now().plusDays(30),
        totalTickets = tickets,
        price = BigDecimal("25.00")
    )

    @Test
    fun `purchaseTicket saves ticket and decrements count`() {
        val event = sampleEvent(tickets = 5)
        whenever(eventRepository.findByIdWithLock(1L)).thenReturn(Optional.of(event))
        whenever(ticketRepository.save(any())).thenAnswer { it.arguments[0] as Ticket }

        val ticket = service.purchaseTicket(eventId = 1L, customerName = "Alice")

        assertEquals(1L, ticket.eventId)
        assertEquals("Alice", ticket.customerName)
        assertEquals(4, event.totalTickets)          // decremented
        verify(eventRepository).save(event)
        verify(ticketRepository).save(any())
    }

    @Test
    fun `purchaseTicket throws when event not found`() {
        whenever(eventRepository.findByIdWithLock(99L)).thenReturn(Optional.empty())

        assertThrows<EventNotFoundException> {
            service.purchaseTicket(eventId = 99L, customerName = "Bob")
        }
    }

    @Test
    fun `purchaseTicket throws when no tickets remain`() {
        val event = sampleEvent(tickets = 0)
        whenever(eventRepository.findByIdWithLock(1L)).thenReturn(Optional.of(event))

        assertThrows<NoTicketsAvailableException> {
            service.purchaseTicket(eventId = 1L, customerName = "Carol")
        }
        verify(ticketRepository, never()).save(any())
    }
}
