package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(entities = [TaskEntity::class, AuditLogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vault_tasks.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            seedInitialData(database)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(database: AppDatabase) {
            val now = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

            val initialTasks = listOf(
                TaskEntity(
                    title = "Update firewall ingress rules",
                    description = "Restrict internal cluster ports to verified enclave endpoints only.",
                    tag = "devops",
                    priority = "HIGH",
                    status = "TODO",
                    dueDate = "Yesterday",
                    isVaultLocked = true,
                    subtasksCount = 4,
                    subtasksCompleted = 2,
                    createdAt = now - 86400000
                ),
                TaskEntity(
                    title = "Review AES-GCM encryption key rotation",
                    description = "Verify key derivation salt and PBKDF2 iterations in cryptographic pipeline.",
                    tag = "security",
                    priority = "MEDIUM",
                    status = "IN_PROGRESS",
                    dueDate = "Today, 2:00 PM",
                    isVaultLocked = true,
                    subtasksCount = 3,
                    subtasksCompleted = 2,
                    createdAt = now - 3600000
                ),
                TaskEntity(
                    title = "Audit AES-GCM IV reuse vulnerability",
                    description = "Perform static security review of nonces generated across distributed clients.",
                    tag = "security",
                    priority = "HIGH",
                    status = "BACKLOG",
                    dueDate = "Tomorrow",
                    isVaultLocked = true,
                    subtasksCount = 3,
                    subtasksCompleted = 0,
                    createdAt = now - 1800000
                ),
                TaskEntity(
                    title = "Configure biometric fallback delay UX",
                    description = "Introduce user-configurable timeout before falling back from Face ID to master passcode.",
                    tag = "frontend",
                    priority = "LOW",
                    status = "DONE",
                    dueDate = "Completed",
                    isVaultLocked = false,
                    subtasksCount = 2,
                    subtasksCompleted = 2,
                    createdAt = now - 7200000
                ),
                TaskEntity(
                    title = "Export encrypted JSON backup to cold storage",
                    description = "Verify offline vault portability schema and integrity checksums.",
                    tag = "devops",
                    priority = "MEDIUM",
                    status = "TODO",
                    dueDate = "Today, 5:00 PM",
                    isVaultLocked = false,
                    subtasksCount = 1,
                    subtasksCompleted = 0,
                    createdAt = now - 900000
                )
            )
            database.taskDao().insertTasks(initialTasks)

            val initialLogs = listOf(
                AuditLogEntity(
                    event = "Local Enclave Handshake: OK",
                    eventType = "NORMAL",
                    timestamp = now - 120000,
                    relativeTime = "2 mins ago",
                    absoluteTime = dateFormat.format(Date(now - 120000)),
                    ipAddress = "127.0.0.1",
                    deviceType = "Mobile Android Enclave",
                    status = "VERIFIED"
                ),
                AuditLogEntity(
                    event = "Vault Unlocked via Biometrics",
                    eventType = "NORMAL",
                    timestamp = now - 300000,
                    relativeTime = "5 mins ago",
                    absoluteTime = dateFormat.format(Date(now - 300000)),
                    ipAddress = "192.168.1.104",
                    deviceType = "Pixel Secure Biometrics",
                    status = "VERIFIED"
                ),
                AuditLogEntity(
                    event = "AES-GCM 256-bit Key Derivation Complete",
                    eventType = "SENSITIVE",
                    timestamp = now - 3600000,
                    relativeTime = "1 hour ago",
                    absoluteTime = dateFormat.format(Date(now - 3600000)),
                    ipAddress = "192.168.1.104",
                    deviceType = "Android Hardware Keystore",
                    status = "VERIFIED"
                ),
                AuditLogEntity(
                    event = "Failed PIN attempt detected (1/5)",
                    eventType = "ALERT",
                    timestamp = now - 86400000,
                    relativeTime = "1 day ago",
                    absoluteTime = dateFormat.format(Date(now - 86400000)),
                    ipAddress = "10.0.4.21",
                    deviceType = "Unknown Client Entry",
                    status = "BLOCKED"
                ),
                AuditLogEntity(
                    event = "Zero-Knowledge CSP Sandbox Verified",
                    eventType = "NORMAL",
                    timestamp = now - 172800000,
                    relativeTime = "2 days ago",
                    absoluteTime = dateFormat.format(Date(now - 172800000)),
                    ipAddress = "127.0.0.1",
                    deviceType = "Local Enclave Runtime",
                    status = "VERIFIED"
                )
            )
            database.auditLogDao().insertLogs(initialLogs)
        }
    }
}
