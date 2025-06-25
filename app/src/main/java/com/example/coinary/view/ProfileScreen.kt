package com.example.coinary.view

import android.app.TimePickerDialog
import android.content.Context // Importar Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coinary.R
import com.example.coinary.view.GoogleAuthClient
import com.example.coinary.viewmodel.ProfileViewModel
import com.example.coinary.data.repository.BankRepositoryImpl
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.lifecycle.viewmodel.compose.viewModel

// Importar AggregatorResult y TransactionUpdate desde el paquete data.model
import com.example.coinary.data.model.AggregatorResult
import com.example.coinary.data.model.TransactionUpdate

// -------------------------------------------------------------------------
// NEW: Mueve esta función fuera del Composable ProfileScreen
// -------------------------------------------------------------------------
// Función para iniciar el flujo del agregador (lanza la UI del agregador)
// Se pasa el ViewModel para que esta función pueda reportar el resultado de la simulación
fun startAggregatorConnectionFlow(context: Context, userId: String, viewModel: ProfileViewModel) {
    Log.d("ProfileScreen", "UI: Iniciando flujo de conexión del agregador para el usuario: $userId (simulado).")
    // Aquí iría el código real para lanzar el SDK o WebView del agregador.
    // Por ejemplo: AggregatorSDK.launch(context, userId, this::handleAggregatorCallback)
    // O si usa una ActivityResultLauncher:
    // val intent = AggregatorSDK.getLaunchIntent(context, userId)
    // someActivityResultLauncher.launch(intent)

    // --- SIMULACIÓN DEL CALLBACK DEL AGREGADOR ---
    // Para la demostración, simulamos un éxito inmediato.
    // En un caso real, esto ocurriría después de que el usuario interactúe con el agregador.
    val simulatedAccessToken = "simulated_aggregator_access_token_${System.currentTimeMillis()}"
    Log.d("ProfileScreen", "UI: Simulando callback exitoso del agregador con token: $simulatedAccessToken")
    viewModel.onAggregatorConnectionSuccess(simulatedAccessToken)

    // Para simular un fallo, podrías llamar:
    // viewModel.onAggregatorConnectionFailure("Simulación de error de conexión del agregador.")

    // TODO: Implementar la llamada real al SDK/WebView del agregador y manejar su resultado real.
}
// -------------------------------------------------------------------------
// FIN NEW: Mueve esta función fuera del Composable ProfileScreen
// -------------------------------------------------------------------------


