package com.example.coinary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ProfileScreen(
    navController: NavController, onLogout: () -> Unit
) {

    val systemUiController = rememberSystemUiController()
    val statusBarColor = Color.Black
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    val screenHeight =
        LocalConfiguration.current.screenHeightDp.dp //Para aplicar responsividad, conociendo las dimensiones del dispositivo
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.toFloat()
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.toFloat()
    val context = LocalContext.current
    val googleAuthClient = remember { GoogleAuthClient(context) }
    val coroutineScope = rememberCoroutineScope()
    val user = googleAuthClient.getSignedInUser()

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

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
                    text = "User",
                    //user?.username ?: "User",
                    fontWeight = FontWeight.Bold,
                    fontSize = (screenWidthDp * 0.042f).sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = screenHeight * 0.032f)
                        .fillMaxWidth(1f),
                    textAlign = TextAlign.Center
                )

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
                                .padding(top = screenHeight * 0.042f)
                                .fillMaxWidth(1f)
                        )

                        Text(
                            text = "Email",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = (screenWidthDp * 0.045f).sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = screenHeight * 0.014f)
                                .fillMaxWidth(1f)
                        )

                    }


                }


            }

        }

    }
}
