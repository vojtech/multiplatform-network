package com.vhrdina.multiplatform.network.model

class RequestConfig(val host: String,
                    val headers: Map<String, String>? = null,
                    val contentType: String = "application/json",
                    val followRedirects: Boolean = false,
                    val expectSuccess: Boolean = false)
