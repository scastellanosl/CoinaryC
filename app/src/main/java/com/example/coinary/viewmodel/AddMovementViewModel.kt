package com.example.coinary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinary.data.model.FreelanceIncome
import com.example.coinary.data.repository.FreelanceIncomeRepository
import kotlinx.coroutines.launch

class AddMovementViewModel(
    private val repository: FreelanceIncomeRepository // Inyectamos la interfaz del repositorio
) : ViewModel() {

    // Función para guardar un nuevo ingreso freelance
    fun saveFreelanceIncome(amount: Double, category: String, description: String) {
        viewModelScope.launch {
            val newIncome = FreelanceIncome(
                amount = amount,
                category = category,
                description = description
            )
            repository.addIncome(newIncome)
            println("AddMovementViewModel: Ingreso preparado para guardar: $newIncome")
        }
    }
}