package ca.myasir.auroraweatherservice.dao.impl

import ca.myasir.auroraweatherservice.dao.DynamoDbDao
import ca.myasir.auroraweatherservice.exception.DynamoDbUpdateException
import ca.myasir.auroraweatherservice.test_util.DynamoDbTestUtils
import ca.myasir.auroraweatherservice.util.DynamoDbConditionalExpressionBuilder
import ca.myasir.auroraweatherservice.util.DynamoDbQueryExpressionBuilder
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import software.amazon.awssdk.core.pagination.sync.SdkIterable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import software.amazon.awssdk.enhanced.dynamodb.model.*
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.CancellationReason
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException

typealias WriteRequest = TransactWriteItemsEnhancedRequest
typealias WriteRequestBuilder = TransactWriteItemsEnhancedRequest.Builder

@DynamoDbBean
data class BasicDynamoTableRow(
    @get:DynamoDbPartitionKey
    var hashKey: String = "hashkey",

    @get:DynamoDbSortKey
    var rangeKey: String? = null
)

internal class DynamoDbImplTest {

    private lateinit var basicDao: DynamoDbDao<BasicDynamoTableRow>
    private lateinit var mockWriteRequestBuilder: WriteRequestBuilder

    private val client: DynamoDbEnhancedClient = mockk()
    private val table: DynamoDbTable<BasicDynamoTableRow> = mockk()
    private val testHashKey = "hashkey"
    private val testRangeKey = "rangekey"
    private val classType = BasicDynamoTableRow::class.java
    private val tableName = "table"
    private val tableSchema: TableSchema<BasicDynamoTableRow> = mockk()
    private val batchResults: BatchGetResultPageIterable = mockk()

    @BeforeEach
    fun setUp() {
        every { table.tableSchema() } returns (tableSchema)
        every { table.tableName() } returns (tableName)
        every { client.transactWriteItems(any<WriteRequest>()) } answers { nothing }
        every { client.table(any<String>(), any<TableSchema<BasicDynamoTableRow>>()) } returns table

        basicDao = DynamoDbDaoImpl(
            client, BasicDynamoTableRow::class.java, table
        )

        mockWriteRequestBuilder = mockk(relaxed = true)
        mockkStatic(WriteRequest::class)

        every { WriteRequest.builder() } returns mockWriteRequestBuilder
    }

    @Test
    fun `it should call DDB to update the given record`() {
        val expectedRow = BasicDynamoTableRow(testHashKey, null)

        justRun {
            table.updateItem(expectedRow)
        }

        basicDao.update(expectedRow)

        verify(exactly = 1) {
            table.updateItem(expectedRow)
        }
    }

    @Test
    fun `it should throw Exception if DDB throws exception while updating a record`() {
        val expectedRow = BasicDynamoTableRow(testHashKey, null)

        every {
            table.updateItem(expectedRow)
        }.throws(RuntimeException("Something broke while updating"))

        assertThrows<RuntimeException> {
            basicDao.update(expectedRow)
        }
    }

    @Test
    fun `it should return existing row for the given hash key`() {
        val expectedRow = BasicDynamoTableRow(testHashKey, null)
        val expectedKey = Key.builder()
            .partitionValue(testHashKey)
            .build()
        val request = GetItemEnhancedRequest.builder()
            .key(expectedKey)
            .consistentRead(false)
            .build()

        every {
            table.getItem(request)
        }.returns(expectedRow)

        val actualRow = basicDao.get(testHashKey, consistentRead = false)

        assertNotNull(actualRow)
        assertEquals(testHashKey, actualRow!!.hashKey)

        verify(exactly = 1) {
            table.getItem(request)
        }
    }

    @Test
    fun `it should return existing row for the given hash and range key`() {
        val expectedRow = BasicDynamoTableRow(testHashKey, testRangeKey)
        val expectedKey = Key.builder()
            .partitionValue(testHashKey)
            .sortValue(testRangeKey)
            .build()
        val request = GetItemEnhancedRequest.builder()
            .key(expectedKey)
            .consistentRead(false)
            .build()

        every {
            table.getItem(request)
        }.returns(expectedRow)

        val actualRow = basicDao.get(testHashKey, testRangeKey, false)!!

        assertEquals(testHashKey, actualRow.hashKey)
        assertEquals(testRangeKey, actualRow.rangeKey)
        verify(exactly = 1) {
            table.getItem(request)
        }
    }

