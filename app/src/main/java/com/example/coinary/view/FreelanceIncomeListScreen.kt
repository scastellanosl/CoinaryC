package com.example.coinary.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coinary.data.database.AppDatabase
import com.example.coinary.data.model.FreelanceIncome
import com.example.coinary.data.repository.FreelanceIncomeRepositoryImpl
import com.example.coinary.viewmodel.FreelanceIncomeListViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FreelanceIncomeListScreen(navController: NavController) {

    // Configuración del System UI Controller para la barra de estado
    val systemUiController = rememberSystemUiController()
    val statusBarColor = Color.Black
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    // Obtenemos el contexto para inicializar la base de datos y el repositorio
    val context = LocalContext.current.applicationContext

    // Inicializamos el ViewModel con la factory, proporcionando el repositorio real
    val viewModel: FreelanceIncomeListViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                // Obtiene una instancia del DAO desde la base de datos
                val freelanceIncomeDao = AppDatabase.getDatabase(context).freelanceIncomeDao()
                // Crea una instancia del repositorio real, pasándole el DAO
                val repository = FreelanceIncomeRepositoryImpl(freelanceIncomeDao)
                // Crea el ViewModel con el repositorio
                return FreelanceIncomeListViewModel(repository) as T
            }
        }
    )

    // Observa el StateFlow de ingresos desde el ViewModel
    val incomes by viewModel.freelanceIncomes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Fondo negro para toda la pantalla
            .padding(horizontal = 16.dp, vertical = 1.dp), // Ajusta el padding superior
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Barra superior con botón de retroceso y título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), // Ajusta el padding superior para no pegarse al borde
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Ingresos Freelance",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            // Un spacer para centrar el título si no hay icono a la derecha
            Spacer(modifier = Modifier.width(48.dp)) // Ancho de IconButton
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Contenido principal de la pantalla
        if (incomes.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Aún no hay ingresos freelance registrados.",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Text(
                    text = "¡Usa la pantalla 'Add Movement' para agregar tu primer ingreso!",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 32.dp) // Primero el padding horizontal
                        .padding(top = 8.dp) // Luego el padding superior
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(incomes) { income ->
                    FreelanceIncomeCard(income = income)
                }
            }
        }
    }
}

@Composable
fun FreelanceIncomeCard(income: FreelanceIncome) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF282C34)), // Un gris oscuro/azulado
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Monto: $${String.format(Locale.US, "%.2f", income.amount)}", // Formato a 2 decimales
                color = Color.Green,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Categoría: ${income.category}",
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (income.description.isNotBlank()) {
                Text(
                    text = "Descripción: ${income.description}",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = "Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(income.date))}",
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}