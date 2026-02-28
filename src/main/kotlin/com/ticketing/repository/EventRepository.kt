package com.ticketing.repository

import com.ticketing.entity.Event
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface EventRepository : JpaRepository<Event, Long> {

    /**
     * Loads an event and immediately acquires a PESSIMISTIC_WRITE (SELECT … FOR UPDATE)
     * row-level lock.  Any concurrent transaction that calls this method for the same
     * [id] will block until the first transaction commits or rolls back, preventing
     * double-spending of the last remaining ticket.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    fun findByIdWithLock(@Param("id") id: Long): Optional<Event>
}
