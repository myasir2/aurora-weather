package ca.myasir.auroraweatherservice.dao

import ca.myasir.auroraweatherservice.util.DynamoDbConditionalExpressionBuilder
import ca.myasir.auroraweatherservice.util.DynamoDbQueryExpressionBuilder
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional

interface DynamoDbDao<T : Any> {

    /**
     * This method will update the given record in DDB
     */
    fun update(row: T)

    /**
     * This method will return a row from DDB with the given partition and optional sort key
     */
    fun get(hashKey: String, rangeKey: String? = null, consistentRead: Boolean = false): T?

    /**
     * This method will return rows from DDB with the given partition keys
     * (only useful when the table schema only has a partition key)
     */
    fun batchLoad(hashKeys: Collection<String>, consistentRead: Boolean = false): List<T>

    /**
     * This method is useful for querying all rows with the given key(s) (and their values),
     * with any filtering operations
     */
    fun query(
        keyCondition: QueryConditional,
        filterExpression: DynamoDbQueryExpressionBuilder? = null,
        requestedAttributes: Collection<String>? = null,
        consistentRead: Boolean = false,
        scanIndexForward: Boolean = false,
        limit: Int? = null
    ): List<T>

    /**
     * This method will batch update in DDB using the TransactionsWrite API for DDB. The return is the rows updated.
     * If a conditional expression is provided, then failed rows will be returned
     */
    fun batchUpdate(
        rows: Set<T>,
        conditionalExpressionBuilder: DynamoDbConditionalExpressionBuilder? = null
    )
}
