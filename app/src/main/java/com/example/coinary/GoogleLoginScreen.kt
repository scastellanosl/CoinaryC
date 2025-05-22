package com.example.coinary

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily


val ZenDots = FontFamily(Font(R.font.zen_dots))

@Composable
fun GoogleLoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val googleAuthClient = remember { GoogleAuthClient(context) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (googleAuthClient.getSignedInUser() != null) {
            onLoginSuccess()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isLoading = true
        coroutineScope.launch {
            try {
                val user = googleAuthClient.signInWithIntent(result.data ?: return@launch)
                if (user.isSuccess) {
                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "Error al iniciar sesión: ${user.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Parte superior amarilla con logo y texto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f) // Menos de la mitad
                    .clip(RoundedCornerShape(bottomEnd = 90.dp)) // Curvatura solo en la esquina inferior derecha
                    .background(Color(0xFFFFF89D)) // Amarillo claro
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo_coinary),
                        contentDescription = "Logo Coinary",
                        modifier = Modifier.size(170.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Welcome to Coinary",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = ZenDots,
                        color = Color(0xFF150F33),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }

            // Parte inferior blanca con botón
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                } else {
                    Button(
                        onClick = {
                            isLoading = true
                            launcher.launch(googleAuthClient.getSignInIntent())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4D54BF)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Continue with Google",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google Icon",
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterStart), // Icono a la izquierda
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val googleAuthClient = remember { GoogleAuthClient(context) }
    val coroutineScope = rememberCoroutineScope()
    val user = googleAuthClient.getSignedInUser()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido, ${user?.username ?: "Usuario"}!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    googleAuthClient.signOut()
                    onLogout()
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Cerrar Sesión")
        }
    }
}