    @Test
    fun `it should return existing row for the given hash and range key with consistent read`() {
        val expectedRow = BasicDynamoTableRow(testHashKey, testRangeKey)
        val expectedKey = Key.builder()
            .partitionValue(testHashKey)
            .sortValue(testRangeKey)
            .build()
        val request = GetItemEnhancedRequest.builder()
            .key(expectedKey)
            .consistentRead(true)
            .build()

        every {
            table.getItem(request)
        }.returns(expectedRow)

        val actualRow = basicDao.get(testHashKey, testRangeKey, true)!!

        assertEquals(testHashKey, actualRow.hashKey)
        assertEquals(testRangeKey, actualRow.rangeKey)
        verify(exactly = 1) {
            table.getItem(request)
        }
    }

    @Test
    fun `it should throw exception if DynamoDb throws exception while fetching row`() {
        every {
            table.getItem(any<GetItemEnhancedRequest>())
        }.throws(RuntimeException("Something broke while fetching data"))

        assertThrows<RuntimeException> {
            basicDao.get(testHashKey, testRangeKey)
        }
    }

    @Test
    fun `it should return all rows from DDB for the given hash keys`() {
        val hashKeys = listOf(testHashKey)
        val expectedRow = BasicDynamoTableRow(testHashKey, testRangeKey)
        val expectedRowList = mutableListOf(expectedRow, expectedRow)

        every { table.tableSchema() }.returns(TableSchema.fromBean(classType))
        val expectedReadRequests = hashKeys.map {
            GetItemEnhancedRequest.builder()
                .consistentRead(false)
                .key(
                    Key.builder()
                        .partitionValue(it)
                        .build()
                )
                .build()
        }
        val readBatch = ReadBatch.builder(classType)
            .mappedTableResource(table)
            .addGetItem(expectedReadRequests[0])
            .build()
        val expectedRequest = BatchGetItemEnhancedRequest.builder()
            .addReadBatch(readBatch)
            .build()

        every {
            batchResults.resultsForTable(table)
        }.returns(
            SdkIterable { expectedRowList.iterator() }
        )

        every {
            client.batchGetItem(expectedRequest)
        }.returns(
            batchResults
        )

        val actualRows = basicDao.batchLoad(hashKeys, consistentRead = false)

        assertEquals(expectedRowList, actualRows)
        verify(exactly = 1) {
            client.batchGetItem(expectedRequest)
        }
    }

    @Test
    fun `it should return empty list if no rows were found for the given hash keys`() {
        val hashKeys = listOf(testHashKey)

        every {
            table.tableSchema()
        }.returns(
            TableSchema.fromBean(
                classType
            )
        )

        every {
            batchResults.resultsForTable(table)
        }.returns(
            SdkIterable { mutableListOf<BasicDynamoTableRow>().iterator() }
        )

        every {
            client.batchGetItem(any<BatchGetItemEnhancedRequest>())
        }.returns(
            batchResults
        )

        val actualRows = basicDao.batchLoad(hashKeys)

        assertTrue(actualRows.isEmpty())
        verify(exactly = 1) {
            client.batchGetItem(any<BatchGetItemEnhancedRequest>())
        }
    }

    @Test
    fun `it should throw Exception if DDB threw exception while loading for the given hash keys`() {
        every {
            client.batchGetItem { r -> any() }
        }.throws(RuntimeException("Something broke while loading for the given hash keys"))

        assertThrows<RuntimeException> {
            val hashKeys = listOf(testHashKey)

            basicDao.batchLoad(hashKeys)
        }
    }

    @Test
    fun `it should return rows based on query expression from DynamoDB`() {
        val mockPaginatedQueryList: PageIterable<BasicDynamoTableRow> = mockk()
        val expectedRows = SdkIterable {
            mutableListOf(BasicDynamoTableRow(testHashKey, testRangeKey)).iterator()
        }
        val conditional = QueryConditional.keyEqualTo { it.partitionValue(testHashKey).sortValue(testRangeKey) }
        val queryExpression = DynamoDbTestUtils.createSampleQueryExpression("hashKey", testHashKey)

        every {
            mockPaginatedQueryList.items()
        }.returns(expectedRows)

        every {
            table.query(any<QueryEnhancedRequest>())
        }.returns(mockPaginatedQueryList)

        val actualResultsList = basicDao.query(conditional, queryExpression, null)

        assertEquals(expectedRows.toList(), actualResultsList)
        verify(exactly = 1) {
            table.query(
                withArg<QueryEnhancedRequest> {
                    assertEquals(conditional, it.queryConditional())
                    assertEquals(queryExpression.expression, it.filterExpression())
                }
            )
        }
    }

