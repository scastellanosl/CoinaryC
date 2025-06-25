package com.example.coinary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val amount: Double,
    val category: String, // Ej: Food, Transport, Entertainment, Health, Other
    val description: String = "",
    val date: Long = System.currentTimeMillis() // Fecha y hora del gasto en milisegundos
)