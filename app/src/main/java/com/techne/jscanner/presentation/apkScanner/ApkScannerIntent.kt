package com.techne.jscanner.presentation.apkScanner

import com.techne.jscanner.data.model.ApkInfo

sealed class ApkScannerIntent {
    object ScanForApks : ApkScannerIntent()
    data class FlagExcessivePermissions(val apkInfo: ApkInfo) : ApkScannerIntent()
}
