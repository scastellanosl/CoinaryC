@file:Suppress("DEPRECATION")

package com.example.coinary

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


val InterFont = FontFamily(Font(R.font.inter))

@Composable
fun GoogleLoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
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

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen collage encima del logo
            Image(
                painter = painterResource(id = R.drawable.ic_images), // Asegúrate de tener esta imagen
                contentDescription = "Collage Coinary",
                modifier = Modifier
                    .fillMaxWidth(1f),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Manage your plans\nwith Coinary",
            fontFamily = InterFont,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 30.sp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Email y password
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = "Email Address",
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = Color(0xFF868686)
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 20.dp)
                    .background(Color.Black)
                    .border(1.dp, color = Color.White, RoundedCornerShape(12.dp)),
                textStyle = TextStyle(
                    fontFamily = InterFont,
                    color = Color.White,
                    fontSize = 16.sp
                ),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = "Password",
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = Color(0xFF868686)
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 20.dp)
                    .background(Color.Black)
                    .border(1.dp, color = Color.White, RoundedCornerShape(12.dp)),
                textStyle = TextStyle(
                    fontFamily = InterFont,
                    color = Color.White,
                ),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        var isLoadingContinue by remember { mutableStateOf(false) }
        var isLoadingGoogle by remember { mutableStateOf(false) }

// Botón Continue
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoadingContinue) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else {
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoadingContinue = true
                            val auth = FirebaseAuth.getInstance()
                            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                .addOnCompleteListener { task ->
                                    isLoadingContinue = false
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        if (user != null) {  // Sin verificación de email
                                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        } else {
                                            Toast.makeText(context, "Error inesperado, intenta de nuevo", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4D54BF)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(45.dp)
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        fontFamily = InterFont
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

// Botón de Google
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoadingGoogle) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else {
                Button(
                    onClick = {
                        isLoadingGoogle = true
                        launcher.launch(googleAuthClient.getSignInIntent())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757569)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(45.dp)
                )
                {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Continue with Google",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            fontFamily = InterFont,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Icon",
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.CenterStart),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val annotatedText = buildAnnotatedString {
            append("Don't have an account? ")
            val signUpText = "Sign Up"
            pushStringAnnotation(tag = "SIGNUP", annotation = "signup")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFFF2E423),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(signUpText)
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            modifier = Modifier
                .fillMaxWidth(),
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = InterFont,
                textAlign = TextAlign.Center
            ),
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "SIGNUP", start = offset, end = offset)
                    .firstOrNull()?.let {
                        onNavigateToRegister()
                    }
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "By continuing, you agree to Coinary's Terms of\nService and Privacy Policy. You confirm that you have\nread and understood these terms.",
            color = Color.White,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            fontFamily = InterFont,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        )
    }
}


