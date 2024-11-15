package com.techne.jscanner.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.techne.jscanner.data.model.ApkInfo
import com.techne.jscanner.domain.ApkInfoRepository
import javax.inject.Inject

class ApkInfoRepositoryImpl @Inject constructor(private val context: Context) : ApkInfoRepository {

    override suspend fun getInstalledApps(): List<ApkInfo> {
        val packageManager = context.packageManager
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        return installedApps.map { app ->
            val packageInfo = packageManager.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS)
            val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()

            ApkInfo(
                packageName = app.packageName,
                appName = packageManager.getApplicationLabel(app).toString(),
                permissions = permissions
            )
        }
    }
}
