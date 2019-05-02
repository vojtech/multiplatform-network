package com.vhrdina.multiplatform.network

import com.vhrdina.multiplatform.network.model.Request
import com.vhrdina.multiplatform.network.model.Response

interface INetworkClient {

    fun <S, T : Request<T>> execute(request: T): CoroutineCallback<Response<S>>
}