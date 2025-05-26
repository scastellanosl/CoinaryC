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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coinary.ui.theme.CoinaryTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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

@Composable
fun AppNavigation() { 

    val systemUiController = rememberSystemUiController() // Forma de pintar la barra de estado del celu
    val statusBarColor = Color(0xFF150F33)
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false // Al dejar dark icons en false, las notificaciones y otros iconitos serán claros y harán contraste con el color de la barra de navegación d esta pantalla en específico
        )
    }

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
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.marco_superior),
                contentDescription = "Marco superior",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.padding(top = 5.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.user_icon),
                    contentDescription = "Foto de usuario",
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {

                        }

                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
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
                        .size(28.dp)
                        .clickable {
                            // Screen for user configuration (Close session, )
                        }
                        .padding(top = 2.dp)
                )


            }


        }

        Box(
            modifier = Modifier
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.backgroundcoinary),
                contentDescription = "Background Coinary",
                modifier = Modifier
                    .padding(top = 15.dp),
                contentScale = ContentScale.None
            )

            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Personal Expenses",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 40.dp)
                )

                Text(
                    text = "$ 245.567,55",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 15.dp)
                )

                Text(
                    text = "Total income",
                    fontWeight = FontWeight.Thin,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 3.dp)
                )

                Text(
                    text = "$ 1’245.567,34",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White,
                    modifier = Modifier.offset(y = (-5).dp)
                )

                Button(
                    onClick = { /* Change month action */ },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4D54BF),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.offset(y = (-3).dp),
                    contentPadding = PaddingValues(horizontal = 25.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Month",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }

        Box(
            modifier = Modifier
                .offset(y = (-18).dp)
                .padding(horizontal = 55.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.fondo_contenedor_this_week),
                contentDescription = "Fondo contenedor this week"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 55.dp)
            ) {

                Text(
                    text = "Your week",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 20.sp, modifier = Modifier.padding(top = 30.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Image(
                    alignment = Alignment.Center,
                    painter = painterResource(id = R.drawable.cake_graphic),
                    contentDescription = "Cake graphic",
                    modifier = Modifier
                        .size(175.dp)
                )

                Text(
                    text = "All",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Thin,
                    color = Color.White,
                    modifier = Modifier.offset(y = (-120).dp)
                )
                Text(
                    text = "$ 245.567,55",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.offset(y = (-120).dp)
                )

            }


        }

        Box(
            modifier = Modifier
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.marco_inferior),
                contentDescription = "Marco inferior",
                modifier = Modifier.offset(y = (-10).dp),
                contentScale = ContentScale.None
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {

                Text(
                    text = "Top Expenses",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.Thin,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 0.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 35.dp), // espacio alrededor de toda la fila
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 3.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .size(95.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.rectangle1),
                                contentDescription = "Rectangle 1",
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                text = "Food",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.food_icon),
                                contentDescription = "Food icon",
                                modifier = Modifier
                                    .padding(top = 26.dp)
                                    .size(42.dp)
                            )
                            Text(
                                text = "$ 102.500",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 70.dp)
                            )
                        }
                    }


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 3.dp)
                    ) {

                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .size(95.dp)
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.rectangle2),
                                contentDescription = "Rectangle 2",
                                modifier = Modifier.fillMaxSize()
                            )

                            Text(
                                text = "Gifts",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Image(
                                painter = painterResource(id = R.drawable.gift_icon),
                                contentDescription = "Gift icon",
                                modifier = Modifier
                                    .padding(top = 33.dp)
                                    .size(32.dp)
                            )

                            Text(
                                text = "$ 78.000",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 70.dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 3.dp)
                    ) {

                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .size(95.dp)
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.rectangle3),
                                contentDescription = "Rectangle 3",
                                modifier = Modifier.fillMaxSize()
                            )

                            Text(
                                text = "Transport",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Image(
                                painter = painterResource(id = R.drawable.car_icon),
                                contentDescription = "Car icon",
                                modifier = Modifier
                                    .padding(top = 28.dp)
                                    .size(38.dp)
                            )

                            Text(
                                text = "$ 65.123",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 70.dp)
                            )
                        }
                    }

                }

            }

        }

        Box(
            modifier = Modifier
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp), // ajusta aquí
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentWidth()

            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.button_background),
                            contentDescription = "Button background",
                            modifier = Modifier.clickable {
                                // Lógica
                            }
                        )

                        Image(
                            painter = painterResource(id = R.drawable.stats_icon),
                            contentDescription = "Stats icon"
                        )
                    }
                }

                Column() {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.button_background),
                            contentDescription = "Button background",
                            modifier = Modifier.clickable {
                                // Lógica
                            }
                        )

                        Image(
                            painter = painterResource(id = R.drawable.add_icon),
                            contentDescription = "Add icon"
                        )
                    }

                }

                Column() {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.button_background),
                            contentDescription = "Button background",
                            modifier = Modifier.clickable {
                                // Lógica
                            }
                        )

                        Image(
                            painter = painterResource(id = R.drawable.pencil_icon),
                            contentDescription = "Pencil icon"
                        )
                    }

                }

            }

        }


    }
}
