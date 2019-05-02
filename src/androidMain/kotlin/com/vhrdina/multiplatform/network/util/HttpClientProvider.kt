package com.vhrdina.multiplatform.network.util

import com.vhrdina.multiplatform.network.model.Config
import com.vhrdina.multiplatform.network.util.NetworkSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging

actual class HttpClientProvider actual constructor(val config: Config) {

    actual fun getHttpClient(): HttpClient {
        val engine: HttpClientEngineFactory<*> = if (config.mock) MockEngine else OkHttp
        return HttpClient(engine) {
            followRedirects = config.requestConfig.followRedirects
            expectSuccess = config.requestConfig.expectSuccess
            install(JsonFeature) {
                serializer = NetworkSerializer.serializer
            }
            if (config.debug) {
                install(Logging) {
                    level = LogLevel.ALL
                }
            }
        }
    }
}