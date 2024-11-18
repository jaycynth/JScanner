package com.techne.jscanner.presentation.apkScanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techne.jscanner.data.model.ApkInfo
import com.techne.jscanner.data.repository.AppType
import com.techne.jscanner.domain.ApkInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApkScannerViewModel @Inject constructor(private val repository: ApkInfoRepository) : ViewModel() {
    private val _state = MutableStateFlow<ApkScannerState>(ApkScannerState.Idle)
    val state: StateFlow<ApkScannerState> = _state.asStateFlow()



    fun handleIntent(intent: ApkScannerIntent) {
        when (intent) {
            is ApkScannerIntent.ScanForApks -> scanForApks()
            is ApkScannerIntent.FlagExcessivePermissions -> flagPermissions(intent.apkInfo)
        }
    }

    fun scanForApks(filter: AppType = AppType.ALL) {
        viewModelScope.launch {
            _state.value = ApkScannerState.Loading

            try {
                val installedApps = repository.getInstalledApps(filter)
                val totalApps = installedApps.size
                var currentAppIndex = 0

                installedApps.forEach { app ->
                    val progress = ((currentAppIndex + 1) * 100) / totalApps
                    _state.value = ApkScannerState.Scanning(progress, app.appName)
                    currentAppIndex++
                    delay(50) // Simulate scanning delay
                }

                _state.value = ApkScannerState.Loaded(installedApps)

            } catch (e: Exception) {
                _state.value = ApkScannerState.Error("Failed to load apps: ${e.message}")
            }
        }
    }

    private fun flagPermissions(apkInfo: ApkInfo) {
        val flaggedPermissions = apkInfo.permissions.filter { permission ->
            // Define a list of "risky" permissions and flag them
            permission.contains("LOCATION") || permission.contains("SMS") ||
                    permission.contains("CALL_LOG") || permission.contains("CONTACTS")
        }
        _state.value = ApkScannerState.Loaded(
            listOf(apkInfo.copy(flaggedPermissions = flaggedPermissions))
        )
    }
}
