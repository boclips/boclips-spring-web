package com.boclips.web.demo

import com.boclips.web.EnableBoclipsApiErrors
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBoclipsApiErrors
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
