package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.main.mydevice.MyDeviceFragment
import com.kamikaze.shareddevicemanager.ui.main.mydevice.MyDeviceViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MyDeviceModule {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun myDeviceFragment(): MyDeviceFragment

    @Binds
    @IntoMap
    @ViewModelKey(MyDeviceViewModel::class)
    abstract fun bindViewModel(viewmodel: MyDeviceViewModel): ViewModel
}
