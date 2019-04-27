package com.vhrdina.network.util

import com.vhrdina.network.model.Config
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.ios.Ios
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging

actual class HttpClientProvider actual constructor(val config: Config) {

    actual fun getHttpClient(): HttpClient {
        val engine: HttpClientEngineFactory<*> = if(config.mock) MockEngine else Ios
        return HttpClient(engine) {
            followRedirects = config.networkConfig.followRedirects
            expectSuccess = config.networkConfig.expectSuccess
            install(JsonFeature) {
                serializer = NetworkSerializer.serializer
            }
            if(config.debug) {
                install(Logging) {
                    level = LogLevel.ALL
                }
            }
        }
    }
}