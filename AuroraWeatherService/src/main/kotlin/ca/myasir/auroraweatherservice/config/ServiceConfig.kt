package ca.myasir.auroraweatherservice.config

import ca.myasir.auroraweatherservice.util.InstantJsonAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ServiceConfig {

    @Bean
    fun gson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Instant::class.java, InstantJsonAdapter())
            .create()
    }
}
