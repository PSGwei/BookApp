package com.plcoding.bookpedia.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// HttpClientEngine is dependent on platform

object HttpClientFactory {
    fun create(engine:HttpClientEngine): HttpClient{
        // configures various Ktor plugins
        return HttpClient(engine){
            // Tells Ktor how to handle serialization and deserialization of request/response bodies
            install(ContentNegotiation){
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(HttpTimeout){
                socketTimeoutMillis = 20_00L
                requestTimeoutMillis = 20_00L
            }
            // Logs all HTTP activity
            install(Logging){
                logger = object : Logger{
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
            // Ensures that every request made using this HttpClient has the Content-Type: application/json header by default.
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}