package ca.myasir.auroraweatherservice.dao.impl

import ca.myasir.auroraweatherservice.dao.DynamoDbDao
import ca.myasir.auroraweatherservice.exception.DynamoDbUpdateException
import ca.myasir.auroraweatherservice.logger
import ca.myasir.auroraweatherservice.util.DynamoDbConditionalExpressionBuilder
import ca.myasir.auroraweatherservice.util.DynamoDbQueryExpressionBuilder
import com.google.gson.Gson
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.*
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException

/**
 * A generic class to interact with DynamoDB. The `type` is of the entity that represents a row in DDB.
 */
class DynamoDbDaoImpl<T : Any>(
    private val ddbClient: DynamoDbEnhancedClient,
    private val classType: Class<T>,
    private val table: DynamoDbTable<T>
) : DynamoDbDao<T> {

    private val gson = Gson()

    override fun update(row: T) {
        table.updateItem(row)
    }

    override fun get(hashKey: String, rangeKey: String?, consistentRead: Boolean): T? {
        val key = Key.builder()
            .partitionValue(hashKey)
            .apply {
                rangeKey?.let {
                    sortValue(rangeKey)
                }
            }
            .build()

        val request = GetItemEnhancedRequest.builder()
            .consistentRead(consistentRead)
            .key(key)
            .build()

        return table.getItem(request)
    }

    override fun batchLoad(hashKeys: Collection<String>, consistentRead: Boolean): List<T> {
        val readBatch = ReadBatch.builder(classType)
            .mappedTableResource(table)
            .apply {
                hashKeys.forEach { hashKey ->
                    val key = Key.builder()
                        .partitionValue(hashKey)
                        .build()

                    addGetItem(
                        GetItemEnhancedRequest.builder()
                            .consistentRead(consistentRead)
                            .key(key)
                            .build()
                    )
                }
            }
            .build()


        val request = BatchGetItemEnhancedRequest.builder()
            .addReadBatch(readBatch)
            .build()

        return ddbClient.batchGetItem(request).resultsForTable(table).toList()
    }

    override fun query(
        keyCondition: QueryConditional,
        filterExpression: DynamoDbQueryExpressionBuilder?,
        requestedAttributes: Collection<String>?,
        consistentRead: Boolean,
        scanIndexForward: Boolean,
        limit: Int?
    ): List<T> {
        val queryEnhancedRequest = QueryEnhancedRequest.builder()
            .queryConditional(keyCondition)
            .filterExpression(filterExpression?.expression)
            .attributesToProject(requestedAttributes)
            .consistentRead(consistentRead)
            .scanIndexForward(scanIndexForward)
            .limit(limit)
            .build()

        return table.query(queryEnhancedRequest).items().toList()
    }

    override fun batchUpdate(
        rows: Set<T>,
        conditionalExpressionBuilder: DynamoDbConditionalExpressionBuilder?
    ) {
        // DDB SDK would throw an exception if we try to write empty/null rows
        if (rows.isEmpty()) {
            return
        }
        val operationStartTime = System.currentTimeMillis()

        logger.info { "Total number of ${classType.simpleName} rows to update: ${rows.size}" }

        rows.chunked(MAX_TRANSACT_ITEMS).forEach { items ->
            conditionalExpressionBuilder?.let {
                logger.info { "Adding conditional expression: ${it.expression}" }
            }

            val requestBuilder = TransactWriteItemsEnhancedRequest.builder()
            items.forEach { item ->
                requestBuilder.addPutItem(
                    table,
                    TransactPutItemEnhancedRequest.builder(classType)
                        .item(item)
                        .conditionExpression(conditionalExpressionBuilder?.expression)
                        .build()
                )
            }
            val request = requestBuilder.build()

            try {
                val requestStartTime = System.currentTimeMillis()

                ddbClient.transactWriteItems(request)

                val requestLatency = System.currentTimeMillis() - requestStartTime

                logger.info { "Total time took to run TransactWrite for chunk ${items.size} rows: $requestLatency milliseconds" }
            } catch (e: TransactionCanceledException) {
                val reasons = e.cancellationReasons()
                val schema = table.tableSchema()
                val failedItems = reasons.mapNotNull { failedItem ->
                    logger.warn { "Cancellation reason: ${failedItem.message()}" }

                    failedItem.item()?.let { reason ->
                        reason.isNotEmpty().let { schema.mapToItem(reason) }
                    }
                }

                logger.warn { "Failed to update items $failedItems" }

                throw DynamoDbUpdateException(
                    failedItems,
                    "Existing records found while updating in DDB"
                )
            } catch (e: Exception) {
                logger.error { "Failed to update ${classType.simpleName} items: ${gson.toJson(items)}" }

                throw e
            }
        }

        val operationLatency = System.currentTimeMillis() - operationStartTime

        logger.info { "Total time took to write all ${classType.simpleName} rows: $operationLatency milliseconds" }
    }

    private companion object {
        const val MAX_TRANSACT_ITEMS = 100
    }
}
