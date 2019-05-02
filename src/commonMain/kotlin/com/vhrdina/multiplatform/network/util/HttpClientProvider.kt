package com.vhrdina.multiplatform.network.util

import com.vhrdina.multiplatform.network.model.Config
import io.ktor.client.HttpClient

expect class HttpClientProvider constructor(config: Config) {

    fun getHttpClient(): HttpClient
}