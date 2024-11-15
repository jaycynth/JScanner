package com.techne.jscanner.domain

import com.techne.jscanner.data.model.ApkInfo

interface ApkInfoRepository {
    suspend fun getInstalledApps(): List<ApkInfo>
}


