package com.kamikaze.shareddevicemanager

import android.content.Context
import android.os.Build
import androidx.multidex.MultiDex
import com.kamikaze.shareddevicemanager.di.DaggerApplicationComponent
import com.kamikaze.shareddevicemanager.model.service.DeviceService
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class SharedDeviceManagerApplication: DaggerApplication() {
    @Inject
    lateinit var deviceService: DeviceService

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(this)
        }
    }

    override fun onCreate() {
        super.onCreate()

        GlobalScope.launch {
            deviceService.initialize()
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(applicationContext)
    }
}
