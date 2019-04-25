package cz.vhrdinat.network

import cz.vhrdinat.network.model.Request
import cz.vhrdinat.network.model.Response

interface INetworkClient {

    fun <S, T : Request<T>> execute(request: T): CoroutineCallback<Response<S>>
}