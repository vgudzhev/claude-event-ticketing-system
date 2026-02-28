package com.ticketing.controller

import com.ticketing.exception.EventNotFoundException
import com.ticketing.exception.NoTicketsAvailableException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ErrorResponse(val status: Int, val message: String)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException::class)
    fun handleNotFound(ex: EventNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(404, ex.message ?: "Event not found"))

    @ExceptionHandler(NoTicketsAvailableException::class)
    fun handleSoldOut(ex: NoTicketsAvailableException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(409, ex.message ?: "No tickets available"))
}
