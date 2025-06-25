package com.example.coinary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinary.data.model.FreelanceIncome
import com.example.coinary.data.model.Expense
import com.example.coinary.data.repository.FreelanceIncomeRepository
import com.example.coinary.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest // Importar collectLatest
import kotlinx.coroutines.launch

class AddMovementViewModel(
    private val freelanceIncomeRepository: FreelanceIncomeRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    // --- NUEVOS ESTADOS PARA MANEJAR LA ALERTA Y EL TOTAL DE INGRESOS ---
    private val _showExceedIncomeAlert = MutableStateFlow(false)
    val showExceedIncomeAlert: StateFlow<Boolean> = _showExceedIncomeAlert

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome // Expone el total de ingresos

    // Estado temporal para guardar los detalles del gasto si se activa la alerta
    // para que puedan ser usados en la confirmación.
    private var pendingExpenseAmount: Double = 0.0
    private var pendingExpenseCategory: String = ""
    private var pendingExpenseDescription: String = ""
    // ------------------------------------------------------------------

    init {
        // Observa los cambios en los ingresos y actualiza el total
        viewModelScope.launch {
            // ASUNCIÓN: freelanceIncomeRepository.getAllIncomes() devuelve un Flow<List<FreelanceIncome>>
            // SI ESTO NO EXISTE, NECESITARÁS CREARLO EN TU REPOSITORIO Y DAO.
            freelanceIncomeRepository.getAllIncomes().collectLatest { incomes ->
                _totalIncome.value = incomes.sumOf { it.amount }
                println("AddMovementViewModel: Total de ingresos actualizado: ${_totalIncome.value}")
            }
        }
    }

    // Función para guardar un nuevo ingreso freelance (existente)
    fun saveFreelanceIncome(amount: Double, category: String, description: String, onIncomeSaved: () -> Unit) {
        viewModelScope.launch {
            val currentDateInMillis = System.currentTimeMillis()

            val newIncome = FreelanceIncome(
                amount = amount,
                category = category,
                description = description,
                date = currentDateInMillis
            )
            freelanceIncomeRepository.addIncome(newIncome)
            println("AddMovementViewModel: Ingreso guardado: $newIncome")
            onIncomeSaved() // Notificar a la UI que el ingreso fue guardado
        }
    }

    // <-- FUNCIÓN PARA GUARDAR GASTOS (MODIFICADA PARA LA ALERTA)
    fun saveExpense(
        amount: Double,
        category: String,
        description: String,
        onExpenseSaved: () -> Unit // Callback para la UI
    ) {
        viewModelScope.launch {
            val currentIncome = _totalIncome.value // Obtiene el valor más reciente de los ingresos totales

            if (amount > currentIncome) {
                // El gasto supera los ingresos, activar la alerta
                pendingExpenseAmount = amount
                pendingExpenseCategory = category
                pendingExpenseDescription = description
                _showExceedIncomeAlert.value = true // Activa el estado para mostrar la alerta en la UI
                println("AddMovementViewModel: Alerta de gasto excesivo activada.")
            } else {
                // El gasto es aceptable, guardar directamente
                val newExpense = Expense(
                    amount = amount,
                    category = category,
                    description = description
                )
                expenseRepository.addExpense(newExpense)
                println("AddMovementViewModel: Gasto guardado normalmente: $newExpense")
                onExpenseSaved() // Notificar a la UI que el gasto fue guardado
            }
        }
    }

    // --- NUEVAS FUNCIONES PARA LA INTERACCIÓN CON LA ALERTA ---

    // Función para confirmar y guardar el gasto a pesar de que exceda los ingresos
    fun confirmSaveExpenseDespiteExceeding(onExpenseSaved: () -> Unit) {
        viewModelScope.launch {
            val newExpense = Expense(
                amount = pendingExpenseAmount,
                category = pendingExpenseCategory,
                description = pendingExpenseDescription
            )
            expenseRepository.addExpense(newExpense)
            println("AddMovementViewModel: Gasto guardado a pesar de exceder ingresos: $newExpense")
            _showExceedIncomeAlert.value = false // Oculta la alerta
            onExpenseSaved() // Notificar a la UI que el gasto fue guardado
        }
    }

    // Función para cancelar la acción de gasto y ocultar la alerta
    fun dismissExceedIncomeAlert() {
        _showExceedIncomeAlert.value = false
        // Opcional: limpiar los datos pendientes si el usuario decide cancelar
        pendingExpenseAmount = 0.0
        pendingExpenseCategory = ""
        pendingExpenseDescription = ""
        println("AddMovementViewModel: Alerta de gasto excesivo descartada.")
    }
    // --------------------------------------------------------
}