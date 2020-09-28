package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.model.service.IGroupService
import com.kamikaze.shareddevicemanager.model.service.firebase.FirebaseAuthService
import com.kamikaze.shareddevicemanager.model.service.firebase.FirebaseGroupService
import dagger.Binds
import dagger.Module

@Module
abstract class FirebaseServiceModule {
    @Binds
    abstract fun bindAuthService(repo: FirebaseAuthService): IAuthService

    @Binds
    abstract fun bindGroupService(repo: FirebaseGroupService): IGroupService
}