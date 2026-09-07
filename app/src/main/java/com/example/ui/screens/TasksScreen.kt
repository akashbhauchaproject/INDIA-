package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TaskEntity
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber500
import com.example.ui.theme.AmberBorder
import com.example.ui.theme.Amber950
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.BorderProminent
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.Emerald950
import com.example.ui.theme.Obsidian600
import com.example.ui.theme.Obsidian700
import com.example.ui.theme.Obsidian750
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
import com.example.ui.theme.Zinc200
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc500
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800
import com.example.ui.theme.Zinc900

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    isMasked: Boolean,
    activeFilter: String,
    selectedTag: String?,
    searchQuery: String,
    aiPrompt: String,
    isAiGenerating: Boolean,
    onFilterChange: (String) -> Unit,
    onTagSelect: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onAiPromptChange: (String) -> Unit,
    onGenerateWithAi: () -> Unit,
    onToggleTaskComplete: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onOpenNewTaskDialog: () -> Unit,
    isTodoListMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Filter tasks based on active filter, tag, and search query
    val filteredTasks = tasks.filter { task ->
        val matchesFilter = when (activeFilter) {
            "ALL" -> true
            "TODAY" -> task.dueDate.contains("Today", ignoreCase = true)
            "UPCOMING" -> task.dueDate.contains("Tomorrow", ignoreCase = true) || task.dueDate.contains("Next", ignoreCase = true)
            "OVERDUE" -> task.dueDate.contains("Yesterday", ignoreCase = true)
            "COMPLETED" -> task.status == "DONE"
            "MASKED" -> task.isVaultLocked
            else -> true
        }

        val matchesTag = selectedTag == null || task.tag.equals(selectedTag, ignoreCase = true)

        val matchesSearch = searchQuery.isBlank() ||
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true) ||
                task.tag.contains(searchQuery, ignoreCase = true)

        matchesFilter && matchesTag && matchesSearch
    }

    val totalCount = tasks.size
    val completedCount = tasks.count { it.status == "DONE" }
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian950)
            .testTag(if (isTodoListMode) "todo_list_screen" else "tasks_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Focus Progress Target Bar (matching Professional Polish hero card)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(SurfaceHeroFrom, SurfaceHeroTo)))
                        .border(1.dp, BorderProminent, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isTodoListMode) "TO-DO LIST PROGRESS" else "SECURITY OBJECTIVES",
                                color = Zinc500,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "$completedCount / $totalCount (${(progress * 100).toInt()}%)",
                                color = Emerald400,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // Progress bar track matching HTML (w-full bg-zinc-900 h-1.5 rounded-full)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Zinc900)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (progress > 0f) progress else 0.001f)
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(Emerald500.copy(alpha = 0.85f))
                            )
                        }
                    }
                }
            }

            // Natural Language Task Breakdown (Gemini AI Engine)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceTile)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Cyan400,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "AI Task Breakdown (Gemini 3.5 Flash)",
                                color = Zinc400,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = aiPrompt,
                                onValueChange = onAiPromptChange,
                                placeholder = {
                                    Text(
                                        text = "e.g., Audit AES-GCM IV reuse vulnerability with 3 subtasks",
                                        fontSize = 11.sp,
                                        color = Zinc500
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("ai_task_prompt_input"),
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
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { onGenerateWithAi() })
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Zinc100)
                                    .clickable(enabled = !isAiGenerating) { onGenerateWithAi() }
                                    .padding(horizontal = 14.dp, vertical = 13.dp)
                                    .testTag("ai_generate_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isAiGenerating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = Obsidian950,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Generate",
                                        color = Obsidian950,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Search Bar & Filter Controls
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = "Search objectives, tags, hash...",
                            fontSize = 12.sp,
                            color = Zinc500
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Zinc500,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("tasks_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Obsidian900,
                        unfocusedContainerColor = Obsidian900,
                        focusedBorderColor = Obsidian600,
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
            }

            // Filter Pills (All, Today, Upcoming, Overdue, Completed, Masked)
            item {
                val filters = listOf("ALL", "TODAY", "UPCOMING", "OVERDUE", "COMPLETED", "MASKED")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    filters.forEach { f ->
                        val isSelected = activeFilter == f
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Zinc100 else Obsidian850)
                                .border(
                                    1.dp,
                                    if (isSelected) Zinc100 else Obsidian700,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onFilterChange(f) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("filter_pill_$f")
                        ) {
                            Text(
                                text = f,
                                color = if (isSelected) Obsidian950 else Zinc400,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Tag Filters (#all, #security, #devops, #frontend, #backend, #database)
            item {
                val tags = listOf(null, "security", "devops", "frontend", "backend", "database")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.forEach { tag ->
                        val isSelected = selectedTag == tag
                        val label = if (tag == null) "#all" else "#$tag"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Obsidian700 else Obsidian900)
                                .border(
                                    1.dp,
                                    if (isSelected) Cyan400 else Obsidian700,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onTagSelect(tag) }
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Cyan400 else Zinc400,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Objectives (${filteredTasks.size})",
                        color = Zinc400,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Local Enclave Cache",
                        color = Zinc500,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Empty State
            if (filteredTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Zinc500,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "No tasks match filter in enclave",
                                color = Zinc400,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Tasks List Items
            items(filteredTasks, key = { it.id }) { task ->
                TaskCardItem(
                    task = task,
                    isMasked = isMasked,
                    onToggleComplete = { onToggleTaskComplete(task) },
                    onDelete = { onDeleteTask(task) }
                )
            }

            // Security Audit Footer
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .background(SurfaceTile)
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔒 Local Enclave Handshake: OK",
                        color = Emerald400,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "CSP Sandbox: ACTIVE",
                        color = Zinc400,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(72.dp)) // Padding for FAB
            }
        }

        // Floating Action Button for New Task
        FloatingActionButton(
            onClick = onOpenNewTaskDialog,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 20.dp)
                .testTag("floating_new_task_button"),
            containerColor = Zinc100,
            contentColor = Obsidian950,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Task",
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "New Task",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun TaskCardItem(
    task: TaskEntity,
    isMasked: Boolean,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDone = task.status == "DONE"

    // Priority color tokens
    val priorityPipColor = when (task.priority) {
        "HIGH" -> Rose500
        "MEDIUM" -> Amber500
        else -> Emerald500
    }

    val priorityBadgeBg = when (task.priority) {
        "HIGH" -> Rose950
        "MEDIUM" -> Amber950
        else -> Emerald950
    }

    val priorityBadgeBorder = when (task.priority) {
        "HIGH" -> RoseBorder
        "MEDIUM" -> AmberBorder
        else -> EmeraldBorder
    }

    val priorityTextColor = when (task.priority) {
        "HIGH" -> Rose400
        "MEDIUM" -> Amber400
        else -> Emerald400
    }

    val shouldMask = isMasked || task.isVaultLocked && isMasked

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceTile)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(13.dp)
            .testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox and Content
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Checkbox target
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(18.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (isDone) Emerald500 else Obsidian800)
                        .border(
                            1.dp,
                            if (isDone) Emerald500 else Obsidian700,
                            RoundedCornerShape(5.dp)
                        )
                        .clickable { onToggleComplete() }
                        .testTag("task_checkbox_${task.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Obsidian950,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Details
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Priority pip dot
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(priorityPipColor)
                        )

                        // Title (with masking)
                        Text(
                            text = if (shouldMask) "••••••••••••••••" else task.title,
                            color = if (isDone) Zinc500 else Zinc100,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (isDone) TextDecoration.LineThrough else null,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = if (shouldMask) Modifier.blur(4.dp) else Modifier
                        )

                        // Lock indicator if sensitive
                        if (task.isVaultLocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Vault Locked",
                                tint = Amber400,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }

                    if (task.description.isNotBlank() && !isDone) {
                        Text(
                            text = if (shouldMask) "••••••••••••••••••••••••" else task.description,
                            color = Zinc400,
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = if (shouldMask) Modifier.blur(3.dp) else Modifier
                        )
                    }

                    // Metadata Row: Tag, Due date, Subtask progress
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Tag Chip
                        Box(
                            modifier = Modifier
                                .background(Obsidian800, RoundedCornerShape(4.dp))
                                .border(1.dp, Obsidian700, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "#${task.tag}",
                                color = Zinc400,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "• ${task.dueDate}",
                            color = Zinc500,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        if (task.subtasksCount > 0) {
                            Text(
                                text = "• ${task.subtasksCompleted}/${task.subtasksCount} subtasks",
                                color = Cyan400,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Right column: Priority badge & Delete button
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(priorityBadgeBg, RoundedCornerShape(4.dp))
                        .border(1.dp, priorityBadgeBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = task.priority,
                        color = priorityTextColor,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Zinc500,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}
