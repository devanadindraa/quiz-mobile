package com.example.quizmobile.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizmobile.data.FirebaseRepository
import com.example.quizmobile.data.Task
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskList(repo: FirebaseRepository) {
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var isShowingAddModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        scope.launch {
            tasks = repo.getTasks()
            isLoading = false
        }
    }

    val refreshTasks: () -> Unit = {
        scope.launch {
            tasks = repo.getTasks()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Daftar Kegiatan") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isShowingAddModal = true
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Tambah Task"
                )
            }
        }
    ) { padding ->
        if (isShowingAddModal) {
            AddTaskModal(
                repo = repo,
                onDismiss = { isShowingAddModal = false },
                onTaskAdded = {
                    isShowingAddModal = false
                    refreshTasks()
                }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Tidak ada kegiatan. Klik + untuk menambah.", style = MaterialTheme.typography.bodyLarge) }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onCheckedChange = { checked ->
                            scope.launch {
                                repo.updateTaskStatus(task.id, checked)
                                refreshTasks()
                            }
                        },
                        onDelete = {
                            scope.launch {
                                repo.deleteTask(task.id)
                                refreshTasks()
                            }
                        }
                    )
                }
            }
        }
    }
}