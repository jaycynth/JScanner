package com.techne.jscanner.domain

import com.techne.jscanner.data.model.ApkInfo
import com.techne.jscanner.data.repository.AppType

interface ApkInfoRepository {
    suspend fun getInstalledApps(appType: AppType = AppType.ALL): List<ApkInfo>
}


