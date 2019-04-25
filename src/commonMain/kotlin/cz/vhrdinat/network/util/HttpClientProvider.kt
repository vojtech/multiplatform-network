package cz.vhrdinat.network.util

import cz.vhrdinat.network.model.Config
import io.ktor.client.HttpClient

expect class HttpClientProvider constructor(config: Config) {

    fun getHttpClient(): HttpClient
}