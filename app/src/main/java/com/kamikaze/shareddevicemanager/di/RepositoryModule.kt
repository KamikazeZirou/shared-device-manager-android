package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import com.kamikaze.shareddevicemanager.model.repository.firestore.FirestoreDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.firestore.FirestoreGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.firestore.FirestoreMemberRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindGroupRepository(repo: FirestoreGroupRepository): IGroupRepository

    @Binds
    abstract fun bindMemberRepository(repo: FirestoreMemberRepository): IMemberRepository

    @Binds
    abstract fun bindDeviceRepository(repo: FirestoreDeviceRepository): IDeviceRepository
}