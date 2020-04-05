package com.kamikaze.shareddevicemanager.util

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class CoroutineContexts @Inject constructor(): ICoroutineContexts {
    override val io: CoroutineContext = Dispatchers.IO
    override val default: CoroutineContext = Dispatchers.Default
}