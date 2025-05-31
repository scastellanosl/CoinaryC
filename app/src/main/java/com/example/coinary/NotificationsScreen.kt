package com.example.coinary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun NotificationsScreen(
    navController: NavController, onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {

    val systemUiController = rememberSystemUiController()
    val statusBarColor = Color.Black
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

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
            Text (text = "Notifications", color = Color.White)

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(R.drawable.fondo_contenedor_categoria),
                contentDescription = "Notification background",
                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds
            )

            Column(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 12.dp).padding(top = 15.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 3f),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.notification_background),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reminder",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Remember to pay your internet service soon",
                                    color = Color.White,
                                    fontWeight = FontWeight.Thin,
                                    fontSize = 12.sp
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.message_icon),
                                contentDescription = "Message icon",
                                modifier = Modifier
                                    .padding(end = 8.dp).padding(bottom = 3.dp)
                                    .size(36.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF150F33))
                            )
                        }

                    }

                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 12.dp).padding(top = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 3f),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.notification_background),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reminder",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Remember to add today's expenses",
                                    color = Color.White,
                                    fontWeight = FontWeight.Thin,
                                    fontSize = 12.sp
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.message_icon),
                                contentDescription = "Message icon",
                                modifier = Modifier
                                    .padding(end = 8.dp).padding(bottom = 3.dp)
                                    .size(36.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF150F33))
                            )
                        }

                    }

                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 12.dp).padding(top = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 3f),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.notification_background),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reminder",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "The rent is almost due",
                                    color = Color.White,
                                    fontWeight = FontWeight.Thin,
                                    fontSize = 12.sp
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.message_icon),
                                contentDescription = "Message icon",
                                modifier = Modifier
                                    .padding(end = 8.dp).padding(bottom = 3.dp)
                                    .size(36.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF150F33))
                            )
                        }

                    }

                }

            }

        }

    }
    
}
