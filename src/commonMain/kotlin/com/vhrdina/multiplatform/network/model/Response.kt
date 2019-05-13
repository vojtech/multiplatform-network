package com.vhrdina.multiplatform.network.model

data class Response<T> constructor(var result: T? = null,
                                   var headers: Map<String, List<String>>? = null,
                                   var responseCode: Int? = null,
                                   var networkError: NetworkError? = null)