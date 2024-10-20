package ca.myasir.auroraweatherservice.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class InstantTypeConverterTest {

    private val testInstant = LocalDateTime.of(2023, 1, 1, 12, 0).toInstant(ZoneOffset.UTC)
    private val testInstantIsoString = "2023-01-01T12:00:00.000Z"
    private val converter = InstantTypeConverter()

    @Test
    fun `it should return ISO formatted datetime of given Instant`() {
        val actualIsoString = converter.transformFrom(testInstant)

        assertEquals(testInstantIsoString, actualIsoString?.s())
    }

    @Test
    fun `it should return null if given Instant is null when converting`() {
        val isoString = converter.transformFrom(null)

        assertNull(isoString)
    }

    @Test
    fun `it should parse given ISO formatted datetime to Instant`() {
        val actualInstant = converter.transformTo(AttributeValue.fromS(testInstantIsoString))

        assertEquals(testInstant, actualInstant)
    }

    @Test
    fun `it should return null if given ISO formatted string is null when un-converting`() {
        val instant = converter.transformTo(null)

        assertNull(instant)
    }
}
