@file:Suppress("DEPRECATION")

package com.example.quizmobile.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun currentUser() = auth.currentUser

    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun signup(email: String, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getTasks(): List<Task> {
        val user = auth.currentUser ?: return emptyList()
        val snapshot = db.collection("tasks")
            .whereEqualTo("userId", user.uid)
            .get()
            .await()

        return snapshot.documents.map {
            Task(
                id = it.id,
                name = it.getString("name") ?: "",
                date = it.getString("date") ?: "",
                done = it.getBoolean("done") == true
            )
        }
    }

    suspend fun addTask(task: Task) {
        val user = auth.currentUser ?: return
        val taskData = hashMapOf(
            "name" to task.name,
            "date" to task.date,
            "done" to task.done,
            "userId" to user.uid
        )
        db.collection("tasks").add(taskData).await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateTaskStatus(id: String, done: Boolean) {
        val user = auth.currentUser ?: return

        val query = db.collection("tasks")
            .whereEqualTo("userId", user.uid)
            .whereEqualTo(FieldPath.documentId(), id)
            .get()
            .await()

        if (query.documents.isNotEmpty()) {
            val docId = query.documents.first().id
            db.collection("tasks").document(docId).update("done", done).await()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteTask(id: String) {
        val user = auth.currentUser ?: return

        val query = db.collection("tasks")
            .whereEqualTo("userId", user.uid)
            .whereEqualTo(FieldPath.documentId(), id)
            .get()
            .await()

        if (query.documents.isNotEmpty()) {
            val docId = query.documents.first().id
            db.collection("tasks").document(docId).delete().await()
        }
    }

}
