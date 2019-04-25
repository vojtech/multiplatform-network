package cz.vhrdinat.network.model

class Response<T> constructor(val result: T? = null,
                              val headers: Map<String, List<String>>? = null,
                              val responseCode: Int? = null,
                              val error: Error? = null)