@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            val applicationContext = LocalContext.current.applicationContext
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val googleAuthClient = GoogleAuthClient(applicationContext)
                val bankRepository = BankRepositoryImpl()
                return ProfileViewModel(bankRepository, googleAuthClient) as T
            }
        }
    )
) {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = Color.Black
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.toFloat()
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.toFloat()
    val context = LocalContext.current

    val user by profileViewModel.signedInUser.collectAsState()
    val hour by profileViewModel.reminderHour.collectAsState()
    val minute by profileViewModel.reminderMinute.collectAsState()
    val currentpass by profileViewModel.currentPassword.collectAsState()
    val newpass by profileViewModel.newPassword.collectAsState()
    val confirmnewpass by profileViewModel.confirmNewPassword.collectAsState()
    val colombianBanks by profileViewModel.colombianBanks.collectAsState()
    val singleEvent by profileViewModel.singleEvent.collectAsState()

    val currentOnLogout by rememberUpdatedState(onLogout)

    LaunchedEffect(singleEvent) {
        singleEvent?.let { event ->
            when (event) {
                is ProfileViewModel.ProfileScreenEvent.OpenUrl -> {
                    Log.d("LaunchedEffect", "Abriendo URL: ${event.url}")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                    context.startActivity(intent)
                }
                is ProfileViewModel.ProfileScreenEvent.ShowToast -> {
                    Log.d("LaunchedEffect", "Mostrando Toast: ${event.message}")
                    // Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                // Manejar el evento para iniciar el flujo del agregador
                is ProfileViewModel.ProfileScreenEvent.StartAggregatorFlow -> {
                    Log.d("LaunchedEffect", "UI: Recibido evento para iniciar flujo del agregador para usuario: ${event.userId}")
                    // Ahora sí se puede llamar a la función que movimos fuera del Composable
                    startAggregatorConnectionFlow(context, event.userId, profileViewModel)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d("ProfileScreen", "Attempting to start observing real-time updates (conceptual).")
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                profileViewModel.onSetReminderTime(selectedHour, selectedMinute)
            },
            hour,
            minute,
            true
        )
    }

    val scrollState = rememberScrollState()

    // --- ESQUEMA DE FUNCIONES CONCEPTUALES ---

    // Función que se llama cuando el usuario pulsa el botón en la UI
    fun onConnectBankAccountClicked() {
        Log.d("ProfileScreen", "UI: Botón 'Conectar Cuenta Bancaria' pulsado.")
        // Delegar la intención al ViewModel.
        profileViewModel.onConnectBankAccountIntent()
    }

    // Las funciones handleAggregatorCallback y onRealtimeTransactionUpdate ya no son necesarias aquí.
    // ... (El resto de tu código de UI permanece igual) ...

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(text = "Profile configuration", color = Color.White)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 3.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { navController.navigate("notifications") }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(top = 5.dp)
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.darker_background),
                contentDescription = "User icon",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = (screenHeight * 0.025f))
                        .fillMaxWidth(0.25f)
                        .aspectRatio(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user_icon),
                        contentDescription = "User icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    user?.username ?: "User",
                    fontWeight = FontWeight.Bold,
                    fontSize = (screenWidthDp * 0.042f).sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = screenHeight * 0.032f)
                        .fillMaxWidth(1f),
                    textAlign = TextAlign.Center
                )

                Column {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.fondo_contenedor_categoria),
                            contentDescription = "background for more info", modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = screenWidth * 0.048f)
                                .padding(top = screenHeight * 0.012f),
                            contentScale = ContentScale.Crop
                        )
                        Column {
                            Text(
                                text = "Your info",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = (screenWidthDp * 0.06f).sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(top = screenHeight * 0.038f)
                                    .fillMaxWidth(1f)
                            )

                            Text(
                                text = "Email",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = (screenWidthDp * 0.045f).sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(top = screenHeight * 0.012f)
                                    .fillMaxWidth(1f)
                            )

                            Text(
                                user?.userId ?: "mailusuario",
                                color = Color.White,
                                fontWeight = FontWeight.Thin,
                                fontSize = (screenWidthDp * 0.04f).sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(top = screenHeight * 0.004f)
                                    .fillMaxWidth(1f)
                            )

                            Text(
                                text = "Hour for daily reminder",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = (screenWidthDp * 0.048f).sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(top = screenHeight * 0.02f)
                                    .fillMaxWidth(1f)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = (screenHeightDp * 0.01f).dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(screenHeight * 0.06f)
                                        .clickable { timePickerDialog.show() }
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.button_background),
                                        contentDescription = "Hour background",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Text(
                                        text = "%02d".format(if (hour % 12 == 0) 12 else hour % 12),
                                        color = Color.White,
                                        fontSize = (screenWidthDp * 0.052f).sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }

                                Text(
                                    text = ":",
                                    color = Color.White,
                                    fontSize = (screenWidthDp * 0.105f).sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(screenHeight * 0.06f)
                                        .clickable { timePickerDialog.show() }
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.button_background),
                                        contentDescription = "Minute background",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Text(
                                        text = "%02d".format(minute),
                                        color = Color.White,
                                        fontSize = (screenWidthDp * 0.052f).sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }

                                Spacer(modifier = Modifier.width((screenWidth * 0.022f)))

                                Box(
                                    modifier = Modifier
                                        .size(screenHeight * 0.06f)
                                        .clickable { timePickerDialog.show() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.button_hour),
                                        contentDescription = "AM/PM background",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Text(
                                        text = if (hour < 12) "AM" else "PM",
                                        color = Color.White,
                                        fontSize = (screenWidthDp * 0.052f).sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "Wanna change your password?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = (screenWidthDp * 0.042f).sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(
                                        top = screenHeight * 0.02f,
                                        bottom = screenHeight * 0.012f
                                    )
                                    .fillMaxWidth(1f)
                            )

                            Column(
                                modifier = Modifier
                                    .padding(bottom = screenHeight * 0.012f)
                                    .fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = currentpass,
                                    onValueChange = { profileViewModel.onCurrentPasswordChange(it) },
                                    shape = RoundedCornerShape(10.dp),
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    placeholder = {
                                        Text(
                                            "Digit your actual password",
                                            color = Color.White,
                                            modifier = Modifier.fillMaxWidth(),
                                            fontSize = (screenWidthDp * 0.0275f).sp,
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = (screenWidthDp * 0.05f).sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier
                                        .width(screenWidth * 0.78f)
                                        .align(Alignment.CenterHorizontally)
                                        .height(screenHeight * 0.058f)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .padding(bottom = screenHeight * 0.004f)
                                    .fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = newpass,
                                    onValueChange = { profileViewModel.onNewPasswordChange(it) },
                                    shape = RoundedCornerShape(10.dp),
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    placeholder = {
                                        Text(
                                            text = "Digit your new password",
                                            color = Color.White,
                                            modifier = Modifier.fillMaxWidth(),
                                            fontSize = (screenWidthDp * 0.0275f).sp,
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = (screenWidthDp * 0.05f).sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier
                                        .width(screenWidth * 0.78f)
                                        .align(Alignment.CenterHorizontally)
                                        .height(screenHeight * 0.058f)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .padding(bottom = screenHeight * 0.012f)
                                    .fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = confirmnewpass,
                                    onValueChange = { profileViewModel.onConfirmNewPasswordChange(it) },
                                    shape = RoundedCornerShape(10.dp),
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    placeholder = {
                                        Text(
                                            "Confirm your password",
                                            color = Color.White,
                                            modifier = Modifier.fillMaxWidth(),
                                            fontSize = (screenWidthDp * 0.0275f).sp,
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = (screenWidthDp * 0.05f).sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier
                                        .width(screenWidth * 0.78f)
                                        .align(Alignment.CenterHorizontally)
                                        .height(screenHeight * 0.058f)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { profileViewModel.onSaveChangesClicked() },
                                    modifier = Modifier.height(36.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4D54BF),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "Save changes",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Button(
                                    onClick = {
                                        profileViewModel.onSignOutClicked(currentOnLogout)
                                    },
                                    modifier = Modifier.height(36.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFBF4D4D),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "Close session",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Sección de Bancos de Colombia
                            Text(
                                text = "Colombian Banks",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = (screenWidthDp * 0.048f).sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(top = screenHeight * 0.02f)
                                    .fillMaxWidth(1f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight * 0.3f)
                                    .padding(horizontal = screenWidth * 0.048f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(colombianBanks) { bank ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                Log.d("BankClick", "Se hizo clic en el banco: ${bank.name}")
                                                profileViewModel.onBankClick(bank)
                                            },
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF282C34)),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Text(
                                            text = bank.name,
                                            color = Color.White,
                                            fontSize = (screenWidthDp * 0.04f).sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }

                            // Botón para conectar cuenta bancaria
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onConnectBankAccountClicked() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = screenWidth * 0.048f)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Connect Your Bank Account (Conceptual)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (screenWidthDp * 0.04f).sp
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}