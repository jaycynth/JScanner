package com.techne.jscanner.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.techne.jscanner.data.model.ApkInfo
import com.techne.jscanner.domain.ApkInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class AppType {
    ALL,
    SYSTEM_APPS,
    THIRD_PARTY
}

class ApkInfoRepositoryImpl @Inject constructor(private val context: Context) : ApkInfoRepository {

    private val sensitivePermissions = listOf(
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.RECORD_AUDIO",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.CAMERA",
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.READ_CALENDAR",
        "android.permission.WRITE_CALENDAR",
        "android.permission.READ_CALL_LOG",
        "android.permission.WRITE_CALL_LOG"
    )

    private fun isAppOfType(app: ApplicationInfo, appType: AppType): Boolean {
        return when (appType) {
            AppType.SYSTEM_APPS -> (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            AppType.THIRD_PARTY -> (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0
            AppType.ALL -> true
        }
    }
    override suspend fun getInstalledApps(appType: AppType): List<ApkInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val installedApps = try {
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        } catch (e: Exception) {
            emptyList<ApplicationInfo>()
        }

        installedApps.mapNotNull { app ->
            try {
                if (!isAppOfType(app, appType)) return@mapNotNull null

                val packageInfo = packageManager.getPackageInfo(
                    app.packageName,
                    PackageManager.GET_PERMISSIONS
                )

                val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
                val flaggedPermissions = permissions.filter { it in sensitivePermissions }

                ApkInfo(
                    packageName = app.packageName,
                    appName = packageManager.getApplicationLabel(app).toString(),
                    permissions = permissions,
                    flaggedPermissions = flaggedPermissions
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            } catch (e: Exception) {
                null
            }
        }
    }

}