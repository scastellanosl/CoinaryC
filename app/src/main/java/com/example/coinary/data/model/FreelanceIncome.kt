package com.example.coinary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "freelance_income_table") // Nombre de la tabla
data class FreelanceIncome(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val description: String,
    val date: Long, // Importante para las consultas por fecha
    val category: String? = null // Si tienes una categoría
    // ... otros campos
)