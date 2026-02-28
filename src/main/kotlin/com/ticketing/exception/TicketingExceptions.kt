package com.ticketing.exception

class EventNotFoundException(id: Long) :
    RuntimeException("Event not found: id=$id")

class NoTicketsAvailableException(id: Long) :
    RuntimeException("No tickets available for event id=$id")
