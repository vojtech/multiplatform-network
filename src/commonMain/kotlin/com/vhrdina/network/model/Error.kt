package com.vhrdina.network.model

import kotlinx.serialization.Serializable

@Serializable
class Error(val code: Int, val message: String?)