package com.ticketing.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Represents a purchased ticket for an event.
 *
 * [eventId] is a plain FK value rather than a JPA association so the entity
 * stays simple and the purchase query requires no extra join.
 */
@Entity
@Table(name = "tickets")
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val eventId: Long = 0,

    @Column(nullable = false)
    val customerName: String = "",

    @Column(nullable = false)
    val purchaseDate: LocalDateTime = LocalDateTime.now()
)
