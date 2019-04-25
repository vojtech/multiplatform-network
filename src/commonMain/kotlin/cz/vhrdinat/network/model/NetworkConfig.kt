package cz.vhrdinat.network.model

class NetworkConfig(val host: String,
                    val headers: Map<String, String>?,
                    val contentType: String,
                    val followRedirects: Boolean = false,
                    val expectSuccess: Boolean = false)
