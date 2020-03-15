package com.kamikaze.shareddevicemanager

import android.content.Context
import android.os.Build
import androidx.multidex.MultiDex
import com.kamikaze.shareddevicemanager.di.DaggerApplicationComponent
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedDeviceManagerApplication: DaggerApplication() {
    @Inject
    lateinit var authService: IAuthService
    @Inject
    lateinit var groupRepository: IGroupRepository
    @Inject
    lateinit var deviceRepository: IDeviceRepository


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(this)
        }
    }

    override fun onCreate() {
        super.onCreate()

        // TODO アプリケーションクラスの外に出す(MainViewModelあたり)
        GlobalScope.launch {
            authService.authStateFlow.collect {
                if (it == AuthState.SIGN_OUT) {
                    deviceRepository.setGroupId(null)
                }
            }
        }

        GlobalScope.launch {
            // TODO サインアップ後の初期化処理はCloudFunctionに実行させる
            authService.userFlow.collect {
                val user = it ?: return@collect
                var group = groupRepository.get(user.id)

                if (group == null) {
                    groupRepository.add(
                        Group(
                            name = user.name,
                            owner = user.id,
                            default = true
                        )
                    )
                    group = groupRepository.get(user.id)
                    require(group != null)
                }

                deviceRepository.setGroupId(group.id)
            }
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(applicationContext)
    }
}
