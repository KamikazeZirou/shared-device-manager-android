package com.kamikaze.shareddevicemanager.util

import kotlin.coroutines.CoroutineContext

interface ICoroutineContexts {
    val io: CoroutineContext
    val default: CoroutineContext
}