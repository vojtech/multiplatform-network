package com.vhrdina.network

import com.vhrdina.network.model.Request
import com.vhrdina.network.model.Response

interface INetworkClient {

    fun <S, T : Request<T>> execute(request: T): CoroutineCallback<Response<S>>
}