package com.vhrdina.network

import com.vhrdina.network.model.Config
import com.vhrdina.network.model.Request
import com.vhrdina.network.model.Response
import com.vhrdina.network.util.HttpClientProvider
import com.vhrdina.network.util.RequestExecutor

class NetworkClient(val config: Config) {

    private val httpClientProvider = HttpClientProvider(config)

    @PublishedApi
    internal val requestExecutor =
        RequestExecutor(config = config, httpClient = httpClientProvider.getHttpClient())

    inline fun <reified S, reified T : Request<T>> execute(request: T): CoroutineCallback<Response<S>> {
        return requestExecutor.execute(request)
    }
}