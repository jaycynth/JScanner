package com.techne.jscanner.data.model

data class ApkInfo(
    val packageName: String,
    val appName: String,
    val permissions: List<String>,
    val flaggedPermissions: List<String> = emptyList()
)
