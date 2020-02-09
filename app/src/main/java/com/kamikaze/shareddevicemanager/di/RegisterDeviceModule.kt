package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.register.RegisterDeviceFragment
import com.kamikaze.shareddevicemanager.ui.register.RegisterDeviceViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class RegisterDeviceModule {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun registerDeviceFragment(): RegisterDeviceFragment

    @Binds
    @IntoMap
    @ViewModelKey(RegisterDeviceViewModel::class)
    abstract fun bindViewModel(viewmodel: RegisterDeviceViewModel): ViewModel
}