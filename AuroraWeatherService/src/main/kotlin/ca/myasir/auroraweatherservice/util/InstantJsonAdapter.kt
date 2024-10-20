package ca.myasir.auroraweatherservice.util

import com.google.gson.*
import java.lang.reflect.Type
import java.time.Instant

class InstantJsonAdapter : JsonSerializer<Instant?>, JsonDeserializer<Instant?> {

    override fun serialize(instant: Instant?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return instant?.let(Instant::toIsoFormat)
            ?.let(::JsonPrimitive)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Instant? {
        return json.asString?.let(Instant::parse)
    }
}
