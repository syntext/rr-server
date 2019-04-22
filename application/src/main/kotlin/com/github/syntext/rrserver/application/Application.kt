package com.github.syntext.rrserver.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class Application

fun main(args: Array<String>) {
	TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
	runApplication<Application>(*args)
}
