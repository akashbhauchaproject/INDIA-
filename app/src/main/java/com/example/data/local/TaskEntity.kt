package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val tag: String = "security",
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val status: String = "TODO",     // BACKLOG, TODO, IN_PROGRESS, DONE
    val dueDate: String = "Today",
    val isVaultLocked: Boolean = false,
    val subtasksCount: Int = 0,
    val subtasksCompleted: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
