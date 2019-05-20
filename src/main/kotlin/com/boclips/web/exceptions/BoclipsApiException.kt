package com.boclips.web.exceptions

import org.springframework.http.HttpStatus
import java.lang.RuntimeException

abstract class BoclipsApiException(val exceptionDetails: ExceptionDetails) : RuntimeException()

data class ExceptionDetails(
        val error: String,
        val message: String,
        val status: HttpStatus = HttpStatus.BAD_REQUEST
)