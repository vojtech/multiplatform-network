package cz.vhrdinat.network.model

import cz.vhrdinat.network.ApplicationDispatcher
import cz.vhrdinat.network.CoroutineCallback
import cz.vhrdinat.network.util.RequestExecutor
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