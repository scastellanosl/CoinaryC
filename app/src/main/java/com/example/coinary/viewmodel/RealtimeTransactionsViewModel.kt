package com.example.coinary.viewmodel

import androidx.lifecycle.ViewModel // Importa ViewModel
import androidx.lifecycle.viewModelScope // Importa viewModelScope
import com.example.coinary.data.model.Transaction // Importa Transaction
import com.example.coinary.data.model.TransactionUpdate // Importa TransactionUpdate
import com.example.coinary.data.repository.BankRepository // Importa BankRepository
import kotlinx.coroutines.flow.MutableStateFlow // Importa MutableStateFlow
import kotlinx.coroutines.flow.StateFlow // Importa StateFlow
import kotlinx.coroutines.flow.asStateFlow // Importa asStateFlow
import kotlinx.coroutines.flow.collectLatest // Importa collectLatest
import kotlinx.coroutines.launch // Importa launch
// No necesitamos @Inject si estamos usando una factory manual para este ViewModel

class RealtimeTransactionsViewModel constructor( // Constructor sin @Inject
    private val bankRepository: BankRepository // Aquí va la interfaz, no la implementación concreta
) : ViewModel() { // <-- ¡Importante! El ViewModel debe extender de androidx.lifecycle.ViewModel

    // StateFlow para exponer la lista de transacciones a la UI
    private val _realtimeTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val realtimeTransactions: StateFlow<List<Transaction>> = _realtimeTransactions.asStateFlow()

    init {
        // Inicia la observación de actualizaciones en tiempo real cuando el ViewModel es creado
        observeRealtimeUpdates()
    }

    private fun observeRealtimeUpdates() {
        println("RealtimeTransactionsViewModel: Iniciando observación de actualizaciones en tiempo real.")
        viewModelScope.launch {
            // bankRepository.getRealtimeUpdates() actualmente devuelve emptyFlow(),
            // por lo que _realtimeTransactions.value se mantendrá como emptyList()
            // hasta que se implemente la lógica real del WebSocket.
            bankRepository.getRealtimeUpdates().collectLatest { update ->
                println("RealtimeTransactionsViewModel: Actualización de transacción recibida (conceptualmente): $update")
                // Convertir TransactionUpdate a Transaction para la UI
                val newTransaction = Transaction(
                    id = update.id,
                    accountId = update.accountId,
                    description = update.description,
                    amount = update.amount,
                    date = update.date
                )
                // Aquí es donde se añadirían las transacciones reales.
                // Por ahora, como getRealtimeUpdates() es emptyFlow(), esto no se ejecutará.
                _realtimeTransactions.value = _realtimeTransactions.value + newTransaction
            }
        }
    }

    // Funciones adicionales si la pantalla de transacciones necesitara alguna interacción.
}