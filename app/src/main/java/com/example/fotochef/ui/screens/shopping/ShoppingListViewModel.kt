package com.example.fotochef.ui.screens.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotochef.data.local.entity.IngredientCompra
import com.example.fotochef.data.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {

    // Fecha seleccionada en el calendario, por defecto "hoy" a medianoche
    val selectedDate = MutableStateFlow(getStartOfDayTimestamp())

    val pendingItems: StateFlow<List<IngredientCompra>> = selectedDate.flatMapLatest { date ->
        shoppingListRepository.getItemsByStatusAndDate(false, date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val purchasedItems: StateFlow<List<IngredientCompra>> = selectedDate.flatMapLatest { date ->
        shoppingListRepository.getItemsByStatusAndDate(true, date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedDate(date: Long) {
        selectedDate.value = date
    }

    private fun getStartOfDayTimestamp(timeInMillis: Long = System.currentTimeMillis()): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun toggleStatus(id: Long, isComprat: Boolean) {
        viewModelScope.launch {
            shoppingListRepository.updateStatus(id, isComprat)
        }
    }

    fun deleteItem(item: IngredientCompra) {
        viewModelScope.launch {
            shoppingListRepository.deleteItem(item)
        }
    }

    fun clearPurchased() {
        viewModelScope.launch {
            shoppingListRepository.clearPurchased()
        }
    }

    fun addManualItem(name: String, quantity: String, unit: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val item = IngredientCompra(
                nom = name,
                quantitat = quantity,
                unitat = unit,
                scheduledDate = selectedDate.value
            )
            shoppingListRepository.addItem(item)
        }
    }
}
