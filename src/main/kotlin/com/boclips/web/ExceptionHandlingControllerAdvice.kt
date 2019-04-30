package com.boclips.web

import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlingControllerAdvice {
    companion object : KLogging()

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<*> {
        logger.info { "Invalid request: $ex" }
        val errors = ex.bindingResult.allErrors.map { error ->
            val fieldName = (error as FieldError).field
            ApiErrorResource(field = fieldName, message = error.getDefaultMessage().orEmpty())
        }
        return ResponseEntity(ApiErrorsResource(errors = errors), HttpStatus.BAD_REQUEST)
    }
}

