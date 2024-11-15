package com.techne.jscanner.di

import com.techne.jscanner.data.repository.ApkInfoRepositoryImpl
import com.techne.jscanner.domain.ApkInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindApkScannerRepository(apkInfoRepository: ApkInfoRepositoryImpl): ApkInfoRepository




}