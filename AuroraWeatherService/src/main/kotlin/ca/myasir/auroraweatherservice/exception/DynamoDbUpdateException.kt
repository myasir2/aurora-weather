package ca.myasir.auroraweatherservice.exception

class DynamoDbUpdateException(
    val existingRows: List<*>,
    message: String
) : RuntimeException(message)
