package com.vhrdina.multiplatform.network.model

class Request(
    val method: String,
    val endpoint: String,
    val query: Map<String, String?>? = null,
    val headers: Map<String, String>? = null,
    val contentType: String,
    val body: Any?
)