package com.vhrdina.multiplatform.network.util

import com.vhrdina.multiplatform.network.ApplicationDispatcher
import com.vhrdina.multiplatform.network.CoroutineCallback
import com.vhrdina.multiplatform.network.model.Config
import com.vhrdina.multiplatform.network.model.Error
import com.vhrdina.multiplatform.network.model.Request
import com.vhrdina.multiplatform.network.model.Response
import io.ktor.client.HttpClient
import io.ktor.client.call.typeInfo
import io.ktor.client.request.*
import io.ktor.client.response.HttpResponse
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
    private val errorHandlerException = CoroutineExceptionHandler { _, throwable ->
        println(throwable)
    }

    @Transient
    override val coroutineContext: CoroutineContext
        get() = job + ApplicationDispatcher + errorHandlerException

    inline fun <reified S, reified T : Request<T>> execute(request: T): CoroutineCallback<Response<S>> {
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
            sendError(Error(1000, "Unsupported Http Method"))
        }
    }

    @PublishedApi
    internal inline fun <reified S, reified T : Request<T>> executeGet(request: T): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext) {
            coroutineCallback.send(get(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal inline fun <reified S, reified T : Request<T>> executePost(request: T): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext) {
            coroutineCallback.send(post(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal inline fun <reified S, reified T : Request<T>> executePut(request: T): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext) {
            coroutineCallback.send(put(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal inline fun <reified S, reified T : Request<T>> executeDelete(request: T): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext) {
            coroutineCallback.send(delete(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal inline fun <reified S, reified T : Request<T>> executeOptions(request: T): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext) {
            coroutineCallback.send(options(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal inline fun <reified S, reified T : Request<T>> executeHead(request: T): CoroutineCallback<Response<S>> {
        val coroutineCallback =
            CoroutineCallback<Response<S>>()
        launch(coroutineContext) {
            coroutineCallback.send(head(request))
        }.invokeOnCompletion {
            coroutineCallback.clear()
        }
        return coroutineCallback
    }

    @PublishedApi
    internal suspend inline fun <reified S, reified T : Request<T>> get(request: T): Response<S> {
        return httpClient.get<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal suspend inline fun <reified S, reified T : Request<T>> post(request: T): Response<S> {
        return httpClient.post<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal suspend inline fun <reified S, reified T : Request<T>> put(request: T): Response<S> {
        return httpClient.put<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal suspend inline fun <reified S, reified T : Request<T>> delete(request: T): Response<S> {
        return httpClient.delete<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal suspend inline fun <reified S, reified T : Request<T>> options(request: T): Response<S> {
        return httpClient.options<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal suspend inline fun <reified S, reified T : Request<T>> head(request: T): Response<S> {
        return httpClient.head<HttpResponse> { buildRequest(request) }.processResponse()
    }

    @PublishedApi
    internal inline fun <reified T : Request<T>> HttpRequestBuilder.buildRequest(request: T) {
        url { takeFrom("${config.requestConfig.host}/${request.endpoint}") }
        request.query?.entries?.iterator()?.forEach {
            parameter(it.key, it.value)
        }
    }

    @PublishedApi
    internal suspend inline fun <reified T> HttpResponse.processResponse(): Response<T> {
        val responseCode = status
        val headers = headers.toMap()

        return if (responseCode == HttpStatusCode.OK) {
            try {
                val data = NetworkSerializer.serializer.read(typeInfo<T>(), this) as? T
                Response(
                    result = data,
                    headers = headers,
                    responseCode = responseCode.value
                )
            } catch (e: Exception) {
                Response<T>(
                    headers = headers,
                    responseCode = responseCode.value,
                    error = Error(code = -1, message = e.message)
                )
            }
        } else {
            try {
                Response<T>(
                    headers = headers,
                    responseCode = responseCode.value,
                    error = NetworkSerializer.serializer.read(
                        typeInfo<Error>(),
                        this
                    ) as? Error
                )
            } catch (e: Exception) {
                Response<T>(
                    headers = headers,
                    responseCode = responseCode.value,
                    error = Error(code = -1, message = e.message)
                )
            }
        }
    }

}