package com.example.coinary.data.repository

import com.example.coinary.data.model.Bank
import com.example.coinary.data.model.TransactionUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class BankRepositoryImpl @Inject constructor() : BankRepository {

    override fun getColombianBanks(): List<Bank> {
        return listOf(
            Bank("1", "Bancolombia", "https://www.bancolombia.com/"),
            Bank("2", "Davivienda", "https://www.davivienda.com/"),
            Bank("3", "Banco de Bogotá", "https://www.bancodebogota.com/"),
            Bank("4", "BBVA Colombia", "https://www.bbva.com.co/"),
            Bank("5", "Banco Popular", "https://www.bancopopular.com.co/"),
            Bank("6", "Banco Agrario", "https://www.bancoagrario.gov.co/"),
            Bank("7", "Colpatria", "https://www.colpatria.com/"),
            Bank("8", "Banco de Occidente", "https://www.bancodeoccidente.com.co/"),
            Bank("9", "AV Villas", "https://www.avvillas.com.co/"),
            Bank("10", "Banco Caja Social", "https://www.bancocajasocial.com/"),
            Bank("11", "Itaú Colombia", "https://www.itau.co/"),
            Bank("12", "Scotiabank Colpatria", "https://www.scotiabankcolpatria.com/"),
            Bank("13", "GNB Sudameris", "https://www.gnbsudameris.com.co/"),
            Bank("14", "Banco Falabella", "https://www.bancofalabella.com.co/"),
            Bank("15", "Banco Pichincha", "https://www.bancopichincha.com.co/"),
            Bank("16", "Banco Serfinanza", "https://www.bancoserfinanza.com/"),
            Bank("17", "Banco W", "https://www.bancow.com.co/")
        )
    }

    override suspend fun sendAggregatorTokenToYourBackend(token: String) {
        println("BankRepositoryImpl: Simulando envío de token '$token' a tu backend.")
        // TODO("Implementar la llamada real a tu Backend API para enviar el token del agregador")
        // No hay problema en dejar este TODO, ya que esta función no se llama en el 'init' de ProfileViewModel.
        // Se llamaría solo si realmente implementas el flujo de conexión de agregador.
    }

    override fun getRealtimeUpdates(): Flow<TransactionUpdate> {
        println("BankRepositoryImpl: Proporcionando un Flow conceptual para actualizaciones en tiempo real.")
        // *** CAMBIO AQUÍ: Elimina el TODO() para evitar el crash ***
        // TODO("Implementar la lógica real para obtener actualizaciones en tiempo real (ej. WebSocket client)")
        return emptyFlow() // ¡Esto evitará el crash!
    }
}