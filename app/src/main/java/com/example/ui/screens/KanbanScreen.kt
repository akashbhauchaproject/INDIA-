package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TaskEntity
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber500
import com.example.ui.theme.AmberBorder
import com.example.ui.theme.Amber950
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.Emerald950
import com.example.ui.theme.Obsidian700
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian850
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.Rose400
import com.example.ui.theme.Rose500
import com.example.ui.theme.RoseBorder
import com.example.ui.theme.Rose950
import com.example.ui.theme.SurfaceTile
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc500
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800
import com.example.ui.theme.Zinc900

@Composable
fun KanbanScreen(
    tasks: List<TaskEntity>,
    activeColumn: String,
    isMasked: Boolean,
    onColumnChange: (String) -> Unit,
    onMoveTaskStatus: (TaskEntity, String) -> Unit,
    onOpenNewTaskDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = listOf(
        "BACKLOG" to "Backlog",
        "TODO" to "To Do",
        "IN_PROGRESS" to "In Progress",
        "DONE" to "Done"
    )

    val currentColumnTasks = tasks.filter { it.status.equals(activeColumn, ignoreCase = true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian950)
            .testTag("kanban_board_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Column Switcher Segmented Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                columns.forEach { (colKey, colName) ->
                    val isSelected = activeColumn.equals(colKey, ignoreCase = true)
                    val count = tasks.count { it.status.equals(colKey, ignoreCase = true) }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Zinc100 else Obsidian900)
                            .border(
                                1.dp,
                                if (isSelected) Zinc100 else Obsidian700,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onColumnChange(colKey) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("kanban_col_$colKey")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = colName,
                                color = if (isSelected) Obsidian950 else Zinc400,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) Obsidian900 else Obsidian800)
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = count.toString(),
                                    color = if (isSelected) Zinc100 else Zinc400,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Tasks in Active Column
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (currentColumnTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Zinc500,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "No cards in this column",
                                    color = Zinc400,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                items(currentColumnTasks, key = { it.id }) { task ->
                    KanbanCardItem(
                        task = task,
                        isMasked = isMasked,
                        activeColumn = activeColumn,
                        onMoveStatus = { nextStatus -> onMoveTaskStatus(task, nextStatus) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onOpenNewTaskDialog,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 20.dp)
                .testTag("kanban_new_card_button"),
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
                    contentDescription = "New Card",
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Add Card",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun KanbanCardItem(
    task: TaskEntity,
    isMasked: Boolean,
    activeColumn: String,
    onMoveStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val shouldMask = isMasked || task.isVaultLocked && isMasked

    val priorityPipColor = when (task.priority) {
        "HIGH" -> Rose500
        "MEDIUM" -> Amber500
        else -> Emerald500
    }

    val priorityTextColor = when (task.priority) {
        "HIGH" -> Rose400
        "MEDIUM" -> Amber400
        else -> Emerald400
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

    val subtaskProgress = if (task.subtasksCount > 0) {
        task.subtasksCompleted.toFloat() / task.subtasksCount.toFloat()
    } else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceTile)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("kanban_card_${task.id}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header: Priority, Lock, Tag
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
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(priorityPipColor)
                    )
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
                    if (task.isVaultLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Vault Locked",
                            tint = Amber400,
                            modifier = Modifier.size(12.dp)
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
                        text = "#${task.tag}",
                        color = Zinc400,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Title
            Text(
                text = if (shouldMask) "••••••••••••••••" else task.title,
                color = Zinc100,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = if (shouldMask) Modifier.blur(4.dp) else Modifier
            )

            // Subtask progress meter
            if (task.subtasksCount > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Subtasks",
                            color = Zinc500,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${task.subtasksCompleted}/${task.subtasksCount}",
                            color = Cyan400,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    LinearProgressIndicator(
                        progress = { subtaskProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Cyan400,
                        trackColor = Obsidian800
                    )
                }
            }

            // Footer: Due Date and Move Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Due: ${task.dueDate}",
                    color = Zinc500,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )

                // Quick Move Status buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val prevStatus = when (activeColumn) {
                        "TODO" -> "BACKLOG"
                        "IN_PROGRESS" -> "TODO"
                        "DONE" -> "IN_PROGRESS"
                        else -> null
                    }
                    val nextStatus = when (activeColumn) {
                        "BACKLOG" -> "TODO"
                        "TODO" -> "IN_PROGRESS"
                        "IN_PROGRESS" -> "DONE"
                        else -> null
                    }

                    if (prevStatus != null) {
                        IconButton(
                            onClick = { onMoveStatus(prevStatus) },
                            modifier = Modifier
                                .size(26.dp)
                                .background(Obsidian800, RoundedCornerShape(6.dp))
                                .border(1.dp, Obsidian700, RoundedCornerShape(6.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Move to $prevStatus",
                                tint = Zinc400,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    if (nextStatus != null) {
                        IconButton(
                            onClick = { onMoveStatus(nextStatus) },
                            modifier = Modifier
                                .size(26.dp)
                                .background(Obsidian800, RoundedCornerShape(6.dp))
                                .border(1.dp, Obsidian700, RoundedCornerShape(6.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Move to $nextStatus",
                                tint = Zinc400,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
