package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BorderProminent
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.Cyan400
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
import com.example.ui.theme.SurfaceHeroTo
import com.example.ui.theme.SurfaceTile
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc50
import com.example.ui.theme.Zinc500
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VaultLockScreen(
    passcodeAttempt: String,
    failedAttempts: Int,
    biometricsEnabled: Boolean,
    onEnterDigit: (String) -> Unit,
    onDeleteDigit: () -> Unit,
    onClear: () -> Unit,
    onBiometricUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Subtle background animation simulating tech shader lattice
    val infiniteTransition = rememberInfiniteTransition(label = "vault_lattice")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian950)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("vault_lock_screen")
    ) {
        // Tech Grid Canvas Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Center radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x1A06B6D4),
                        Color(0x0A10B981),
                        Color.Transparent
                    ),
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.35f),
                    radius = canvasWidth * 0.8f
                )
            )

            // Animated tech points
            for (i in 0 until 12) {
                val angle = pulse + i * (6.28f / 12f)
                val r = 160f + 25f * sin(pulse * 2f + i.toFloat())
                val x = canvasWidth * 0.5f + r * cos(angle)
                val y = canvasHeight * 0.28f + r * sin(angle)
                drawCircle(
                    color = Color(0x3306B6D4),
                    radius = 2.5f,
                    center = Offset(x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Badges
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
                            .background(EmeraldBadgeBg, CircleShape)
                            .border(1.dp, EmeraldBadgeBorder, CircleShape)
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AES-GCM 256-BIT ENCLAVE",
                            color = Emerald400,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Zinc400,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "ZERO-TELEMETRY",
                        color = Zinc400,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Center Logo & Passcode Prompt
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceHeroTo)
                        .border(1.dp, BorderProminent, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_vault_logo),
                        contentDescription = "VaultTasks Icon",
                        modifier = Modifier.size(44.dp)
                    )
                }

                Text(
                    text = "VaultTasks",
                    color = Zinc50,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = "Enter Master PIN (Default: 123456)",
                    color = Zinc400,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )

                if (failedAttempts > 0) {
                    Text(
                        text = "Invalid passcode ($failedAttempts/5 attempts)",
                        color = Rose400,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 6-Dot PIN Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 6) {
                        val isFilled = i < passcodeAttempt.length
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) Emerald500 else Zinc800)
                                .border(
                                    1.dp,
                                    if (isFilled) Emerald500 else Zinc700,
                                    CircleShape
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Numeric Keypad
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val keypad = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("CLEAR", "0", "BACK")
                )

                keypad.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { key ->
                            KeypadButton(
                                text = key,
                                onClick = {
                                    when (key) {
                                        "CLEAR" -> onClear()
                                        "BACK" -> onDeleteDigit()
                                        else -> onEnterDigit(key)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Biometric Trigger Button
            if (biometricsEnabled) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceTile)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { onBiometricUnlock() }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                        .testTag("biometric_unlock_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometrics",
                        tint = Cyan400,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Unlock with Biometrics (Face ID / Touch ID)",
                        color = Zinc100,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Trust Policy Footer
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Zinc400,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = "Encrypted with client-side WebCrypto & Argon2id",
                    color = Zinc400,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(width = 78.dp, height = 52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceTile)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .testTag("keypad_$text"),
        contentAlignment = Alignment.Center
    ) {
        if (text == "BACK") {
            Icon(
                imageVector = Icons.Default.Backspace,
                contentDescription = "Backspace",
                tint = Zinc400,
                modifier = Modifier.size(18.dp)
            )
        } else if (text == "CLEAR") {
            Text(
                text = "C",
                color = Zinc400,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Text(
                text = text,
                color = Zinc100,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
