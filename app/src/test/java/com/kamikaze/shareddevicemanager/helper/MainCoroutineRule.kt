package com.kamikaze.shareddevicemanager.helper

import com.kamikaze.shareddevicemanager.util.ICoroutineContexts
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

class MainCoroutineRule : TestWatcher(), TestCoroutineScope by TestCoroutineScope(),
    ICoroutineContexts {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }

    override val io: CoroutineContext
        get() = this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher

    override val default: CoroutineContext
        get() = this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
}