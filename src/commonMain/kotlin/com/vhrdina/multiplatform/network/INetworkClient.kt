package com.vhrdina.multiplatform.network

import com.vhrdina.multiplatform.network.model.Request
import com.vhrdina.multiplatform.network.model.Response

interface INetworkClient {

    fun <S> execute(request: Request): CoroutineCallback<Response<S>>
}