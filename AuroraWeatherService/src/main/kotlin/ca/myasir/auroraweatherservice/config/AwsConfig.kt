package ca.myasir.auroraweatherservice.config

import ca.myasir.auroraweatherservice.logger
import ca.myasir.auroraweatherservice.util.EnvironmentUtils
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.signer.Aws4Signer
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.apigateway.ApiGatewayClient
import software.amazon.awssdk.services.location.LocationClient

@Component
class AwsConfig {

    @Bean
    fun getAwsRegion(): Region {
        val regionName = EnvironmentUtils.extractEnvironmentVariable("AWS_REGION")

        logger.info("Identified region from environment variables: {}", regionName)

        return Region.of(regionName)
    }

    @Bean
    fun getApiGatewayClient(region: Region): ApiGatewayClient {
        return ApiGatewayClient.builder()
            .region(region)
            .build()

    }

    @Bean
    fun getLocationClient(region: Region): LocationClient {
        return LocationClient.builder()
            .region(region)
            .build()
    }

    @Bean
    fun getApiGatewaySigner(): Aws4Signer = Aws4Signer.create()

    @Bean
    fun getApiGatewaySignerParams(region: Region): Aws4SignerParams {
        return Aws4SignerParams.builder()
            .awsCredentials(DefaultCredentialsProvider.create().resolveCredentials())
            .signingRegion(region)
            .signingName("execute-api")
            .build()
    }
}
