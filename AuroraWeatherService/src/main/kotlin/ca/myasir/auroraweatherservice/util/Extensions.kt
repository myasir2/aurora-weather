package ca.myasir.auroraweatherservice.util

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    .withZone(ZoneOffset.UTC)

fun Instant.toIsoFormat(): String {
    return dateTimeFormatter.format(this)
}
