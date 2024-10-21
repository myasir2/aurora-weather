package ca.myasir.auroraweatherservice.service

import ca.myasir.auroraweatherservice.exception.ApiGatewayServiceException
import ca.myasir.auroraweatherservice.service.ApiGatewayService.Companion.JSON_MIME_TYPE
import com.google.gson.Gson
import io.mockk.*
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.HttpEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import software.amazon.awssdk.auth.signer.Aws4Signer
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams
import software.amazon.awssdk.http.Header
import software.amazon.awssdk.http.SdkHttpFullRequest
import software.amazon.awssdk.http.SdkHttpMethod
import software.amazon.awssdk.utils.StringInputStream
import java.net.URI
import kotlin.test.assertEquals

internal class ApiGatewayLocationGrpcOperationTest {

    private val gson = Gson()
    private val testRequestPayload = Payload("request")
    private val testResponsePayload = Payload("response")
    private val testUrl = "http://localhost:8080"
    private val testSignedRequest = createSignedRequest()
    private val mockedHttpClient: CloseableHttpClient = mockk()
    private val mockedSigner: Aws4Signer = mockk()
    private val mockedSignerParams: Aws4SignerParams = mockk()

    private val service = ApiGatewayService(gson, mockedSigner, mockedSignerParams)

    @BeforeEach
    fun setup() {
        clearAllMocks()

        mockkStatic(HttpClients::class)

        every { HttpClients.createDefault() } returns mockedHttpClient
        every { mockedSigner.sign(any<SdkHttpFullRequest>(), any<Aws4SignerParams>()) } returns testSignedRequest
    }

    @Test
    fun `it should successfully call API Gateway with the given payload, url, and response`() {
        val mockedResponse = createHttpPostResponse()

        every { mockedHttpClient.execute(any()) } returns mockedResponse

        val actualResponsePayload = service.executePostRequest(testRequestPayload, testUrl, Payload::class.java)

        assertEquals(testResponsePayload, actualResponsePayload)
    }

    @Test
    fun `it should throw ApiGatewayServiceException if API Gateway throws exception`() {
        every { mockedHttpClient.execute(any()) } throws RuntimeException("Something broke")

        assertThrows<ApiGatewayServiceException> {
            service.executePostRequest(testRequestPayload, testUrl, Payload::class.java)
        }
    }

    private fun createSignedRequest(): SdkHttpFullRequest {
        val body = gson.toJson(testRequestPayload)

        return SdkHttpFullRequest.builder()
            .uri(URI(testUrl))
            .contentStreamProvider { StringInputStream(body) }
            .method(SdkHttpMethod.POST)
            .appendHeader(Header.ACCEPT, JSON_MIME_TYPE)
            .appendHeader(Header.CONTENT_TYPE, JSON_MIME_TYPE)
            .build()
    }

    private fun createHttpPostResponse(): CloseableHttpResponse {
        val body = gson.toJson(testResponsePayload)
        val mockedResponse: CloseableHttpResponse = mockk()
        val mockedHttpEntity: HttpEntity = mockk()

        every { mockedHttpEntity.content } returns body.byteInputStream()
        every { mockedResponse.entity } returns mockedHttpEntity
        justRun { mockedResponse.close() }

        return mockedResponse
    }

    private data class Payload(
        val value: String
    )
}
