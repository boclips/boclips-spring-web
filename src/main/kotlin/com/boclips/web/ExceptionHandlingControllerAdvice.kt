package com.boclips.web

import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.OffsetDateTime

@ControllerAdvice
class ExceptionHandlingControllerAdvice {
    companion object : KLogging()

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        webRequest: WebRequest
    ): ResponseEntity<*> {
        logger.info { "Invalid request: $ex" }
        val errors = ex.bindingResult.allErrors.map { error ->
            val fieldName = (error as FieldError).field
            ApiErrorResource(
                    path = webRequest.getDescription(false).substringAfter("uri="),
                    timestamp = OffsetDateTime.now().toString(),
                    status = 400,
                    error = "Invalid field: $fieldName",
                    message = error.getDefaultMessage().orEmpty()
            )
        }
        return ResponseEntity(ApiErrorsResource(errors = errors), HttpStatus.BAD_REQUEST)
    }
}

