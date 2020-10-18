package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.model.service.firebase.FirebaseAuthService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
abstract class FirebaseServiceModule {
    @Binds
    abstract fun bindAuthService(repo: FirebaseAuthService): IAuthService
}