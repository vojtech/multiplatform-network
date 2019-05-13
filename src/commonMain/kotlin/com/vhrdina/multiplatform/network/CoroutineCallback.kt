package com.vhrdina.multiplatform.network

import com.vhrdina.multiplatform.network.model.NetworkError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.serialization.Transient

class CoroutineCallback<T> {

    private val results = arrayListOf<T>()
    private val errors = arrayListOf<NetworkError>()

    @Transient
    @PublishedApi
    internal val errorHandlerException = CoroutineExceptionHandler { _, throwable ->
        println("RequestExecutor -> ErrorHandlerException: $throwable")
        sendError(NetworkError(code = -1, message = throwable.message), false)
    }

    @Transient
    var onReceive: (T?) -> Unit = {}

    @Transient
    var onError: (NetworkError?) -> Unit = {}

    fun send(item: T, postpone: Boolean = false) {
        results.add(item)
        if (!postpone) notifyResultReceiver(item)
    }

    fun executePendingResults() {
        repeat(results.size) {
            notifyResultReceiver(results[it])
        }
    }

    private fun notifyResultReceiver(item: T) {
        onReceive.invoke(item)
        results.remove(item)
    }

    fun sendError(item: NetworkError, postpone: Boolean = false) {
        errors.add(item)
        if (!postpone) notifyErrorReceiver(item)
    }

    fun executePendingErrors() {
        repeat(errors.size) {
            notifyErrorReceiver(errors[it])
        }
    }

    private fun notifyErrorReceiver(item: NetworkError) {
        onError.invoke(item)
        errors.remove(item)
    }

    fun clear() {
        results.clear()
        errors.clear()
    }
}