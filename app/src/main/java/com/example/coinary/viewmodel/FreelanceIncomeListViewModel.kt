package com.example.coinary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinary.data.model.FreelanceIncome
import com.example.coinary.data.repository.FreelanceIncomeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FreelanceIncomeListViewModel(
    private val repository: FreelanceIncomeRepository // Inyectamos la interfaz del repositorio
) : ViewModel() {

    // Exponemos la lista de ingresos como un StateFlow
    val freelanceIncomes: StateFlow<List<FreelanceIncome>> =
        repository.getAllIncomes() // Obtiene el Flow de ingresos del repositorio
            .map { incomes -> // Puedes transformar los datos si es necesario antes de exponerlos
                incomes.sortedByDescending { it.date } // Asegura que estén ordenados por fecha descendente
            }
            .stateIn( // Convierte el Flow en un StateFlow
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // Inicia la recolección cuando hay suscriptores y la detiene 5 segundos después de que no haya ninguno
                initialValue = emptyList() // Valor inicial para el StateFlow
            )

    // Si necesitaras alguna otra función de lógica de negocio para la lista, iría aquí.
}