package cz.vhrdinat.network

import cz.vhrdinat.network.model.Config
import cz.vhrdinat.network.model.Request
import cz.vhrdinat.network.model.Response
import cz.vhrdinat.network.util.HttpClientProvider
import cz.vhrdinat.network.util.RequestExecutor

class NetworkClient(val config: Config) {

    private val httpClientProvider = HttpClientProvider(config)

    @PublishedApi
    internal val requestExecutor =
        RequestExecutor(config = config, httpClient = httpClientProvider.getHttpClient())

    inline fun <reified S, reified T : Request<T>> execute(request: T): CoroutineCallback<Response<S>> {
        return requestExecutor.execute(request)
    }
}