package com.boclips.web

import com.boclips.web.exceptions.BoclipsApiException
import mu.KLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import sun.security.pkcs11.wrapper.Constants
import java.time.OffsetDateTime
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ExceptionHandlingControllerAdvice {
    companion object : KLogging()

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
            ex: MethodArgumentNotValidException,
            webRequest: WebRequest
    ): ResponseEntity<*> {
        logger.info { "Invalid request - due to validation errors: $ex" }
        val errorMessage = ex.bindingResult.allErrors.map { error ->
            val fieldName = (error as FieldError).field
            "- $fieldName ${error.getDefaultMessage().orEmpty()}"
        }.joinToString(Constants.NEWLINE)

        val error = ApiErrorResource(
                path = webRequest.getDescription(false).substringAfter("uri="),
                timestamp = OffsetDateTime.now().toString(),
                status = 400,
                error = "Invalid field/s",
                message = errorMessage
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidationExceptions(
            ex: ConstraintViolationException,
            webRequest: WebRequest
    ): ResponseEntity<*> {
        logger.info { "Invalid request - due to validation errors: $ex" }
        val errorMessage = ex.constraintViolations.map { error ->
            val fieldName = error.propertyPath.toString().substringAfterLast(".")
            "- $fieldName ${error.message.orEmpty()}"
        }.joinToString(Constants.NEWLINE)
        val error = ApiErrorResource(
                path = webRequest.getDescription(false).substringAfter("uri="),
                timestamp = OffsetDateTime.now().toString(),
                status = 400,
                error = "Invalid field/s",
                message = errorMessage
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }


    @ExceptionHandler(BoclipsApiException::class)
    fun handleBoclipsApiExceptions(
            ex: BoclipsApiException,
            webRequest: WebRequest
    ): ResponseEntity<*> {
        logger.info { "Invalid request: $ex" }
        val error = ApiErrorResource(
                path = webRequest.getDescription(false).substringAfter("uri="),
                timestamp = OffsetDateTime.now().toString(),
                status = ex.exceptionDetails.status.value(),
                error = ex.exceptionDetails.error,
                message = ex.exceptionDetails.message
        )
        return ResponseEntity(
                error,
                HttpHeaders().apply { put("Content-Type", listOf("application/json")) },
                ex.exceptionDetails.status
        )
    }
}

