package com.example.data

import com.example.data.local.AppDatabase
import com.example.data.local.AuditLogEntity
import com.example.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VaultRepository(private val database: AppDatabase) {
    val allTasks: Flow<List<TaskEntity>> = database.taskDao().getAllTasks()
    val allLogs: Flow<List<AuditLogEntity>> = database.auditLogDao().getAllLogs()

    suspend fun insertTask(task: TaskEntity): Long {
        val id = database.taskDao().insertTask(task)
        logSecurityEvent(
            event = "Task Created & Encrypted (${task.tag})",
            eventType = if (task.isVaultLocked) "SENSITIVE" else "NORMAL"
        )
        return id
    }

    suspend fun updateTask(task: TaskEntity) {
        database.taskDao().updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        database.taskDao().deleteTask(task)
        logSecurityEvent(
            event = "Task Safely Shredded from Vault",
            eventType = "NORMAL"
        )
    }

    suspend fun clearCompletedTasks() {
        database.taskDao().clearCompletedTasks()
        logSecurityEvent(
            event = "Completed Vault Records Purged",
            eventType = "NORMAL"
        )
    }

    suspend fun logSecurityEvent(
        event: String,
        eventType: String,
        ip: String = "127.0.0.1",
        device: String = "Mobile Android Enclave",
        status: String = "VERIFIED"
    ) {
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val log = AuditLogEntity(
            event = event,
            eventType = eventType,
            timestamp = now,
            relativeTime = "Just now",
            absoluteTime = dateFormat.format(Date(now)),
            ipAddress = ip,
            deviceType = device,
            status = status
        )
        database.auditLogDao().insertLog(log)
    }

    suspend fun clearAllData() {
        database.taskDao().clearAll()
        database.auditLogDao().clearLogs()
        logSecurityEvent(
            event = "Cryptographic Enclave Wiped & Re-initialized",
            eventType = "ALERT",
            status = "BLOCKED"
        )
    }
}
