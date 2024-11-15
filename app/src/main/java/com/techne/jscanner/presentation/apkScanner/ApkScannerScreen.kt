package com.techne.jscanner.presentation.apkScanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.techne.jscanner.data.model.ApkInfo

@Composable
fun ApkScannerScreen(viewModel: ApkScannerViewModel) {
    val state by viewModel.state.collectAsState()

    when (state) {
        is ApkScannerState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        is ApkScannerState.Scanning -> {
            val scanningState = state as ApkScannerState.Scanning
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { scanningState.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    text = "Scanning ${scanningState.currentApp}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(8.dp)
                )

                Text(
                    text = "${scanningState.progress}% completed",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        is ApkScannerState.Loaded -> {
            val apps = (state as ApkScannerState.Loaded).apps
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(apps) { app ->
                    ApkInfoItem(app) { apk ->
                        viewModel.handleIntent(ApkScannerIntent.FlagExcessivePermissions(apk))
                    }
                }
            }
        }

        is ApkScannerState.Error -> {
            val errorState = state as ApkScannerState.Error
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${errorState.message}",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color(0x80FF0000))
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}


@Composable
fun ApkInfoItem(apkInfo: ApkInfo, onFlagPermissions: (ApkInfo) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "App: ${apkInfo.appName}")
        Text(text = "Package: ${apkInfo.packageName}")
        Text(text = "Permissions:")
        apkInfo.permissions.forEach { permission ->
            Text(text = permission)
        }
        if (apkInfo.flaggedPermissions.isNotEmpty()) {
            Text("Flagged Permissions:", color = Color.Red)
            apkInfo.flaggedPermissions.forEach { flagged ->
                Text(text = flagged, color = Color.Red)
            }
        }
        Button(onClick = { onFlagPermissions(apkInfo) }) {
            Text("Flag Excessive Permissions")
        }
    }
}
