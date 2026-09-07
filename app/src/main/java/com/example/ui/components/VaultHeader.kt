package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.EmeraldBadgeBg
import com.example.ui.theme.EmeraldBadgeBorder
import com.example.ui.theme.Obsidian700
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceTile
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc500
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800

@Composable
fun VaultHeader(
    isMasked: Boolean,
    onToggleMask: () -> Unit,
    onOpenCommandPalette: () -> Unit,
    onLockVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Obsidian950)
            .border(1.dp, BorderSubtle)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo and Title (matching HTML: w-8 h-8 rounded-lg bg-zinc-800 border border-zinc-700 + text-lg font-semibold tracking-tight)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Zinc800)
                        .border(1.dp, Zinc700, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VT",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-0.5).sp
                    )
                }

                Column {
                    Text(
                        text = "VaultTasks",
                        color = Zinc100,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.4).sp
                    )
                    Text(
                        text = "Client-Side Zero-Knowledge",
                        color = Zinc500,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Right side: Badge (px-2.5 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/20) + Quick action buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // AES Badge from Design HTML
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldBadgeBg)
                        .border(1.dp, EmeraldBadgeBorder, CircleShape)
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Emerald500)
                        )
                        Text(
                            text = "AES-256 E2E",
                            color = Emerald400,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Command palette button
                IconButton(
                    onClick = onOpenCommandPalette,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Zinc800)
                        .border(1.dp, Zinc700, RoundedCornerShape(8.dp))
                        .testTag("command_palette_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Command Palette",
                        tint = Zinc400,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Lock Vault button
                IconButton(
                    onClick = onLockVault,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Zinc800)
                        .border(1.dp, Zinc700, RoundedCornerShape(8.dp))
                        .testTag("lock_vault_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Vault",
                        tint = Zinc400,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Mask Banner Button (styled to matching theme)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isMasked) Color(0x33F59E0B) else SurfaceTile)
                .border(
                    1.dp,
                    if (isMasked) Color(0x88F59E0B) else BorderSubtle,
                    RoundedCornerShape(12.dp)
                )
                .clickable { onToggleMask() }
                .padding(horizontal = 12.dp, vertical = 7.dp)
                .testTag("mask_toggle_button"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = if (isMasked) Color(0xFFFBBF24) else Zinc400,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (isMasked) "Data Masking Active (Obscured)" else "Global Content Masking",
                    color = if (isMasked) Color(0xFFFDE68A) else Zinc100,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        if (isMasked) Color(0xFF78350F) else Obsidian800,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isMasked) "UNMASK" else "MASK DATA",
                    color = if (isMasked) Color(0xFFFDE68A) else Zinc400,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
