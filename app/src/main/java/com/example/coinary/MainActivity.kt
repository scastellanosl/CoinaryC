package com.example.coinary

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coinary.ui.theme.CoinaryTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoinaryTheme {
                AppNavigation()
            }
        }
    }
}

@Preview
@Composable
fun AppNavigation() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val navController = rememberNavController()
    val googleAuthClient = remember { GoogleAuthClient(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        coroutineScope.launch {
            val intent = result.data
            if (intent != null) {
                val signInResult = googleAuthClient.signInWithIntent(intent)
                if (signInResult.isSuccess) {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Error: ${signInResult.exceptionOrNull()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            GoogleLoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                },
                googleAuthClient = googleAuthClient,
                launcher = launcher
            )
        }

        composable("home") {
            HomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
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
            .background(Color.Black)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(top = -50.dp)
                ,contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.marco_superior),
                contentDescription = "Marco superior",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )

            Row (
                modifier = Modifier.padding(top = 15.dp)
            ){

                Image(
                    painter = painterResource(id = R.drawable.user_icon),
                    contentDescription = "Foto de usuario",
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {

                        }

                )

                Spacer(modifier = Modifier.width(8.dp))

                Column{
                    Text(
                        "Hello!!",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFF2E423)
                    )
                    Text(
                        user?.username ?: "User",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF2E423)
                    )
                }

                Spacer(modifier = Modifier.width(270.dp))

                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notification icon",
                    tint = Color(0xFFF2E423),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            // Screen for user configuration (Close session, )
                        }
                )


            }


        }




        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomEnd = 45.dp)) // solo esquina derecha abajo redondeada
                .background(Color(0xFFFFEB3B))
                .padding(18.dp)
                .padding(bottom = 60.dp)
        )

        {
            // Greeting to the user and button for user configuration
            Row(
                modifier = Modifier.align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Icon",
                    tint = Color(0xFF3F51B5),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            // Screen for user configuration (Close session, )
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "Hello!!",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF150F33)
                    )
                    Text(
                        user?.username ?: "User",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF150F33)
                    )
                }
            }

            // Notification icon
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notification",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp),
                tint = Color.Black
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    "Total Personal Expenses",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF150F33)
                )
                Text(
                    "$ 245.567,55",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF150F33)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Total Income",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF150F33)
                )
                Text(
                    "$ 1’245.567,34",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF150F33)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Change month action */ },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF150F33)
                    ),
                    border = BorderStroke(
                        2.dp,
                        Color(0xFF150F33)
                    )
                ) {
                    Text("Mes")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    googleAuthClient.signOut()
                    onLogout()
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Cerrar Sesión")
        }
    }
}
