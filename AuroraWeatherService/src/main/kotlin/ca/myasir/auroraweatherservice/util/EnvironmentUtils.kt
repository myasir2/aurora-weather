package ca.myasir.auroraweatherservice.util

object EnvironmentUtils {

    fun extractEnvironmentVariable(name: String): String {
        return System.getenv(name) ?: throw RuntimeException("Environment variable $name not found")
    }
}
