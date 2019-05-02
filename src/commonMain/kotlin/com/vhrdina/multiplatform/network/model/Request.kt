package com.vhrdina.multiplatform.network.model

class Request<T>(
    val method: String,
    val endpoint: String,
    val query: Map<String, String?>?,
    val headers: Map<String, String>?,
    val contentType: String,
    val body: Any?
)