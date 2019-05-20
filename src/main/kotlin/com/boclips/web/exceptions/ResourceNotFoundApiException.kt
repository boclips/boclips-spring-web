package com.boclips.web.exceptions

import org.springframework.http.HttpStatus

open class ResourceNotFoundApiException(
        error: String,
        message: String
) : BoclipsApiException(ExceptionDetails(
        error = error,
        message = message,
        status = HttpStatus.NOT_FOUND
))