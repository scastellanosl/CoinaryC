package com.example.coinary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "freelance_incomes") // Declara esta clase como una entidad de Room
data class FreelanceIncome(
    @PrimaryKey // Define 'id' como la clave primaria de la tabla
    val id: String = UUID.randomUUID().toString(), // Genera un ID único automáticamente
    val amount: Double,
    val category: String,
    val description: String,
    val date: Long = System.currentTimeMillis()
)