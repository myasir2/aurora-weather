package ca.myasir.auroraweatherservice.service

import ca.myasir.auroraweatherservice.exception.ApiGatewayServiceException
import ca.myasir.auroraweatherservice.logger
import com.google.gson.Gson
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.StringEntity
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.signer.Aws4Signer
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams
import software.amazon.awssdk.http.Header
import software.amazon.awssdk.http.SdkHttpFullRequest
import software.amazon.awssdk.http.SdkHttpMethod
import software.amazon.awssdk.utils.StringInputStream
import java.io.BufferedReader
import java.net.URI

@Service
class ApiGatewayService(
    private val gson: Gson,
    private val signer: Aws4Signer,
    private val signerParams: Aws4SignerParams
) {

    /**
     * This method will execute a JSON POST AWS SigV4-signed request to an API Gateway endpoint
     */
    fun <T> executePostRequest(request: Any, apiUrl: String, clazz: Class<T>): T {
        val body = gson.toJson(request)

        logger.info { "Executing POST request at $apiUrl with $body" }

        // Create the POST request
        val sdkHttpRequest = SdkHttpFullRequest.builder()
            .uri(URI(apiUrl))
            .contentStreamProvider { StringInputStream(body) }
            .method(SdkHttpMethod.POST)
            .appendHeader(Header.ACCEPT, JSON_MIME_TYPE)
            .appendHeader(Header.CONTENT_TYPE, JSON_MIME_TYPE)
            .build()

        val signedRequest = signer.sign(sdkHttpRequest, signerParams)
        val httpClient = HttpClients.createDefault()

        // Convert the signed request into an Apache HttpPost, and add all the signed headers to the HttpPost request
        val httpPost = HttpPost(apiUrl).apply {
            entity = StringEntity(body)

            signedRequest.headers().forEach { (key, value) ->
                this.addHeader(key, value.joinToString(","))
            }
        }

        // Execute the request and get the response
        return try {
            httpClient.execute(httpPost).use { response ->
                val responseBody = response.entity.content.bufferedReader().use(BufferedReader::readText)

                logger.info { "Response status and body: ${response.code} => $responseBody" }

                gson.fromJson(responseBody, clazz)
            }
        } catch (e: Exception) {
            logger.error { "Error while executing POST request at $apiUrl" }

            throw ApiGatewayServiceException(e)
        }
    }

    companion object {
        const val JSON_MIME_TYPE = "application/json"
    }
}
