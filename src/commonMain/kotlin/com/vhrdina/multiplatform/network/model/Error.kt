package com.vhrdina.multiplatform.network.model

import kotlinx.serialization.Serializable

@Serializable
class Error(val code: Int, val message: String?)