package ca.myasir.auroraweatherservice.util

import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class InstantJsonAdapterTest {

    private val testInstant = LocalDateTime.of(2023, 1, 1, 12, 0).toInstant(ZoneOffset.UTC)
    private val testInstantIsoString = "2023-01-01T12:00:00.000Z"
    private val adapter = InstantJsonAdapter()

    @Test
    fun `it should return ISO formatted datetime of given Instant`() {
        val actualIsoString = adapter.serialize(testInstant, null, null)?.asString

        Assertions.assertEquals(testInstantIsoString, actualIsoString)
    }

    @Test
    fun `it should return null if given Instant is null when converting`() {
        val isoString = adapter.serialize(null, null, null)

        Assertions.assertNull(isoString)
    }

    @Test
    fun `it should parse given ISO formatted datetime to Instant`() {
        val actualInstant = adapter.deserialize(JsonPrimitive(testInstantIsoString), null, null)

        Assertions.assertEquals(testInstant, actualInstant)
    }
}
