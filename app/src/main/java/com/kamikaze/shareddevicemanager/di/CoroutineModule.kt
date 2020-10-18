package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.util.CoroutineContexts
import com.kamikaze.shareddevicemanager.util.ICoroutineContexts
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent


@InstallIn(ApplicationComponent::class)
@Module
abstract class CoroutineModule {
    @Binds
    abstract fun bindCoroutineContexts(coroutineContexts: CoroutineContexts): ICoroutineContexts
}