package com.kamikaze.shareddevicemanager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.multidex.MultiDex
import com.kamikaze.shareddevicemanager.model.service.DeviceApplicationService
import com.kamikaze.shareddevicemanager.model.service.GroupApplicationService
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SharedDeviceManagerApplication : Application() {
    @Inject
    lateinit var authService: IAuthService

    @Inject
    lateinit var groupApplicationService: GroupApplicationService

    @Inject
    lateinit var deviceApplicationService: DeviceApplicationService

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(this)
        }
    }

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            private lateinit var applicationScope: CoroutineScope
            private var counter = 0

            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                counter++
                if (counter == 1) {
                    applicationScope = MainScope()
                    applicationScope.launch {
                        authService.initialize()
                    }

                    applicationScope.launch {
                        groupApplicationService.initialize()
                    }

                    applicationScope.launch {
                        deviceApplicationService.initialize()
                    }
                }
            }

            override fun onActivityDestroyed(p0: Activity) {
                counter--
                if (counter == 0) {
                    applicationScope.cancel()
                } else if (counter < 0) {
                    throw IllegalStateException()
                }
            }

            override fun onActivityPaused(p0: Activity) {}
            override fun onActivityStarted(p0: Activity) {}
            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
            override fun onActivityStopped(p0: Activity) {}
            override fun onActivityResumed(p0: Activity) {}

        })
    }
}

