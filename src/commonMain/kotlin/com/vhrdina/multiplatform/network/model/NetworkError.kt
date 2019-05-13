package com.vhrdina.multiplatform.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkError(val code: Int, val message: String?)