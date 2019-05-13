package com.vhrdina.multiplatform.network

import com.vhrdina.multiplatform.network.model.Config
import com.vhrdina.multiplatform.network.model.NetworkError
import com.vhrdina.multiplatform.network.model.Request
import com.vhrdina.multiplatform.network.model.Response
import com.vhrdina.multiplatform.network.util.HttpClientProvider
import com.vhrdina.multiplatform.network.util.NetworkSerializer
import com.vhrdina.multiplatform.network.util.RequestExecutor

class NetworkClient(val config: Config) {

    private val httpClientProvider = HttpClientProvider(config)

    init {
        NetworkSerializer.apply {
            setMapper(NetworkError::class, NetworkError.serializer())
            setMapper(Config::class, Config.serializer())
        }
    }

    @PublishedApi
    internal val requestExecutor =
        RequestExecutor(
            config = config,
            httpClient = httpClientProvider.getHttpClient()
        )

    inline fun <reified S> execute(request: Request): CoroutineCallback<Response<S>> {
        return requestExecutor.execute(request)
    }
}