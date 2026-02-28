package com.ticketing.repository

import com.ticketing.entity.Ticket
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository : JpaRepository<Ticket, Long>
