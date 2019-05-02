package com.vhrdina.multiplatform.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


internal actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Default

internal actual val UIDispatcher: CoroutineDispatcher = Dispatchers.Main