    @Test
    fun `it should return rows based on query expression from DynamoDB for the given index`() {
        val mockPaginatedQueryList: PageIterable<BasicDynamoTableRow> = mockk()
        val expectedRows = SdkIterable {
            mutableListOf(BasicDynamoTableRow(testHashKey, testRangeKey)).iterator()
        }
        val queryExpression = DynamoDbTestUtils.createSampleQueryExpression("hashKey", testHashKey)
        val conditional = QueryConditional.keyEqualTo { it.partitionValue(testHashKey).sortValue(testRangeKey) }

        every {
            mockPaginatedQueryList.items()
        }.returns(expectedRows)

        every {
            table.query(any<QueryEnhancedRequest>())
        }.returns(mockPaginatedQueryList)

        val actualResultsList = basicDao.query(conditional, queryExpression, null)

        assertEquals(expectedRows.toList(), actualResultsList)
        verify(exactly = 1) {
            table.query(
                withArg<QueryEnhancedRequest> {
                    assertEquals(conditional, it.queryConditional())
                    assertEquals(queryExpression.expression, it.filterExpression())
                }
            )
        }
    }

    @Test
    fun `it should return rows based on query and filter expression from DynamoDB`() {
        val mockPaginatedQueryList: PageIterable<BasicDynamoTableRow> = mockk()
        val expectedRows = SdkIterable {
            mutableListOf(BasicDynamoTableRow(testHashKey, testRangeKey)).iterator()
        }
        val filterExpression = DynamoDbTestUtils.createSampleQueryExpression("attribute", "value")
        val conditional = QueryConditional.keyEqualTo { it.partitionValue(testHashKey).sortValue(testRangeKey) }

        every {
            mockPaginatedQueryList.items()
        }.returns(expectedRows)

        every {
            table.query(any<QueryEnhancedRequest>())
        }.returns(mockPaginatedQueryList)

        val actualResultsList = basicDao.query(conditional, filterExpression, listOf("attribute"))

        assertEquals(expectedRows.toList(), actualResultsList)
        verify(exactly = 1) {
            table.query(
                withArg<QueryEnhancedRequest> {
                    assertEquals(conditional, it.queryConditional())
                    assertEquals(filterExpression.expression, it.filterExpression())
                }
            )
        }
    }

    @Test
    fun `it should return rows based on query and filter expression from DynamoDB with consistent read, scan forward, and limit`() {
        val mockPaginatedQueryList: PageIterable<BasicDynamoTableRow> = mockk()
        val expectedRows = SdkIterable {
            mutableListOf(BasicDynamoTableRow(testHashKey, testRangeKey)).iterator()
        }
        val filterExpression = DynamoDbTestUtils.createSampleQueryExpression("attribute", "value")
        val conditional = QueryConditional.keyEqualTo { it.partitionValue(testHashKey).sortValue(testRangeKey) }
        val attributesToProject = listOf("attribute")
        val expectedRequest = QueryEnhancedRequest.builder()
            .queryConditional(conditional)
            .filterExpression(filterExpression.expression)
            .attributesToProject(attributesToProject)
            .consistentRead(true)
            .scanIndexForward(true)
            .limit(10)
            .build()

        every {
            mockPaginatedQueryList.items()
        }.returns(expectedRows)

        every {
            table.query(expectedRequest)
        }.returns(mockPaginatedQueryList)

        val actualResultsList = basicDao.query(
            conditional,
            filterExpression,
            attributesToProject,
            consistentRead = true,
            scanIndexForward = true,
            limit = 10
        )

        assertEquals(expectedRows.toList(), actualResultsList)
        verify(exactly = 1) {
            table.query(
                withArg<QueryEnhancedRequest> {
                    assertEquals(conditional, it.queryConditional())
                    assertEquals(filterExpression.expression, it.filterExpression())
                }
            )
        }
    }

    @Test
    fun `it should throw Exception if DDB throws exception while querying`() {
        val conditional = QueryConditional.keyEqualTo { it.partitionValue(testHashKey).sortValue(testRangeKey) }
        val queryExpression = DynamoDbQueryExpressionBuilder()
            .andEqualsString("hashKey", testHashKey)

        every {
            table.query(any<QueryEnhancedRequest>())
        }.throws(RuntimeException("Something broke while querying"))

        assertThrows<RuntimeException> {
            basicDao.query(conditional, queryExpression, null)
        }
    }

