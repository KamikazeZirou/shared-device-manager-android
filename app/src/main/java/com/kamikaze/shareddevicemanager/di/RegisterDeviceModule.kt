package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.register.RegisterDeviceFragment
import com.kamikaze.shareddevicemanager.ui.register.RegisterDeviceViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@Module
abstract class RegisterDeviceModule {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun registerDeviceFragment(): RegisterDeviceFragment

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Binds
    @IntoMap
    @ViewModelKey(RegisterDeviceViewModel::class)
    abstract fun bindViewModel(viewModel: RegisterDeviceViewModel): ViewModel
}