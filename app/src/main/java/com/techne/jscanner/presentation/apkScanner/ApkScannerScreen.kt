package com.techne.jscanner.presentation.apkScanner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techne.jscanner.data.model.ApkInfo
import com.techne.jscanner.data.repository.AppType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApkScannerScreen(viewModel: ApkScannerViewModel) {
    val state by viewModel.state.collectAsState()
    var selectedFilter by remember { mutableStateOf(AppType.ALL) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(Modifier.padding(16.dp)) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Text(
                text = "APK Scanner",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(text = "Select app type to scan:", style = MaterialTheme.typography.bodyMedium)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                TextField(
                    value = selectedFilter.name,
                    onValueChange = {},
                    label = { Text("App Type") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    AppType.entries.forEach { appType ->
                        DropdownMenuItem(

                            { Text(appType.name) },

                            onClick = {
                                selectedFilter = appType
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Start Scan Button
            Button(
                onClick = { viewModel.scanForApks(selectedFilter) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "Start Scanning")
            }

            // Handle different states
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
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { scanningState.progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
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
                    val apps =
                        (state as ApkScannerState.Loaded).apps.filter { it.flaggedPermissions.isNotEmpty() }
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
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

                ApkScannerState.Idle -> {}
            }
        }
    }
}

@Composable
fun ApkInfoItem(apkInfo: ApkInfo, onFlagPermissions: (ApkInfo) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "App: ${apkInfo.appName}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(text = "Package: ${apkInfo.packageName}", style = MaterialTheme.typography.bodyMedium)

        // Flagged permissions summary
        if (apkInfo.flaggedPermissions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Flagged Permissions",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${apkInfo.flaggedPermissions.size} Flagged Permission(s)",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Red)
                )
            }

            val permissionsToDisplay = if (expanded) apkInfo.flaggedPermissions else apkInfo.flaggedPermissions.take(3)
            permissionsToDisplay.forEach { permission ->
                Text(
                    text = "- $permission",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Red)
                )
            }

            if (apkInfo.flaggedPermissions.size > 3) {
                Text(
                    text = if (expanded) "See less" else "See more",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .padding(top = 8.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No excessive permissions flagged.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Green)
            )
        }

        // Flag Excessive Permissions Button
        Button(
            onClick = { onFlagPermissions(apkInfo) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Review Flagged Permissions")
        }
    }
}



