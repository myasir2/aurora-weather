package ca.myasir.auroraweatherservice

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

val logger = KotlinLogging.logger {}

@SpringBootApplication
class AuroraWeatherServiceApplication

fun main(args: Array<String>) {
    runApplication<AuroraWeatherServiceApplication>(*args)
}
