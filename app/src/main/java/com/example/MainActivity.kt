package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.VaultNavTab
import com.example.ui.VaultViewModel
import com.example.ui.components.CommandPaletteDialog
import com.example.ui.components.CreateTaskDialog
import com.example.ui.components.ExportVaultDialog
import com.example.ui.components.RecoveryPhraseDialog
import com.example.ui.components.VaultBottomNavigation
import com.example.ui.components.VaultHeader
import com.example.ui.screens.EncryptionSettingsScreen
import com.example.ui.screens.KanbanScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.VaultLockScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Obsidian950

class MainActivity : ComponentActivity() {
    private val viewModel: VaultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                VaultTasksApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VaultTasksApp(viewModel: VaultViewModel) {
    val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
    val passcodeAttempt by viewModel.passcodeAttempt.collectAsStateWithLifecycle()
    val failedAttempts by viewModel.failedAttempts.collectAsStateWithLifecycle()
    val biometricsEnabled by viewModel.biometricsEnabled.collectAsStateWithLifecycle()
    val isMasked by viewModel.isMasked.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val logs by viewModel.allLogs.collectAsStateWithLifecycle()
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
    val selectedTag by viewModel.selectedTag.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val logFilter by viewModel.logFilter.collectAsStateWithLifecycle()
    val logSearchQuery by viewModel.logSearchQuery.collectAsStateWithLifecycle()
    val kanbanColumn by viewModel.kanbanColumn.collectAsStateWithLifecycle()

    val isCommandPaletteOpen by viewModel.isCommandPaletteOpen.collectAsStateWithLifecycle()
    val isNewTaskDialogOpen by viewModel.isNewTaskDialogOpen.collectAsStateWithLifecycle()
    val isRecoveryPhraseOpen by viewModel.isRecoveryPhraseOpen.collectAsStateWithLifecycle()
    val isExportDialogOpen by viewModel.isExportDialogOpen.collectAsStateWithLifecycle()

    val requirePasscodeForSensitive by viewModel.requirePasscodeForSensitive.collectAsStateWithLifecycle()
    val fallbackToPasscode by viewModel.fallbackToPasscode.collectAsStateWithLifecycle()
    val fallbackDelaySeconds by viewModel.fallbackDelaySeconds.collectAsStateWithLifecycle()
    val autoLockTimeout by viewModel.autoLockTimeout.collectAsStateWithLifecycle()
    val requirePasscodeOnStartup by viewModel.requirePasscodeOnStartup.collectAsStateWithLifecycle()
    val aesKeyFingerprint by viewModel.aesKeyFingerprint.collectAsStateWithLifecycle()
    val lastRotationTime by viewModel.lastRotationTime.collectAsStateWithLifecycle()
    val recoveryWords by viewModel.recoveryWords.collectAsStateWithLifecycle()

    val aiPrompt by viewModel.aiPrompt.collectAsStateWithLifecycle()
    val isAiGenerating by viewModel.isAiGenerating.collectAsStateWithLifecycle()
    val exportJson by viewModel.exportJson.collectAsStateWithLifecycle()

    // Handle back button behavior
    BackHandler(enabled = !isLocked) {
        when {
            isCommandPaletteOpen -> viewModel.setCommandPaletteOpen(false)
            isNewTaskDialogOpen -> viewModel.setNewTaskDialogOpen(false)
            isRecoveryPhraseOpen -> viewModel.setRecoveryPhraseOpen(false)
            isExportDialogOpen -> viewModel.setExportDialogOpen(false)
            currentTab != VaultNavTab.ALL_TASKS -> viewModel.setTab(VaultNavTab.ALL_TASKS)
            else -> viewModel.lockVault()
        }
    }

    if (isLocked) {
        VaultLockScreen(
            passcodeAttempt = passcodeAttempt,
            failedAttempts = failedAttempts,
            biometricsEnabled = biometricsEnabled,
            onEnterDigit = { viewModel.enterPasscodeDigit(it) },
            onDeleteDigit = { viewModel.deletePasscodeDigit() },
            onClear = { viewModel.clearPasscode() },
            onBiometricUnlock = { viewModel.triggerBiometricUnlock() }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Obsidian950),
            topBar = {
                Column(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                    VaultHeader(
                        isMasked = isMasked,
                        onToggleMask = { viewModel.toggleContentMask() },
                        onOpenCommandPalette = { viewModel.setCommandPaletteOpen(true) },
                        onLockVault = { viewModel.lockVault() }
                    )
                }
            },
            bottomBar = {
                VaultBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.setTab(it) }
                )
            },
            containerColor = Obsidian950
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Obsidian950)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        VaultNavTab.ALL_TASKS -> {
                            TasksScreen(
                                tasks = tasks,
                                isMasked = isMasked,
                                activeFilter = activeFilter,
                                selectedTag = selectedTag,
                                searchQuery = searchQuery,
                                aiPrompt = aiPrompt,
                                isAiGenerating = isAiGenerating,
                                onFilterChange = { viewModel.setFilter(it) },
                                onTagSelect = { viewModel.setTagFilter(it) },
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onAiPromptChange = { viewModel.setAiPrompt(it) },
                                onGenerateWithAi = { viewModel.generateTaskWithAi() },
                                onToggleTaskComplete = { viewModel.toggleTaskCompletion(it) },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                onOpenNewTaskDialog = { viewModel.setNewTaskDialogOpen(true) },
                                isTodoListMode = false
                            )
                        }

                        VaultNavTab.TODO_LIST -> {
                            TasksScreen(
                                tasks = tasks,
                                isMasked = isMasked,
                                activeFilter = activeFilter,
                                selectedTag = selectedTag,
                                searchQuery = searchQuery,
                                aiPrompt = aiPrompt,
                                isAiGenerating = isAiGenerating,
                                onFilterChange = { viewModel.setFilter(it) },
                                onTagSelect = { viewModel.setTagFilter(it) },
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onAiPromptChange = { viewModel.setAiPrompt(it) },
                                onGenerateWithAi = { viewModel.generateTaskWithAi() },
                                onToggleTaskComplete = { viewModel.toggleTaskCompletion(it) },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                onOpenNewTaskDialog = { viewModel.setNewTaskDialogOpen(true) },
                                isTodoListMode = true
                            )
                        }

                        VaultNavTab.KANBAN -> {
                            KanbanScreen(
                                tasks = tasks,
                                activeColumn = kanbanColumn,
                                isMasked = isMasked,
                                onColumnChange = { viewModel.setKanbanColumn(it) },
                                onMoveTaskStatus = { task, newStatus ->
                                    viewModel.updateTaskStatus(task, newStatus)
                                },
                                onOpenNewTaskDialog = { viewModel.setNewTaskDialogOpen(true) }
                            )
                        }

                        VaultNavTab.ENCRYPTION_SETTINGS -> {
                            EncryptionSettingsScreen(
                                keyFingerprint = aesKeyFingerprint,
                                lastRotation = lastRotationTime,
                                biometricsEnabled = biometricsEnabled,
                                requirePasscodeForSensitive = requirePasscodeForSensitive,
                                fallbackToPasscode = fallbackToPasscode,
                                fallbackDelaySeconds = fallbackDelaySeconds,
                                autoLockTimeout = autoLockTimeout,
                                requirePasscodeOnStartup = requirePasscodeOnStartup,
                                logs = logs,
                                logFilter = logFilter,
                                logSearchQuery = logSearchQuery,
                                totalTasksCount = tasks.size,
                                onRotateKey = { viewModel.rotateEncryptionKey() },
                                onViewRecoveryPhrase = { viewModel.setRecoveryPhraseOpen(true) },
                                onExportVault = { viewModel.setExportDialogOpen(true) },
                                onToggleBiometrics = { viewModel.setBiometricsEnabled(it) },
                                onToggleRequireSensitive = { viewModel.setRequirePasscodeForSensitive(it) },
                                onToggleFallbackPasscode = { viewModel.setFallbackToPasscode(it) },
                                onFallbackDelayChange = { viewModel.setFallbackDelaySeconds(it) },
                                onAutoLockChange = { viewModel.setAutoLockTimeout(it) },
                                onToggleRequireStartup = { viewModel.setRequirePasscodeOnStartup(it) },
                                onLogFilterChange = { viewModel.setLogFilter(it) },
                                onLogSearchChange = { viewModel.setLogSearchQuery(it) },
                                onWipeEnclave = { viewModel.wipeAllVaultData() }
                            )
                        }
                    }
                }
            }
        }

        // Modals & Dialogs
        CommandPaletteDialog(
            isOpen = isCommandPaletteOpen,
            onDismiss = { viewModel.setCommandPaletteOpen(false) },
            onOpenNewTask = { viewModel.setNewTaskDialogOpen(true) },
            onToggleMask = { viewModel.toggleContentMask() },
            onLockVault = { viewModel.lockVault() },
            onRotateKey = { viewModel.rotateEncryptionKey() },
            onViewRecovery = { viewModel.setRecoveryPhraseOpen(true) },
            onExportVault = { viewModel.setExportDialogOpen(true) },
            onNavigate = { viewModel.setTab(it) }
        )

        CreateTaskDialog(
            isOpen = isNewTaskDialogOpen,
            onDismiss = { viewModel.setNewTaskDialogOpen(false) },
            onCreateTask = { title, desc, tag, priority, due, isLocked, subtasks ->
                viewModel.createTask(title, desc, tag, priority, "TODO", due, isLocked, subtasks)
            }
        )

        RecoveryPhraseDialog(
            isOpen = isRecoveryPhraseOpen,
            words = recoveryWords,
            onDismiss = { viewModel.setRecoveryPhraseOpen(false) }
        )

        ExportVaultDialog(
            isOpen = isExportDialogOpen,
            exportJson = exportJson,
            onDismiss = { viewModel.setExportDialogOpen(false) }
        )
    }
}

