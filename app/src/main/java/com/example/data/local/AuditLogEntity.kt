package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val event: String,
    val eventType: String, // NORMAL, SENSITIVE, ALERT
    val timestamp: Long = System.currentTimeMillis(),
    val relativeTime: String,
    val absoluteTime: String,
    val ipAddress: String = "192.168.1.104",
    val deviceType: String = "Android Enclave (Snapdragon SE)",
    val status: String = "VERIFIED" // VERIFIED, WARNING, BLOCKED
)
