package com.example.coinary.data.model

// Tu clase Transaction existente
data class Transaction(
    val id: String,
    val accountId: String,
    val description: String,
    val amount: Double,
    val date: String // O un tipo de dato de fecha más robusto
)

// NEW: Clase de datos para actualizaciones de transacciones en tiempo real, movida aquí
data class TransactionUpdate(
    val id: String,
    val accountId: String,
    val description: String,
    val amount: Double,
    val date: String // O un tipo de dato de fecha más robusto
)

// NEW: Clase de datos conceptual para los resultados del agregador (también movida aquí para centralizar modelos)
sealed class AggregatorResult {
    data class Success(val accessToken: String) : AggregatorResult()
    data class Failure(val errorMessage: String) : AggregatorResult()
}