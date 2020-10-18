package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.data.MyDeviceBuilder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
abstract class DeviceModule {
    @Binds
    abstract fun bindDeviceBuilder(deviceBuilder: MyDeviceBuilder): IMyDeviceBuilder
}