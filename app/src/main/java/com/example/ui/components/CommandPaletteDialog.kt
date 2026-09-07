package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.VaultNavTab
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Obsidian700
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian850
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceTile
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc500

data class CommandAction(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val shortcut: String,
    val action: () -> Unit
)

@Composable
fun CommandPaletteDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onOpenNewTask: () -> Unit,
    onToggleMask: () -> Unit,
    onLockVault: () -> Unit,
    onRotateKey: () -> Unit,
    onViewRecovery: () -> Unit,
    onExportVault: () -> Unit,
    onNavigate: (VaultNavTab) -> Unit
) {
    if (!isOpen) return

    var query by remember { mutableStateOf("") }

    val commands = listOf(
        CommandAction(
            id = "new_task",
            title = "Create New Task",
            subtitle = "Add encrypted objective to vault",
            icon = Icons.Default.Add,
            shortcut = "C",
            action = { onDismiss(); onOpenNewTask() }
        ),
        CommandAction(
            id = "toggle_mask",
            title = "Toggle Data Masking",
            subtitle = "Obscure or reveal sensitive objectives",
            icon = Icons.Default.Visibility,
            shortcut = "M",
            action = { onDismiss(); onToggleMask() }
        ),
        CommandAction(
            id = "lock_vault",
            title = "Lock Enclave Now",
            subtitle = "Return immediately to master PIN screen",
            icon = Icons.Default.Lock,
            shortcut = "L",
            action = { onDismiss(); onLockVault() }
        ),
        CommandAction(
            id = "rotate_key",
            title = "Rotate AES-256 Key",
            subtitle = "Derive fresh ephemeral cipher keys",
            icon = Icons.Default.Refresh,
            shortcut = "R",
            action = { onDismiss(); onRotateKey() }
        ),
        CommandAction(
            id = "recovery_phrase",
            title = "View 24-Word Recovery Phrase",
            subtitle = "BIP39 cryptographic offline backup",
            icon = Icons.Default.Key,
            shortcut = "P",
            action = { onDismiss(); onViewRecovery() }
        ),
        CommandAction(
            id = "export_vault",
            title = "Export Encrypted Vault",
            subtitle = "Generate portable JSON with SHA-256 checksum",
            icon = Icons.Default.Download,
            shortcut = "E",
            action = { onDismiss(); onExportVault() }
        ),
        CommandAction(
            id = "nav_tasks",
            title = "Switch to All Tasks",
            subtitle = "High-density list view",
            icon = Icons.Default.ViewList,
            shortcut = "1",
            action = { onDismiss(); onNavigate(VaultNavTab.ALL_TASKS) }
        ),
        CommandAction(
            id = "nav_todo",
            title = "Switch to To-do List",
            subtitle = "Interactive checklist view",
            icon = Icons.Default.ViewList,
            shortcut = "2",
            action = { onDismiss(); onNavigate(VaultNavTab.TODO_LIST) }
        ),
        CommandAction(
            id = "nav_kanban",
            title = "Switch to Kanban Board",
            subtitle = "Multi-column objective tracking",
            icon = Icons.Default.ViewKanban,
            shortcut = "3",
            action = { onDismiss(); onNavigate(VaultNavTab.KANBAN) }
        ),
        CommandAction(
            id = "nav_security",
            title = "Switch to Encryption Settings",
            subtitle = "Cryptographic enclave controls and logs",
            icon = Icons.Default.VpnKey,
            shortcut = "4",
            action = { onDismiss(); onNavigate(VaultNavTab.ENCRYPTION_SETTINGS) }
        )
    )

    val filteredCommands = commands.filter {
        query.isBlank() ||
                it.title.contains(query, ignoreCase = true) ||
                it.subtitle.contains(query, ignoreCase = true)
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceTile)
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .padding(14.dp)
                .testTag("command_palette_dialog")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Command palette search
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            text = "Type a command or search actions...",
                            fontSize = 12.sp,
                            color = Zinc500,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Cyan400,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("command_palette_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Obsidian950,
                        unfocusedContainerColor = Obsidian950,
                        focusedBorderColor = Cyan400,
                        unfocusedBorderColor = Obsidian700,
                        focusedTextColor = Zinc100,
                        unfocusedTextColor = Zinc100
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    singleLine = true
                )

                // Command list
                LazyColumn(
                    modifier = Modifier.height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredCommands, key = { it.id }) { cmd ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Obsidian850)
                                .border(1.dp, Obsidian700, RoundedCornerShape(8.dp))
                                .clickable { cmd.action() }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("command_item_${cmd.id}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Obsidian800),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = cmd.icon,
                                        contentDescription = null,
                                        tint = Zinc100,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = cmd.title,
                                        color = Zinc100,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = cmd.subtitle,
                                        color = Zinc500,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(Obsidian800, RoundedCornerShape(4.dp))
                                    .border(1.dp, Obsidian700, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = cmd.shortcut,
                                    color = Zinc400,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
