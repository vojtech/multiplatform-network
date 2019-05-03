package com.vhrdina.multiplatform.network

import com.vhrdina.multiplatform.network.model.Error

class CoroutineCallback<T> {

    private val results = arrayListOf<T>()
    private val errors = arrayListOf<Error>()

    var onReceive: (T?) -> Unit = {}

    var onError: (Error?) -> Unit = {}

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

    fun sendError(item: Error, postpone: Boolean = false) {
        errors.add(item)
        if (!postpone) notifyErrorReceiver(item)
    }

    fun executePendingErrors() {
        repeat(errors.size) {
            notifyErrorReceiver(errors[it])
        }
    }

    private fun notifyErrorReceiver(item: Error) {
        onError.invoke(item)
        errors.remove(item)
    }

    fun clear() {
        results.clear()
        errors.clear()
    }
}