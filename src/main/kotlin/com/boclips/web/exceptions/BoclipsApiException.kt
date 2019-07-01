package com.boclips.web.exceptions

import org.springframework.http.HttpStatus

abstract class BoclipsApiException(val exceptionDetails: ExceptionDetails) : RuntimeException() {
    override fun toString(): String {
        return "${exceptionDetails.error}: ${exceptionDetails.message}"
    }
}

data class ExceptionDetails(
    val error: String,
    val message: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST
)