package com.kamikaze.shareddevicemanager.di

import android.content.Context
import com.kamikaze.shareddevicemanager.SharedDeviceManagerApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AuthModule::class,
        DeviceModule::class,
        MainModule::class,
        DeviceListModule::class,
        DeviceDetailModule::class,
        MyDeviceModule::class,
        RegisterDeviceModule::class,
        BorrowDeviceModule::class
    ])
interface ApplicationComponent : AndroidInjector<SharedDeviceManagerApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }
}