package com.boclips.web

import com.boclips.web.config.ErrorHandlingConfig
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Import(ErrorHandlingConfig::class)
annotation class EnableBoclipsApiErrors