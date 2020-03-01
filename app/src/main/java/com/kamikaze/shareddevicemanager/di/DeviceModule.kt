package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.data.MyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.repository.FirestoreDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import dagger.Binds
import dagger.Module

@Module
abstract class DeviceModule {
    @Binds
    abstract fun bindDeviceRepository(repo: FirestoreDeviceRepository): IDeviceRepository

    @Binds
    abstract fun bindDeviceBuilder(deviceBuilder: MyDeviceBuilder): IMyDeviceBuilder
}