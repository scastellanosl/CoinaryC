package com.example.coinary.data.repository

import com.example.coinary.data.model.Bank
import com.example.coinary.data.model.TransactionUpdate // NEW: Importar TransactionUpdate desde data.model
import kotlinx.coroutines.flow.Flow

interface BankRepository {
    fun getColombianBanks(): List<Bank>

    // --- Funciones conceptuales añadidas ---

    /**
     * Envía el token de acceso del agregador financiero a tu propio backend.
     * Tu backend será el encargado de gestionar este token y obtener datos bancarios.
     * @param token El token de acceso proporcionado por el agregador financiero.
     */
    suspend fun sendAggregatorTokenToYourBackend(token: String)

    /**
     * Proporciona un Flow de actualizaciones de transacciones en tiempo real.
     * Este Flow emitirá nuevas transacciones o cambios a medida que ocurran.
     */
    fun getRealtimeUpdates(): Flow<TransactionUpdate>
}