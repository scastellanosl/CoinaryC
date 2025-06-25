package com.example.coinary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinary.data.model.Bank
import com.example.coinary.data.repository.BankRepository
import com.example.coinary.view.GoogleAuthClient
import com.example.coinary.view.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.coinary.data.model.AggregatorResult
import com.example.coinary.data.model.Transaction
import com.example.coinary.data.model.TransactionUpdate

class ProfileViewModel @Inject constructor(
    private val bankRepository: BankRepository,
    private val googleAuthClient: GoogleAuthClient
) : ViewModel() {

    private val _signedInUser = MutableStateFlow(googleAuthClient.getSignedInUser())
    val signedInUser: StateFlow<FirebaseUser?> = _signedInUser.asStateFlow()

    private val _colombianBanks = MutableStateFlow<List<Bank>>(emptyList())
    val colombianBanks: StateFlow<List<Bank>> = _colombianBanks.asStateFlow()

    private val _reminderHour = MutableStateFlow(18)
    val reminderHour: StateFlow<Int> = _reminderHour.asStateFlow()

    private val _reminderMinute = MutableStateFlow(0)
    val reminderMinute: StateFlow<Int> = _reminderMinute.asStateFlow()

    private val _currentPassword = MutableStateFlow("")
    val currentPassword: StateFlow<String> = _currentPassword.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmNewPassword = MutableStateFlow("")
    val confirmNewPassword: StateFlow<String> = _confirmNewPassword.asStateFlow()

    private val _singleEvent = MutableStateFlow<ProfileScreenEvent?>(null)
    val singleEvent: StateFlow<ProfileScreenEvent?> = _singleEvent.asStateFlow()

    private val _realtimeTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val realtimeTransactions: StateFlow<List<Transaction>> = _realtimeTransactions.asStateFlow()


    init {
        loadColombianBanks()
        observeRealtimeUpdates()
    }

    private fun loadColombianBanks() {
        viewModelScope.launch {
            _colombianBanks.value = bankRepository.getColombianBanks()
        }
    }

    fun onBankClick(bank: Bank) {
        viewModelScope.launch {
            _singleEvent.value = ProfileScreenEvent.OpenUrl(bank.websiteUrl)
        }
    }

    fun onSetReminderTime(hour: Int, minute: Int) {
        _reminderHour.value = hour
        _reminderMinute.value = minute
    }

    fun onCurrentPasswordChange(password: String) {
        _currentPassword.value = password
    }

    fun onNewPasswordChange(password: String) {
        _newPassword.value = password
    }

    fun onConfirmNewPasswordChange(password: String) {
        _confirmNewPassword.value = password
    }

    fun onSaveChangesClicked() {
        // Lógica para guardar los cambios
    }

    fun onSignOutClicked(onLogout: () -> Unit) {
        viewModelScope.launch {
            googleAuthClient.signOut()
            onLogout()
        }
    }

    // --- ESQUEMA DE FUNCIONES CONCEPTUALES PARA CONEXIÓN Y TIEMPO REAL ---

    // 1. Función para la intención de conectar cuenta bancaria (llamada desde la UI)
    fun onConnectBankAccountIntent() {
        println("ViewModel: Intent de conectar cuenta bancaria recibido. Indicando a la UI que lance el flujo del agregador.")
        val userId = signedInUser.value?.userId // Obtén el ID de usuario si es necesario para el agregador
        // NEW: Emitir un evento para que la UI lance el flujo del agregador
        // Asumiendo que el agregador necesita un userId para iniciar su flujo.
        _singleEvent.value = ProfileScreenEvent.StartAggregatorFlow(userId ?: "default_user_id")
    }

    // 2. Función que maneja el resultado exitoso de la conexión del agregador (llamada desde la UI)
    fun onAggregatorConnectionSuccess(accessToken: String) {
        println("ViewModel: Conexión con agregador exitosa. Enviando token a tu backend.")
        viewModelScope.launch {
            try {
                bankRepository.sendAggregatorTokenToYourBackend(accessToken)
                println("ViewModel: Token del agregador enviado a tu backend con éxito.")
                _singleEvent.value = ProfileScreenEvent.ShowToast("Cuenta bancaria conectada!")
                observeRealtimeUpdates() // NEW: Iniciar la observación después de la conexión
            } catch (e: Exception) {
                println("ViewModel: Error al enviar token a tu backend: ${e.message}")
                _singleEvent.value = ProfileScreenEvent.ShowToast("Error al conectar cuenta: ${e.message}")
            } finally {
                _singleEvent.value = null // Reiniciar el evento después de ser consumido
            }
        }
    }

    // 3. Función que maneja el resultado fallido de la conexión del agregador (llamada desde la UI)
    fun onAggregatorConnectionFailure(errorMessage: String) {
        println("ViewModel: Conexión con agregador fallida: $errorMessage")
        viewModelScope.launch {
            _singleEvent.value = ProfileScreenEvent.ShowToast("Fallo al conectar cuenta: $errorMessage")
            _singleEvent.value = null
        }
    }

    // 4. Función para iniciar la observación de actualizaciones en tiempo real
    fun observeRealtimeUpdates() {
        println("ViewModel: Iniciando observación de actualizaciones en tiempo real desde el repositorio...")
        viewModelScope.launch {
            bankRepository.getRealtimeUpdates().collectLatest { update ->
                println("ViewModel: Actualización en tiempo real recibida: $update")
                val currentTransactions = _realtimeTransactions.value.toMutableList()
                // Convertir TransactionUpdate a Transaction (si no son exactamente iguales)
                val newTransaction = Transaction(update.id, update.accountId, update.description, update.amount, update.date)
                _realtimeTransactions.value = _realtimeTransactions.value + newTransaction
            }
        }
    }

    // --- FIN ESQUEMA DE FUNCIONES CONCEPTUALES ---

    // Eventos de una sola vez que la vista puede observar
    sealed class ProfileScreenEvent {
        data class OpenUrl(val url: String) : ProfileScreenEvent()
        data class ShowToast(val message: String) : ProfileScreenEvent()
        // NEW: Evento para indicar a la UI que lance el flujo del agregador
        data class StartAggregatorFlow(val userId: String) : ProfileScreenEvent()
    }
}