package com.boclips.web.config

import com.boclips.web.ExceptionHandlingControllerAdvice
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ErrorHandlingConfig {

    @Bean
    fun exceptionHandling() = ExceptionHandlingControllerAdvice()

}