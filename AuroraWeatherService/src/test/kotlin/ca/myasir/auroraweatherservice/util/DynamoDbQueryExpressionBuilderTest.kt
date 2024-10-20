package ca.myasir.auroraweatherservice.util

import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DynamoDbQueryExpressionBuilderTest {

    @Test
    fun `it should create an expression with which contains a string equal`() {
        val attributeName = "attribute"
        val attributeValue = "value"
        val expectedAttributeValues = mutableMapOf(
            ":attribute" to AttributeValue.builder().s(attributeValue).build()
        )
        val expectedExpression = Expression.builder()
            .expression("attribute = :attribute")
            .expressionValues(expectedAttributeValues)
            .build()

        val expressionBuilder = DynamoDbQueryExpressionBuilder()
            .andEqualsString(attributeName, attributeValue)

        val actualExpression = expressionBuilder.expression
        val actualAttributeValues = expressionBuilder.values

        assertEquals(expectedExpression, actualExpression)
        assertEquals(expectedAttributeValues, actualAttributeValues)
    }

    @Test
    fun `it should create an expression which contains a boolean equal true`() {
        val attributeName = "attribute"
        val attributeValue = true
        val expectedAttributeValues = mutableMapOf(
            ":attribute" to AttributeValue.builder().n("1").build()
        )
        val expectedExpression = Expression.builder()
            .expression("attribute = :attribute")
            .expressionValues(expectedAttributeValues)
            .build()

        val expressionBuilder = DynamoDbQueryExpressionBuilder()
            .andEqualsBoolean(attributeName, attributeValue)

        val actualExpression = expressionBuilder.expression
        val actualAttributeValues = expressionBuilder.values

        assertEquals(expectedExpression, actualExpression)
        assertEquals(expectedAttributeValues, actualAttributeValues)
    }

    @Test
    fun `it should create an expression which contains a boolean equal false`() {
        val attributeName = "attribute"
        val attributeValue = false
        val expectedAttributeValues = mutableMapOf(
            ":attribute" to AttributeValue.builder().n("0").build()
        )
        val expectedExpression = Expression.builder()
            .expression("attribute = :attribute")
            .expressionValues(expectedAttributeValues)
            .build()

        val expressionBuilder = DynamoDbQueryExpressionBuilder()
            .andEqualsBoolean(attributeName, attributeValue)

        val actualExpression = expressionBuilder.expression
        val actualAttributeValues = expressionBuilder.values

        assertEquals(expectedExpression, actualExpression)
        assertEquals(expectedAttributeValues, actualAttributeValues)
    }
}
