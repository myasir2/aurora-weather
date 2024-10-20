package ca.myasir.auroraweatherservice.util

import software.amazon.awssdk.enhanced.dynamodb.Expression

class DynamoDbConditionalExpressionBuilder {

    private val andSeparator = " AND "
    private val expressions: MutableList<String> = mutableListOf()

    val expression: Expression
        get() = this.createExpression()

    fun withAttributeNotExists(attributeName: String): DynamoDbConditionalExpressionBuilder {
        expressions.add("attribute_not_exists($attributeName)")

        return this
    }

    private fun createExpression(): Expression {
        return Expression.builder()
            .expression(expressions.joinToString(andSeparator))
            .build()
    }
}
