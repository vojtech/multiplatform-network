package com.vhrdina.multiplatform.network.util

import com.vhrdina.multiplatform.network.model.Config
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.ios.Ios
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging

actual class HttpClientProvider actual constructor(val config: Config) {

    actual fun getHttpClient(): HttpClient {
        return if (config.mock && config.mockEngine != null) {
            geMockHttpClient()
        } else {
            getNetworkHttpClient()
        }
    }

    fun getNetworkHttpClient(): HttpClient {
        return HttpClient(Ios) {
            followRedirects = config.requestConfig.followRedirects
            expectSuccess = config.requestConfig.expectSuccess
            install(JsonFeature) {
                serializer = NetworkSerializer.kserializer
            }
            if (config.debug) {
                install(Logging) {
                    level = LogLevel.ALL
                }
            }
        }
    }

    fun geMockHttpClient(): HttpClient {
        return HttpClient(config.mockEngine!!) {
            followRedirects = config.requestConfig.followRedirects
            expectSuccess = config.requestConfig.expectSuccess
            install(JsonFeature) {
                serializer = NetworkSerializer.kserializer
            }
            if (config.debug) {
                install(Logging) {
                    level = LogLevel.ALL
                }
            }
        }
    }
}