package com.vhrdina.network.util

import com.vhrdina.network.model.Config
import io.ktor.client.HttpClient

expect class HttpClientProvider constructor(config: Config) {

    fun getHttpClient(): HttpClient
}