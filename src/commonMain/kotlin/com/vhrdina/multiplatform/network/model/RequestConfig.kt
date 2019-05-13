package com.vhrdina.multiplatform.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RequestConfig(var host: String,
                    var headers: Map<String, String>? = null,
                    var contentType: String = "application/json",
                    var followRedirects: Boolean = false,
                    var expectSuccess: Boolean = false)
