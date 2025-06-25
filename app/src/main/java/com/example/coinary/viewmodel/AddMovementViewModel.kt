package com.example.coinary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinary.data.model.FreelanceIncome
import com.example.coinary.data.model.Expense // <-- ¡NUEVA IMPORTACIÓN!
import com.example.coinary.data.repository.FreelanceIncomeRepository
import com.example.coinary.data.repository.ExpenseRepository // <-- ¡NUEVA IMPORTACIÓN!
import kotlinx.coroutines.launch

class AddMovementViewModel(
    private val freelanceIncomeRepository: FreelanceIncomeRepository, // Renombrado para claridad
    private val expenseRepository: ExpenseRepository // <-- ¡NUEVA INYECCIÓN DE DEPENDENCIA!
) : ViewModel() {

    // Función para guardar un nuevo ingreso freelance (existente)
    fun saveFreelanceIncome(amount: Double, category: String, description: String) {
        viewModelScope.launch {
            // Obtener la fecha actual en milisegundos
            val currentDateInMillis = System.currentTimeMillis() // O la fecha seleccionada por el usuario

            val newIncome = FreelanceIncome(
                amount = amount,
                category = category,
                description = description,
                date = currentDateInMillis // <-- ¡AÑADE ESTA LÍNEA!
            )
            freelanceIncomeRepository.addIncome(newIncome)
            println("AddMovementViewModel: Ingreso preparado para guardar: $newIncome")
        }
    }

    // <-- ¡NUEVA FUNCIÓN PARA GUARDAR GASTOS!
    fun saveExpense(amount: Double, category: String, description: String) {
        viewModelScope.launch {
            val newExpense = Expense(
                amount = amount,
                category = category,
                description = description
            )
            expenseRepository.addExpense(newExpense)
            println("AddMovementViewModel: Gasto preparado para guardar: $newExpense")
        }
    }
}