    @Test
    fun `it should batch update using the TransactionsWrite API to DDB`() {
        val rows = setOf(
            BasicDynamoTableRow(testHashKey, testRangeKey),
            BasicDynamoTableRow(testHashKey, "anotherRangeKey")
        )

        basicDao.batchUpdate(rows)

        verify(exactly = 1) {
            client.transactWriteItems(any<TransactWriteItemsEnhancedRequest>())
        }
    }

    @Test
    fun `it should batch update with conditional expression if provided to DDB`() {
        val rows = setOf(
            BasicDynamoTableRow(testHashKey, testRangeKey),
            BasicDynamoTableRow(testHashKey, "anotherRangeKey")
        )
        val conditionalExpressionBuilder = DynamoDbConditionalExpressionBuilder()
            .withAttributeNotExists("hashKey")

        basicDao.batchUpdate(rows, conditionalExpressionBuilder)

        verify(exactly = 1) {
            client.transactWriteItems(any<TransactWriteItemsEnhancedRequest>())
        }
    }

    @Test
    fun `it should throw DynamoDbUpdateException if conditional exception occurs while batch saving`() {
        val rows = listOf(
            BasicDynamoTableRow(testHashKey, testRangeKey),
            BasicDynamoTableRow(testHashKey, "anotherRangeKey")
        )
        val attributeValues = mapOf(
            "hashKey" to AttributeValue.builder().s(testHashKey).build()
        )
        val expectedFailedItems = listOf(rows[0])
        val conditionalExpressionBuilder = DynamoDbConditionalExpressionBuilder()
            .withAttributeNotExists("hashKey")

        every {
            tableSchema.mapToItem(attributeValues)
        }.returns(expectedFailedItems[0])

        every {
            client.transactWriteItems(any<TransactWriteItemsEnhancedRequest>())
        }.throws(
            TransactionCanceledException.builder()
                .message("Existing rows found")
                .cancellationReasons(
                    CancellationReason.builder().item(attributeValues).build()
                ).build()
        )

        val exception = assertThrows<DynamoDbUpdateException> {
            basicDao.batchUpdate(rows.toSet(), conditionalExpressionBuilder)
        }
        assertEquals(expectedFailedItems, exception.existingRows)
    }

    @Test
    fun `it should throw DynamoDbUpdateException with empty failedItems if DDB didn't return any items`() {
        val rows = setOf(
            BasicDynamoTableRow(testHashKey, testRangeKey),
            BasicDynamoTableRow(testHashKey, "anotherRangeKey")
        )
        val expectedFailedItems = emptyList<BasicDynamoTableRow>()
        val conditionalExpressionBuilder = DynamoDbConditionalExpressionBuilder()
            .withAttributeNotExists("hashKey")

        every {
            tableSchema.mapToItem(emptyMap())
        }.returns(null)

        every {
            client.transactWriteItems(any<TransactWriteItemsEnhancedRequest>())
        }.throws(
            TransactionCanceledException.builder()
                .message("Existing rows found")
                .cancellationReasons(
                    CancellationReason.builder()
                        .item(null).build()
                ).build()
        )

        val exception = assertThrows<DynamoDbUpdateException> {
            basicDao.batchUpdate(rows, conditionalExpressionBuilder)
        }
        assertEquals(expectedFailedItems, exception.existingRows)
    }

    @Test
    fun `it should not call DDB to batch update empty rows`() {
        basicDao.batchUpdate(emptySet())

        verify(exactly = 0) {
            client.transactWriteItems(any<TransactWriteItemsEnhancedRequest>())
        }
    }

    @Test
    fun `it should throw Exception if DDB throws exception during batch update`() {
        val rows = setOf(
            BasicDynamoTableRow(testHashKey, testRangeKey),
            BasicDynamoTableRow(testHashKey, "anotherRangeKey")
        )

        every {
            client.transactWriteItems(any<TransactWriteItemsEnhancedRequest>())
        }.throws(RuntimeException("Something broke while transaction writing"))

        assertThrows<RuntimeException> {
            basicDao.batchUpdate(rows)
        }
    }

    @Test
    fun `it should update more than 100 items correctly`() {
        val rows = (1..200).map {
            BasicDynamoTableRow("$testHashKey-$it", null)
        }

        basicDao.batchUpdate(rows.toSet())

        verify(exactly = 2) {
            client.transactWriteItems(any<TransactWriteItemsEnhancedRequest>())
        }
    }
}
