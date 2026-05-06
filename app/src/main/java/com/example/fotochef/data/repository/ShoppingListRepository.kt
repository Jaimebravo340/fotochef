package com.example.fotochef.data.repository

import com.example.fotochef.data.local.dao.ShoppingListDao
import com.example.fotochef.data.local.entity.IngredientCompra
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class ShoppingListRepository(
    private val shoppingListDao: ShoppingListDao
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId: String? get() = auth.currentUser?.uid

    fun getAllItems(): Flow<List<IngredientCompra>> {
        val uid = userId ?: return flowOf(emptyList())
        return callbackFlow {
            val listener = firestore.collection("users").document(uid)
                .collection("shopping_list")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val items = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(IngredientCompra::class.java)?.copy(id = doc.id.hashCode().toLong())
                    } ?: emptyList()
                    trySend(items)
                }
            awaitClose { listener.remove() }
        }
    }
    
    fun getItemsByStatus(isComprat: Boolean): Flow<List<IngredientCompra>> {
        val uid = userId ?: return flowOf(emptyList())
        return callbackFlow {
            val listener = firestore.collection("users").document(uid)
                .collection("shopping_list")
                .whereEqualTo("comprat", isComprat)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val items = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(IngredientCompra::class.java)?.copy(id = doc.id.hashCode().toLong())
                    } ?: emptyList()
                    trySend(items)
                }
            awaitClose { listener.remove() }
        }
    }

    fun getItemsByStatusAndDate(isComprat: Boolean, date: Long): Flow<List<IngredientCompra>> {
        val uid = userId ?: return flowOf(emptyList())
        return callbackFlow {
            val listener = firestore.collection("users").document(uid)
                .collection("shopping_list")
                .whereEqualTo("comprat", isComprat)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val items = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(IngredientCompra::class.java)?.copy(id = doc.id.hashCode().toLong())
                    }?.filter { it.scheduledDate == null || it.scheduledDate == date } ?: emptyList()
                    trySend(items)
                }
            awaitClose { listener.remove() }
        }
    }

    suspend fun addItem(item: IngredientCompra) {
        val uid = userId ?: return
        // Si el ID es 0 o nulo, dejamos que Firestore genere uno único
        val docId = if (item.id == 0L) {
            firestore.collection("users").document(uid).collection("shopping_list").document().id
        } else {
            item.id.toString()
        }
        
        firestore.collection("users").document(uid)
            .collection("shopping_list").document(docId)
            .set(item.copy(id = docId.hashCode().toLong())) // Guardamos un hash numérico para compatibilidad con la UI si hace falta
            .await()
    }
    
    suspend fun addItems(items: List<IngredientCompra>) {
        val uid = userId ?: return
        val batch = firestore.batch()
        items.forEach { item ->
            val docId = if (item.id == 0L) {
                firestore.collection("users").document(uid).collection("shopping_list").document().id
            } else {
                item.id.toString()
            }
            val docRef = firestore.collection("users").document(uid)
                .collection("shopping_list").document(docId)
            batch.set(docRef, item.copy(id = docId.hashCode().toLong()))
        }
        batch.commit().await()
    }

    suspend fun updateStatus(id: Long, isComprat: Boolean) {
        val uid = userId ?: return
        val doc = firestore.collection("users").document(uid)
            .collection("shopping_list")
            .whereEqualTo("id", id)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return

        doc.reference.update("comprat", isComprat).await()
    }

    suspend fun deleteItem(item: IngredientCompra) {
        val uid = userId ?: return
        val doc = firestore.collection("users").document(uid)
            .collection("shopping_list")
            .whereEqualTo("id", item.id)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return

        doc.reference.delete().await()
    }
    
    suspend fun deleteById(id: Long) {
        val uid = userId ?: return
        val doc = firestore.collection("users").document(uid)
            .collection("shopping_list")
            .whereEqualTo("id", id)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return

        doc.reference.delete().await()
    }

    suspend fun clearPurchased() {
        val uid = userId ?: return
        val query = firestore.collection("users").document(uid)
            .collection("shopping_list")
            .whereEqualTo("comprat", true)
            .get().await()
        
        val batch = firestore.batch()
        query.documents.forEach { doc ->
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }
}
