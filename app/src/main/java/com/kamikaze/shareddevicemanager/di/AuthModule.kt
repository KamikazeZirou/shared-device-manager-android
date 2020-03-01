package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.auth.FirebaseAuthService
import com.kamikaze.shareddevicemanager.model.auth.IAuthService
import dagger.Binds
import dagger.Module

@Module
abstract class AuthModule {
    @Binds
    abstract fun bindAuthService(repo: FirebaseAuthService): IAuthService
}