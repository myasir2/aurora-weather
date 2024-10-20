package ca.myasir.auroraweatherservice.test_util

import ca.myasir.auroraweatherservice.util.DynamoDbQueryExpressionBuilder

object DynamoDbTestUtils {

    fun createSampleQueryExpression(attribute: String, value: String): DynamoDbQueryExpressionBuilder {
        return DynamoDbQueryExpressionBuilder()
            .andEqualsString(attribute, value)
    }

    fun createSampleQueryExpression(attribute: String, value: Boolean): DynamoDbQueryExpressionBuilder {
        return DynamoDbQueryExpressionBuilder()
            .andEqualsBoolean(attribute, value)
    }
}
