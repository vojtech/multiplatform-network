package com.vhrdina.network.model

import com.vhrdina.network.ApplicationDispatcher
import com.vhrdina.network.CoroutineCallback
import com.vhrdina.network.util.RequestExecutor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Transient
import kotlin.coroutines.CoroutineContext

class Request<T>(
    val method: String,
    val endpoint: String,
    val query: Map<String, String?>?,
    val headers: Map<String, String>?,
    val contentType: String,
    val body: Any?
)