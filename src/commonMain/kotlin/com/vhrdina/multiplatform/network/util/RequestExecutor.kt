package com.vhrdina.multiplatform.network.util

import com.vhrdina.multiplatform.network.ApplicationDispatcher
import com.vhrdina.multiplatform.network.CoroutineCallback
import com.vhrdina.multiplatform.network.model.Config
import com.vhrdina.multiplatform.network.model.NetworkError
import com.vhrdina.multiplatform.network.model.Request
import com.vhrdina.multiplatform.network.model.Response
import io.ktor.client.HttpClient
import io.ktor.client.call.TypeInfo
import io.ktor.client.call.typeInfo
import io.ktor.client.request.*
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.takeFrom
import io.ktor.util.toMap
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Transient
import kotlin.coroutines.CoroutineContext

class RequestExecutor constructor(val config: Config, val httpClient: HttpClient) : CoroutineScope {

    @Transient
    private val job = Job()

    @Transient
    override val coroutineContext: CoroutineContext
        get() = job + ApplicationDispatcher

    inline fun <reified S> execute(request: Request): CoroutineCallback<Response<S>> {
        return when (HttpMethod.parse(request.method)) {
            HttpMethod.Get -> executeGet(request)
            HttpMethod.Post -> executePost(request)
            HttpMethod.Put -> executePut(request)
            HttpMethod.Delete -> executeDelete(request)
            HttpMethod.Options -> executeOptions(request)
            HttpMethod.Head -> executeHead(request)
            else -> createUnsupportedMethodResponse()
        }
    }

    @PublishedApi
    internal inline fun <reified S> createUnsupportedMethodResponse(): CoroutineCallback<Response<S>> {
        return CoroutineCallback<Response<S>>()
            .apply {
                sendError(NetworkError(1000, "Unsupported Http Method"))
            }
    }

    @PublishedApi
    internal inline fun <reified S> executeGet(request: Request): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext+coroutineCallback.errorHandlerException) {
            coroutineCallback.send(get(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal inline fun <reified S> executePost(request: Request): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext+coroutineCallback.errorHandlerException) {
            val response = post<S>(request)
            coroutineCallback.send(response)
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal inline fun <reified S> executePut(request: Request): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext+coroutineCallback.errorHandlerException) {
            coroutineCallback.send(put(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal inline fun <reified S> executeDelete(request: Request): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext+coroutineCallback.errorHandlerException) {
            coroutineCallback.send(delete(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal inline fun <reified S> executeOptions(request: Request): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext+coroutineCallback.errorHandlerException) {
            coroutineCallback.send(options(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal inline fun <reified S> executeHead(request: Request): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext+coroutineCallback.errorHandlerException) {
            coroutineCallback.send(head(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal suspend inline fun <reified S> get(request: Request): Response<S> {
        return httpClient.get<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal suspend inline fun <reified S> post(request: Request): Response<S> {
        return httpClient.post<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal suspend inline fun <reified S> put(request: Request): Response<S> {
        return httpClient.put<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal suspend inline fun <reified S> delete(request: Request): Response<S> {
        return httpClient.delete<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal suspend inline fun <reified S> options(request: Request): Response<S> {
        return httpClient.options<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal suspend inline fun <reified S> head(request: Request): Response<S> {
        return httpClient.head<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal fun HttpRequestBuilder.buildRequest(request: Request) {
        url { takeFrom("${config.requestConfig.host}/${request.endpoint}") }
        request.query?.forEach {
            parameter(it.key, it.value)
        }
        headers.apply {
            append(HttpHeaders.ContentType, request.contentType ?: config.requestConfig.contentType)
            config.requestConfig.headers?.forEach {
                append(it.key, it.value)
            }
            request.headers?.forEach {
                remove(it.key)
                append(it.key, it.value)
            }
        }
        request.body?.let { body = it }
    }

    @PublishedApi
    internal suspend inline fun <reified S> HttpResponse.processResponse(): Response<S> {
        val responseCode = status
        val headers = headers.toMap()

        val response = Response<S>().apply {
            this.headers = headers
            this.responseCode = responseCode.value
        }

        if (responseCode == HttpStatusCode.OK) {
            try {
                response.result = NetworkSerializer.kserializer.read(typeInfo<S>(), this) as? S
            } catch (e: Exception) {
                response.networkError = NetworkError(code = -1, message = e.message)
            }
        } else {
            try {
                response.networkError = NetworkSerializer.kserializer.read(typeInfo<NetworkError>(), this) as? NetworkError
            } catch (e: Exception) {
                response.networkError = NetworkError(code = -1, message = e.message)
            }
        }

        return response
    }
}
