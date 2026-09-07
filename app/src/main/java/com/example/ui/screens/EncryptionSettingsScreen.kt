package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AuditLogEntity
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber500
import com.example.ui.theme.AmberBorder
import com.example.ui.theme.Amber950
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.BorderProminent
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.EmeraldBadgeBg
import com.example.ui.theme.EmeraldBadgeBorder
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.Emerald950
import com.example.ui.theme.Obsidian700
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian850
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.Rose400
import com.example.ui.theme.Rose500
import com.example.ui.theme.RoseBorder
import com.example.ui.theme.Rose950
import com.example.ui.theme.SurfaceHeroFrom
import com.example.ui.theme.SurfaceHeroTo
import com.example.ui.theme.SurfaceTile
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc500
import com.example.ui.theme.Zinc600
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800
import com.example.ui.theme.Zinc900

@Composable
fun EncryptionSettingsScreen(
    keyFingerprint: String,
    lastRotation: String,
    biometricsEnabled: Boolean,
    requirePasscodeForSensitive: Boolean,
    fallbackToPasscode: Boolean,
    fallbackDelaySeconds: Int,
    autoLockTimeout: String,
    requirePasscodeOnStartup: Boolean,
    logs: List<AuditLogEntity>,
    logFilter: String,
    logSearchQuery: String,
    totalTasksCount: Int,
    onRotateKey: () -> Unit,
    onViewRecoveryPhrase: () -> Unit,
    onExportVault: () -> Unit,
    onToggleBiometrics: (Boolean) -> Unit,
    onToggleRequireSensitive: (Boolean) -> Unit,
    onToggleFallbackPasscode: (Boolean) -> Unit,
    onFallbackDelayChange: (Int) -> Unit,
    onAutoLockChange: (String) -> Unit,
    onToggleRequireStartup: (Boolean) -> Unit,
    onLogFilterChange: (String) -> Unit,
    onLogSearchChange: (String) -> Unit,
    onWipeEnclave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredLogs = logs.filter { log ->
        val matchesFilter = when (logFilter) {
            "ALL" -> true
            "NORMAL" -> log.eventType == "NORMAL"
            "SENSITIVE" -> log.eventType == "SENSITIVE"
            "ALERT" -> log.eventType == "ALERT"
            else -> true
        }

        val matchesSearch = logSearchQuery.isBlank() ||
                log.event.contains(logSearchQuery, ignoreCase = true) ||
                log.deviceType.contains(logSearchQuery, ignoreCase = true) ||
                log.ipAddress.contains(logSearchQuery, ignoreCase = true)

        matchesFilter && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian950)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Professional Polish Hero: Encryption Status & Key Rotation Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(SurfaceHeroFrom, SurfaceHeroTo)))
                    .border(1.dp, BorderProminent, RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .testTag("encryption_status_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "VAULT STATUS",
                                color = Zinc500,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Encrypted Session Active",
                                color = Zinc100,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = Emerald400,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Progress Bar from Design HTML (w-full bg-zinc-900 h-1.5 rounded-full overflow-hidden)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Zinc900)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Emerald500.copy(alpha = 0.85f))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Handshake: Local Enclave",
                            color = Zinc500,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "0.4ms latency",
                            color = Zinc400,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Obsidian950, RoundedCornerShape(10.dp))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Key Fingerprint (SHA-256):",
                                color = Zinc500,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = keyFingerprint,
                                color = Cyan400,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Last rotated: $lastRotation",
                                color = Zinc400,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRotateKey,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("rotate_key_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Zinc100
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Cyan400,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Rotate Key",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onViewRecoveryPhrase,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("recovery_phrase_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Zinc100
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = Amber400,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "24-Word Phrase",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Biometrics & Fallback Flow Settings Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceTile)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(14.dp)
                    .testTag("biometrics_settings_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Zinc800)
                                .border(1.dp, Zinc700, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = Emerald400,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Biometrics & Fallback Flow",
                                color = Zinc100,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Face ID & Fingerprint Enclave Access",
                                color = Zinc500,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Biometric Unlock Switch
                    SettingSwitchRow(
                        title = "Biometric Vault Unlock",
                        description = "Enable Face ID / Touch ID for quick unlocking",
                        checked = biometricsEnabled,
                        onCheckedChange = onToggleBiometrics,
                        testTag = "toggle_biometrics"
                    )

                    // Require Passcode for Sensitive Actions
                    SettingSwitchRow(
                        title = "Require PIN for Sensitive Actions",
                        description = "Re-verify passcode for key rotations or data exports",
                        checked = requirePasscodeForSensitive,
                        onCheckedChange = onToggleRequireSensitive,
                        testTag = "toggle_require_sensitive"
                    )

                    // Fallback to Passcode Toggle
                    SettingSwitchRow(
                        title = "Fallback to Passcode",
                        description = "Automatically prompt for PIN on biometric failure",
                        checked = fallbackToPasscode,
                        onCheckedChange = onToggleFallbackPasscode,
                        testTag = "toggle_fallback_passcode"
                    )

                    // Fallback Delay Selector (Immediate, 3s, 5s, 10s)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Biometric Fallback Delay",
                            color = Zinc100,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Time before triggering master PIN prompt",
                            color = Zinc500,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(0 to "Instant", 3 to "3s", 5 to "5s", 10 to "10s").forEach { (sec, label) ->
                                val isSelected = fallbackDelaySeconds == sec
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Zinc100 else Obsidian800)
                                        .border(
                                            1.dp,
                                            if (isSelected) Zinc100 else Obsidian700,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onFallbackDelayChange(sec) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Obsidian950 else Zinc400,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Passcode & Session Security Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceTile)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Zinc800)
                                .border(1.dp, Zinc700, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Amber400,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Passcode & Session Security",
                                color = Zinc100,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Auto-Lock Timers & Startup Authentication",
                                color = Zinc500,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    SettingSwitchRow(
                        title = "Require Passcode on Startup",
                        description = "Enforce vault lock screen whenever app launches",
                        checked = requirePasscodeOnStartup,
                        onCheckedChange = onToggleRequireStartup,
                        testTag = "toggle_require_startup"
                    )

                    // Auto-lock timeout selector
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Auto-Lock Inactivity Timeout",
                            color = Zinc100,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Immediate", "1 min", "5 mins", "15 mins").forEach { timeout ->
                                val isSelected = autoLockTimeout.contains(timeout.take(3), ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Zinc100 else Obsidian800)
                                        .border(
                                            1.dp,
                                            if (isSelected) Zinc100 else Obsidian700,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onAutoLockChange(timeout) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = timeout,
                                        color = if (isSelected) Obsidian950 else Zinc400,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Data Sovereignty & Portability Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceTile)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Zinc800)
                                .border(1.dp, Zinc700, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Emerald400,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Data Sovereignty & Portability",
                                color = Zinc100,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Local-First Storage & JSON Export",
                                color = Zinc500,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Obsidian950, RoundedCornerShape(8.dp))
                            .border(1.dp, Obsidian700, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Local Enclave Database",
                                    color = Zinc100,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$totalTasksCount tasks • 0 external telemetry",
                                    color = Zinc500,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(Obsidian800, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "48 KB",
                                    color = Cyan400,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onExportVault,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("export_json_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Zinc100
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Obsidian700)
                        ) {
                            Text(
                                text = "Export Encrypted JSON",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        OutlinedButton(
                            onClick = onWipeEnclave,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("wipe_enclave_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Rose400
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoseBorder)
                        ) {
                            Text(
                                text = "Wipe Enclave",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // 5. Professional Polish Security Logs Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceTile)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(14.dp)
                    .testTag("security_logs_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SECURITY LOGS",
                            color = Zinc500,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(EmeraldBadgeBg)
                                .border(1.dp, EmeraldBadgeBorder, CircleShape)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "REALTIME",
                                color = Emerald400,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Search input for logs
                    OutlinedTextField(
                        value = logSearchQuery,
                        onValueChange = onLogSearchChange,
                        placeholder = {
                            Text(
                                text = "Filter events, IPs, devices...",
                                fontSize = 11.sp,
                                color = Zinc500
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Zinc500,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("log_search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Obsidian950,
                            unfocusedContainerColor = Obsidian950,
                            focusedBorderColor = Cyan400,
                            unfocusedBorderColor = Obsidian700,
                            focusedTextColor = Zinc100,
                            unfocusedTextColor = Zinc100
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        singleLine = true
                    )

                    // Filter pill selector for logs (ALL, NORMAL, SENSITIVE, ALERT)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "NORMAL", "SENSITIVE", "ALERT").forEach { f ->
                            val isSelected = logFilter == f
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Zinc100 else Obsidian800)
                                    .border(
                                        1.dp,
                                        if (isSelected) Zinc100 else Obsidian700,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { onLogFilterChange(f) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = f,
                                    color = if (isSelected) Obsidian950 else Zinc400,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Logs list items
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredLogs.forEach { log ->
                            AuditLogItemView(log = log)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Zinc100,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = description,
                color = Zinc500,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Obsidian950,
                checkedTrackColor = Emerald400,
                uncheckedThumbColor = Zinc500,
                uncheckedTrackColor = Obsidian800
            )
        )
    }
}

@Composable
fun AuditLogItemView(log: AuditLogEntity) {
    val pipColor = when (log.eventType) {
        "ALERT" -> Rose500
        "SENSITIVE" -> Amber500
        else -> Emerald500
    }

    val badgeBg = when (log.eventType) {
        "ALERT" -> Rose950
        "SENSITIVE" -> Amber950
        else -> Emerald950
    }

    val badgeBorder = when (log.eventType) {
        "ALERT" -> RoseBorder
        "SENSITIVE" -> AmberBorder
        else -> EmeraldBorder
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Obsidian950)
            .border(1.dp, BorderDivider, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(pipColor)
                    )
                    Text(
                        text = log.event,
                        color = Zinc100,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(4.dp))
                        .border(1.dp, badgeBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = log.eventType,
                        color = pipColor,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${log.deviceType} • IP: ${log.ipAddress}",
                    color = Zinc500,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = log.relativeTime,
                    color = Zinc400,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = log.absoluteTime,
                color = Zinc500,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
