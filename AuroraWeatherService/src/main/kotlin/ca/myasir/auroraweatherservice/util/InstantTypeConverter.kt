package ca.myasir.auroraweatherservice.util

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant

class InstantTypeConverter : AttributeConverter<Instant> {

    override fun transformFrom(instant: Instant?): AttributeValue? {
        return instant?.let { AttributeValue.fromS(it.toIsoFormat()) }
    }

    override fun transformTo(string: AttributeValue?): Instant? {
        return string?.let { Instant.parse(it.s()) }
    }

    override fun type(): EnhancedType<Instant>? {
        return EnhancedType.of(Instant::class.java)
    }

    override fun attributeValueType(): AttributeValueType {
        return AttributeValueType.S
    }
}
