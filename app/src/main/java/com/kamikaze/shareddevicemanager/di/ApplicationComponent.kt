package com.kamikaze.shareddevicemanager.di

import android.content.Context
import com.kamikaze.shareddevicemanager.SharedDeviceManagerApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        FirebaseServiceModule::class,
        DeviceModule::class,
        MainModule::class,
        DevicesModule::class,
        FirestoreRepositoryModule::class,
        MembersModule::class,
        GroupsModule::class,
        DeviceDetailModule::class,
        MyDeviceModule::class,
        RegisterDeviceModule::class,
        BorrowDeviceModule::class,
        CoroutineModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<SharedDeviceManagerApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }
}