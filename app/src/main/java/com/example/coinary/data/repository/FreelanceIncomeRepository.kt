package com.example.coinary.data.repository

import com.example.coinary.data.model.FreelanceIncome
import kotlinx.coroutines.flow.Flow

interface FreelanceIncomeRepository {
    // Función para agregar un nuevo ingreso
    suspend fun addIncome(income: FreelanceIncome)

    // Función para obtener todos los ingresos como un Flow (para actualizaciones en tiempo real)
    fun getAllIncomes(): Flow<List<FreelanceIncome>>
}