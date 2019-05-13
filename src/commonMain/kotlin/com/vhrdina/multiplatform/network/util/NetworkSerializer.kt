package com.vhrdina.multiplatform.network.util

import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

@ThreadLocal
object NetworkSerializer {

    private val mappers: MutableMap<KClass<*>, KSerializer<*>> = mutableMapOf()
    private val listMappers: MutableMap<KClass<*>, KSerializer<*>> = mutableMapOf()

    val kserializer: KotlinxSerializer = KotlinxSerializer(Json.nonstrict)

    fun <T : Any> setMapper(type: KClass<T>, serializer: KSerializer<T>) {
        @Suppress("UNCHECKED_CAST")
        mappers[type as KClass<Any>] = serializer as KSerializer<Any>
        kserializer.setMapper(type, serializer)
    }


    fun <T : Any> setListMapper(type: KClass<T>, serializer: KSerializer<T>) {
        @Suppress("UNCHECKED_CAST")
        listMappers[type] = serializer.list as KSerializer<List<Any>>
        kserializer.setListMapper(type, serializer)
    }

}