package ca.myasir.auroraweatherservice.util

import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class DynamoDbQueryExpressionBuilder {

    private val andSeparator = " AND "
    private val expressions: MutableList<String> = mutableListOf()

    val values: MutableMap<String, AttributeValue> = mutableMapOf()
    val expression: Expression
        get() = this.createExpression()

    fun andEqualsString(attribute: String, value: String): DynamoDbQueryExpressionBuilder {
        expressions.add("$attribute = :$attribute")
        values[":$attribute"] = AttributeValue.builder().s(value).build()

        return this
    }

    fun andEqualsBoolean(attribute: String, value: Boolean): DynamoDbQueryExpressionBuilder {
        // DDB is storing true/false as 1/0 numbers by default. Using the @DynamoDBTyped(DynamoDBAttributeType.BOOL)
        // annotation on the attribute would supposedly make it store true/false, however this does not seem to work
        // in Kotlin. Therefore, a simple if/else condition to choose 1/0 respectively when querying a boolean field
        val booleanInt = if (value) 1 else 0

        expressions.add("$attribute = :$attribute")
        values[":$attribute"] = AttributeValue.builder().n(booleanInt.toString()).build()

        return this
    }

    private fun createExpression(): Expression {
        return Expression.builder()
            .expression(expressions.joinToString(andSeparator))
            .expressionValues(values)
            .build()
    }
}
