package com.ticketing.config

import com.ticketing.entity.Event
import com.ticketing.repository.EventRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Seeds a handful of sample events on first startup.
 * The count-check makes it idempotent — re-running the app never creates duplicates.
 */
@Component
class DataSeeder(private val eventRepository: EventRepository) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (eventRepository.count() > 0) return

        eventRepository.saveAll(
            listOf(
                Event(
                    title = "Spring Boot Conference 2025",
                    date = LocalDateTime.of(2025, 6, 15, 9, 0),
                    totalTickets = 100,
                    price = BigDecimal("49.99")
                ),
                Event(
                    title = "Kotlin Summit",
                    date = LocalDateTime.of(2025, 7, 20, 10, 0),
                    totalTickets = 50,
                    price = BigDecimal("79.99")
                ),
                Event(
                    title = "Cloud Native Day",
                    date = LocalDateTime.of(2025, 8, 10, 9, 0),
                    totalTickets = 2,       // low stock — good for testing sold-out
                    price = BigDecimal("29.99")
                )
            )
        )
    }
}
