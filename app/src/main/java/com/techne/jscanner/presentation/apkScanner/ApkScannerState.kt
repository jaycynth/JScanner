package com.techne.jscanner.presentation.apkScanner

import com.techne.jscanner.data.model.ApkInfo

sealed class ApkScannerState {
    object Loading : ApkScannerState()
    data class Scanning(val progress: Int, val currentApp: String) : ApkScannerState()
    data class Loaded(val apps: List<ApkInfo>) : ApkScannerState()
    data class Error(val message: String) : ApkScannerState()
}
