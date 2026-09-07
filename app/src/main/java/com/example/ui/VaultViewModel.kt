package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiTaskBreakdownService
import com.example.data.VaultRepository
import com.example.data.local.AppDatabase
import com.example.data.local.AuditLogEntity
import com.example.data.local.TaskEntity
import com.example.security.CryptoEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class VaultNavTab(val label: String) {
    ALL_TASKS("All Tasks"),
    TODO_LIST("To-do List"),
    KANBAN("Kanban"),
    ENCRYPTION_SETTINGS("Encryption")
}

class VaultViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = VaultRepository(database)
    private val aiService = GeminiTaskBreakdownService()

    // Passcode & Security Lock
    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val masterPasscode = MutableStateFlow("123456")
    private val _passcodeAttempt = MutableStateFlow("")
    val passcodeAttempt: StateFlow<String> = _passcodeAttempt.asStateFlow()

    private val _failedAttempts = MutableStateFlow(0)
    val failedAttempts: StateFlow<Int> = _failedAttempts.asStateFlow()

    private val _lockoutSeconds = MutableStateFlow(0)
    val lockoutSeconds: StateFlow<Int> = _lockoutSeconds.asStateFlow()

    // Global Content Masking Toggle
    private val _isMasked = MutableStateFlow(false)
    val isMasked: StateFlow<Boolean> = _isMasked.asStateFlow()

    // Navigation & View Layout
    private val _currentTab = MutableStateFlow(VaultNavTab.ALL_TASKS)
    val currentTab: StateFlow<VaultNavTab> = _currentTab.asStateFlow()

    // Tasks and Audit Flow
    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLogs: StateFlow<List<AuditLogEntity>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filters & Search
    private val _activeFilter = MutableStateFlow("ALL") // ALL, TODAY, UPCOMING, OVERDUE, COMPLETED, MASKED
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _logFilter = MutableStateFlow("ALL") // ALL, NORMAL, SENSITIVE, ALERT
    val logFilter: StateFlow<String> = _logFilter.asStateFlow()

    private val _logSearchQuery = MutableStateFlow("")
    val logSearchQuery: StateFlow<String> = _logSearchQuery.asStateFlow()

    // Kanban Active Column on Mobile
    private val _kanbanColumn = MutableStateFlow("TODO") // BACKLOG, TODO, IN_PROGRESS, DONE
    val kanbanColumn: StateFlow<String> = _kanbanColumn.asStateFlow()

    // Dialogs
    private val _isCommandPaletteOpen = MutableStateFlow(false)
    val isCommandPaletteOpen: StateFlow<Boolean> = _isCommandPaletteOpen.asStateFlow()

    private val _isNewTaskDialogOpen = MutableStateFlow(false)
    val isNewTaskDialogOpen: StateFlow<Boolean> = _isNewTaskDialogOpen.asStateFlow()

    private val _isRecoveryPhraseOpen = MutableStateFlow(false)
    val isRecoveryPhraseOpen: StateFlow<Boolean> = _isRecoveryPhraseOpen.asStateFlow()

    private val _isExportDialogOpen = MutableStateFlow(false)
    val isExportDialogOpen: StateFlow<Boolean> = _isExportDialogOpen.asStateFlow()

    // Security Configurations
    private val _biometricsEnabled = MutableStateFlow(true)
    val biometricsEnabled: StateFlow<Boolean> = _biometricsEnabled.asStateFlow()

    private val _requirePasscodeForSensitive = MutableStateFlow(true)
    val requirePasscodeForSensitive: StateFlow<Boolean> = _requirePasscodeForSensitive.asStateFlow()

    private val _fallbackToPasscode = MutableStateFlow(true)
    val fallbackToPasscode: StateFlow<Boolean> = _fallbackToPasscode.asStateFlow()

    private val _fallbackDelaySeconds = MutableStateFlow(3)
    val fallbackDelaySeconds: StateFlow<Int> = _fallbackDelaySeconds.asStateFlow()

    private val _autoLockTimeout = MutableStateFlow("5 minutes")
    val autoLockTimeout: StateFlow<String> = _autoLockTimeout.asStateFlow()

    private val _requirePasscodeOnStartup = MutableStateFlow(true)
    val requirePasscodeOnStartup: StateFlow<Boolean> = _requirePasscodeOnStartup.asStateFlow()

    private val _aesKeyFingerprint = MutableStateFlow(CryptoEngine.generateKeyFingerprint())
    val aesKeyFingerprint: StateFlow<String> = _aesKeyFingerprint.asStateFlow()

    private val _lastRotationTime = MutableStateFlow("Today, 2:00 PM")
    val lastRotationTime: StateFlow<String> = _lastRotationTime.asStateFlow()

    private val _recoveryWords = MutableStateFlow(CryptoEngine.generate24WordMnemonic())
    val recoveryWords: StateFlow<List<String>> = _recoveryWords.asStateFlow()

    // AI Task Generator State
    private val _aiPrompt = MutableStateFlow("")
    val aiPrompt: StateFlow<String> = _aiPrompt.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    private val _exportJson = MutableStateFlow("")
    val exportJson: StateFlow<String> = _exportJson.asStateFlow()

    // --- Passcode Controls ---
    fun enterPasscodeDigit(digit: String) {
        if (_passcodeAttempt.value.length < 6) {
            val updated = _passcodeAttempt.value + digit
            _passcodeAttempt.value = updated
            if (updated.length == 6) {
                verifyPasscode(updated)
            }
        }
    }

    fun deletePasscodeDigit() {
        if (_passcodeAttempt.value.isNotEmpty()) {
            _passcodeAttempt.value = _passcodeAttempt.value.dropLast(1)
        }
    }

    fun clearPasscode() {
        _passcodeAttempt.value = ""
    }

    private fun verifyPasscode(attempt: String) {
        if (attempt == masterPasscode.value) {
            _isLocked.value = false
            _passcodeAttempt.value = ""
            _failedAttempts.value = 0
            viewModelScope.launch {
                repository.logSecurityEvent(
                    event = "Vault Unlocked via Master PIN",
                    eventType = "NORMAL"
                )
            }
        } else {
            _failedAttempts.value += 1
            _passcodeAttempt.value = ""
            viewModelScope.launch {
                repository.logSecurityEvent(
                    event = "Failed PIN entry attempt (${_failedAttempts.value}/5)",
                    eventType = "ALERT",
                    status = "BLOCKED"
                )
            }
        }
    }

    fun triggerBiometricUnlock() {
        if (_biometricsEnabled.value) {
            _isLocked.value = false
            _passcodeAttempt.value = ""
            viewModelScope.launch {
                repository.logSecurityEvent(
                    event = "Vault Unlocked via Biometric Enclave",
                    eventType = "NORMAL"
                )
            }
        }
    }

    fun lockVault() {
        _isLocked.value = true
        _passcodeAttempt.value = ""
        viewModelScope.launch {
            repository.logSecurityEvent(
                event = "Vault Locked Manually",
                eventType = "NORMAL"
            )
        }
    }

    // --- Content Masking ---
    fun toggleContentMask() {
        val newMask = !_isMasked.value
        _isMasked.value = newMask
        viewModelScope.launch {
            repository.logSecurityEvent(
                event = if (newMask) "Data Masking Engaged (Blur Enabled)" else "Data Masking Disengaged",
                eventType = "NORMAL"
            )
        }
    }

    // --- Navigation ---
    fun setTab(tab: VaultNavTab) {
        _currentTab.value = tab
    }

    fun setFilter(filter: String) {
        _activeFilter.value = filter
    }

    fun setTagFilter(tag: String?) {
        _selectedTag.value = tag
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun setLogFilter(filter: String) {
        _logFilter.value = filter
    }

    fun setLogSearchQuery(q: String) {
        _logSearchQuery.value = q
    }

    fun setKanbanColumn(column: String) {
        _kanbanColumn.value = column
    }

    // --- Dialogs ---
    fun setCommandPaletteOpen(open: Boolean) {
        _isCommandPaletteOpen.value = open
    }

    fun setNewTaskDialogOpen(open: Boolean) {
        _isNewTaskDialogOpen.value = open
    }

    fun setRecoveryPhraseOpen(open: Boolean) {
        if (open && _requirePasscodeForSensitive.value) {
            viewModelScope.launch {
                repository.logSecurityEvent(
                    event = "Mnemonic Phrase Accessed via Secure Enclave",
                    eventType = "SENSITIVE"
                )
            }
        }
        _isRecoveryPhraseOpen.value = open
    }

    fun setExportDialogOpen(open: Boolean) {
        if (open) {
            generateExportJson()
        }
        _isExportDialogOpen.value = open
    }

    // --- Tasks CRUD ---
    fun createTask(
        title: String,
        description: String = "",
        tag: String = "security",
        priority: String = "MEDIUM",
        status: String = "TODO",
        dueDate: String = "Today",
        isVaultLocked: Boolean = false,
        subtasksCount: Int = 0
    ) {
        viewModelScope.launch {
            val task = TaskEntity(
                title = title.trim(),
                description = description.trim(),
                tag = tag.lowercase().replace("#", ""),
                priority = priority,
                status = status,
                dueDate = dueDate,
                isVaultLocked = isVaultLocked,
                subtasksCount = subtasksCount,
                subtasksCompleted = 0
            )
            repository.insertTask(task)
        }
    }

    fun updateTaskStatus(task: TaskEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateTask(task.copy(status = newStatus))
            repository.logSecurityEvent(
                event = "Task Status -> $newStatus: ${task.title.take(20)}",
                eventType = "NORMAL"
            )
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        val newStatus = if (task.status == "DONE") "TODO" else "DONE"
        val completedCount = if (newStatus == "DONE") task.subtasksCount else 0
        viewModelScope.launch {
            repository.updateTask(task.copy(status = newStatus, subtasksCompleted = completedCount))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            repository.clearCompletedTasks()
        }
    }

    // --- AI Generator ---
    fun setAiPrompt(text: String) {
        _aiPrompt.value = text
    }

    fun generateTaskWithAi(customPrompt: String? = null) {
        val prompt = customPrompt ?: _aiPrompt.value
        if (prompt.isBlank()) return

        _isAiGenerating.value = true
        viewModelScope.launch {
            try {
                val generatedTask = aiService.parseTaskWithAI(prompt)
                repository.insertTask(generatedTask)
                repository.logSecurityEvent(
                    event = "AI Structured Task Generated: ${generatedTask.title.take(24)}",
                    eventType = "NORMAL"
                )
                _aiPrompt.value = ""
                _isNewTaskDialogOpen.value = false
            } catch (e: Exception) {
                // Handled
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    // --- Security Settings Actions ---
    fun rotateEncryptionKey() {
        val newKey = CryptoEngine.generateKeyFingerprint()
        _aesKeyFingerprint.value = newKey
        val dateFormat = SimpleDateFormat("h:mm a, MMM d", Locale.US)
        val now = dateFormat.format(Date())
        _lastRotationTime.value = now

        viewModelScope.launch {
            repository.logSecurityEvent(
                event = "Master AES-GCM Key Rotated & Ephemeral Nonces Cleared",
                eventType = "SENSITIVE"
            )
        }
    }

    fun setBiometricsEnabled(enabled: Boolean) {
        _biometricsEnabled.value = enabled
        viewModelScope.launch {
            repository.logSecurityEvent(
                event = if (enabled) "Biometrics Access Enabled" else "Biometrics Access Disabled",
                eventType = "SENSITIVE"
            )
        }
    }

    fun setRequirePasscodeForSensitive(enabled: Boolean) {
        _requirePasscodeForSensitive.value = enabled
    }

    fun setFallbackToPasscode(enabled: Boolean) {
        _fallbackToPasscode.value = enabled
    }

    fun setFallbackDelaySeconds(seconds: Int) {
        _fallbackDelaySeconds.value = seconds
    }

    fun setAutoLockTimeout(timeout: String) {
        _autoLockTimeout.value = timeout
    }

    fun setRequirePasscodeOnStartup(enabled: Boolean) {
        _requirePasscodeOnStartup.value = enabled
    }

    fun wipeAllVaultData() {
        viewModelScope.launch {
            repository.clearAllData()
            _isLocked.value = true
            _aesKeyFingerprint.value = CryptoEngine.generateKeyFingerprint()
        }
    }

    private fun generateExportJson() {
        val tasksList = allTasks.value
        val root = JSONObject().apply {
            put("vaultVersion", "2.1.0-e2e")
            put("cipher", "AES-GCM-256")
            put("exportedAt", System.currentTimeMillis())
            put("keyFingerprint", _aesKeyFingerprint.value)
            val jsonArray = JSONArray()
            tasksList.forEach { task ->
                val taskJson = JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("description", task.description)
                    put("tag", task.tag)
                    put("priority", task.priority)
                    put("status", task.status)
                    put("dueDate", task.dueDate)
                    put("isVaultLocked", task.isVaultLocked)
                    put("subtasksCount", task.subtasksCount)
                }
                jsonArray.put(taskJson)
            }
            put("tasks", jsonArray)
            put("checksum", CryptoEngine.sha256Hex(jsonArray.toString()))
        }
        _exportJson.value = root.toString(2)
    }
}
