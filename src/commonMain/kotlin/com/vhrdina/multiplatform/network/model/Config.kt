package com.vhrdina.multiplatform.network.model

import io.ktor.client.engine.mock.MockEngine
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Config constructor(var requestConfig: RequestConfig,
                         var debug: Boolean = false,
                         var mock: Boolean = false) {

    @Transient
    var mockEngine: MockEngine? = null

}