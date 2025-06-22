package com.fernan.pomodorotime.ui.habits

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fernan.pomodorotime.data.model.Habit
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.fernan.pomodorotime.ui.component.ConfirmationDialog


@Composable
fun HabitItem(
    habit: Habit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onViewHabit: () -> Unit,
    isDoneToday: Boolean,
) {
    val weekDays = listOf("L", "M", "X", "J", "V", "S", "D")
    val cornerSize = 12.dp

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current


    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(cornerSize),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp)
            .clickable { onViewHabit() }
            .shadow(4.dp, shape = RoundedCornerShape(cornerSize))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Fondo decorativo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        shape = RoundedCornerShape(topStart = cornerSize, topEnd = cornerSize)
                    )
            )

            // Botones de acción con efecto flotante
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp, end = 8.dp)
            ) {
                IconButton(
                    onClick = {showDeleteDialog = true},
                    modifier = Modifier
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                    )
                }

                Spacer(modifier = Modifier.width(1.dp))

                IconButton(
                    onClick = onClick,
                    modifier = Modifier
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            if (showDeleteDialog) {
                ConfirmationDialog(
                    title = "¿Eliminar hábito?",
                    message = "¿Estás seguro de que deseas eliminar este hábito? Esta acción no se puede deshacer.",
                    confirmButtonText = "Eliminar",
                    cancelButtonText = "Cancelar",
                    onConfirm = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    onDismiss = {
                        showDeleteDialog = false
                    }
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(top = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Checkbox circular personalizado
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDoneToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = 2.dp,
                                color = if (isDoneToday) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable {
                                if (!isDoneToday) {
                                    showConfirmDialog = true
                                } else {
                                     // pedir confirmación para desmarcar (que no se hará)
                                    Toast.makeText(
                                        context,
                                        "Este hábito ya fue marcado como hecho y no se puede desmarcar.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDoneToday) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completado",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isDoneToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f))
                }

                if (habit.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 36.dp)
                    )
                }

                // Sección de detalles
                if (!habit.reminderTime.isNullOrEmpty() || habit.activeDays.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 36.dp)
                            .background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!habit.reminderTime.isNullOrEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Hora de recordatorio",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = habit.reminderTime,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (habit.activeDays.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AlarmOn,
                                    contentDescription = "Dias de repeticion",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = habit.activeDays.sorted().joinToString(" ") { weekDays[it % 7] },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        ConfirmationDialog(
            title = "¿Marcar hábito como completado?",
            message = "¿Estás seguro de marcar este hábito como hecho manualmente? Lo ideal es completarlo con un pomodoro.",
            onConfirm = {
                showConfirmDialog = false
                onToggle() // Llama a tu lógica para marcar como completado
            },
            onDismiss = {
                showConfirmDialog = false
            }
        )
    }

}





