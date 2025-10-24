@file:Suppress("DEPRECATION")

package com.example.quizmobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizmobile.data.FirebaseRepository
import com.example.quizmobile.data.Task
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun getCurrentDateTime(): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID"))
    return sdf.format(Date())
}

@Composable
fun AddTaskModal(
    repo: FirebaseRepository,
    onDismiss: () -> Unit,
    onTaskAdded: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    val currentDateTime = remember { getCurrentDateTime() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss, // Panggil onDismiss saat klik luar
        title = { Text("Tambah Kegiatan Baru") },
        text = {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.Start) {
                // Field Nama Kegiatan
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Nama Kegiatan") },
                    placeholder = { Text("Masukkan deskripsi kegiatan...") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Teks Waktu Otomatis
                Text("Waktu Dibuat:", style = MaterialTheme.typography.labelSmall)
                Text(
                    currentDateTime,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskName.isNotBlank() && !isLoading) {
                        isLoading = true
                        error = null
                        scope.launch {
                            try {
                                val newTask = Task(
                                    id = UUID.randomUUID().toString(), // Generate ID
                                    name = taskName,
                                    date = currentDateTime,
                                    done = false
                                )
                                repo.addTask(newTask)
                                onTaskAdded() // Panggil callback sukses
                            } catch (e: Exception) {
                                error = "Gagal menyimpan: ${e.localizedMessage}"
                                isLoading = false
                            }
                        }
                    } else if (taskName.isBlank()) {
                        error = "Nama kegiatan tidak boleh kosong."
                    }
                },
                enabled = taskName.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Batal")
            }
        }
    )
}