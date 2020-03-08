package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.repository.FirestoreGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.FirestoreMemberRepository
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import dagger.Binds
import dagger.Module

@Module
abstract class GroupModule {
    @Binds
    abstract fun bindGroupRepository(repo: FirestoreGroupRepository): IGroupRepository

    @Binds
    abstract fun bindMemberRepository(repo: FirestoreMemberRepository): IMemberRepository
}