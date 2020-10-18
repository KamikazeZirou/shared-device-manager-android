package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import com.kamikaze.shareddevicemanager.model.repository.firestore.FirestoreDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.firestore.FirestoreGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.firestore.FirestoreMemberRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.model.service.firebase.FirebaseAuthService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
abstract class FirebaseModule {
    @Binds
    abstract fun bindGroupRepository(repo: FirestoreGroupRepository): IGroupRepository

    @Binds
    abstract fun bindMemberRepository(repo: FirestoreMemberRepository): IMemberRepository

    @Binds
    abstract fun bindDeviceRepository(repo: FirestoreDeviceRepository): IDeviceRepository

    @Binds
    abstract fun bindAuthService(repo: FirebaseAuthService): IAuthService
}