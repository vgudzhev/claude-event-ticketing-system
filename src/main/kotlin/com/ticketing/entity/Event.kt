package com.ticketing.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Represents a ticketed event.
 *
 * [totalTickets] is mutable — it is decremented by one each time a ticket is
 * purchased. All writes go through TicketService#purchaseTicket which holds a
 * PESSIMISTIC_WRITE lock on this row, so the decrement is always thread-safe.
 */
@Entity
@Table(name = "events")
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String = "",

    // "date" is a reserved word in some dialects; use an explicit column name
    @Column(name = "event_date", nullable = false)
    val date: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var totalTickets: Int = 0,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal = BigDecimal.ZERO
)
