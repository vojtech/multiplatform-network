package com.vhrdina.network.util

import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object NetworkSerializer {
    val serializer: JsonSerializer = KotlinxSerializer()
}