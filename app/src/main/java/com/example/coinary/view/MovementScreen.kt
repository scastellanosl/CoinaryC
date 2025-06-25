package com.example.coinary.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coinary.R
import com.example.coinary.data.database.AppDatabase
import com.example.coinary.data.repository.ExpenseRepositoryImpl
import com.example.coinary.data.repository.FreelanceIncomeRepositoryImpl
import com.example.coinary.viewmodel.AddMovementViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovementScreen(
    navController: NavController,
    onBackClick: () -> Unit = {},
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

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val titleFontSize = if (screenWidth < 360.dp) 22.sp else 26.sp
    val labelFontSize = if (screenWidth < 360.dp) 14.sp else 16.sp
    val buttonHeight = if (screenHeight < 600.dp) 32.dp else 36.dp
    val buttonHorizontalPadding = if (screenWidth < 360.dp) 12.dp else 20.dp

    var selectedMovementType by remember { mutableStateOf("Income") } // Estado para el tipo de movimiento
    var bottomButtonSelected by remember { mutableStateOf<String?>(null) }

    // --- Inicio: Lógica de Categorías Dinámicas ---
    val freelanceCategories = listOf(
        "Development", "Design", "Writing", "Consulting",
        "Marketing", "Teaching", "Photography", "Other"
    )
    val expenseCategories = listOf(
        "Food", "Transport", "Entertainment", "Health", "Utilities", "Rent", "Shopping", "Other"
    )

    // Estado para la categoría seleccionada y la lista de categorías actual
    // Se inicializa con la primera categoría de ingresos
    var selectedCategory by remember { mutableStateOf(freelanceCategories.first()) }
    val currentCategories = remember(selectedMovementType) {
        if (selectedMovementType == "Income") freelanceCategories else expenseCategories
    }
    // --- Fin: Lógica de Categorías Dinámicas ---

    var expanded by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val commonFieldModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = buttonHorizontalPadding)
        .border(
            width = 1.dp,
            color = Color.White,
            shape = RoundedCornerShape(12.dp)
        )

    val context = LocalContext.current.applicationContext // <-- Obtener el contexto de la aplicación

    // --- Inicio: Inicialización del ViewModel con inyección de repositorios ---
    val addMovementViewModel: AddMovementViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(context) // Obtener la instancia de la base de datos
                val freelanceIncomeDao = database.freelanceIncomeDao()
                val freelanceIncomeRepository = FreelanceIncomeRepositoryImpl(freelanceIncomeDao)

                val expenseDao = database.expenseDao() // Obtener el DAO de gastos
                val expenseRepository = ExpenseRepositoryImpl(expenseDao) // Crear el repositorio de gastos

                // Pasar ambos repositorios al constructor del ViewModel
                @Suppress("UNCHECKED_CAST")
                return AddMovementViewModel(freelanceIncomeRepository, expenseRepository) as T
            }
        }
    )
    // --- Fin: Inicialización del ViewModel ---

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo_movimentos),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
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

                IconButton(onClick = { navController.navigate("notifications") }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Add a movement",
                // fontFamily = InterFont, // <-- Comentado/removido si no está definido
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Botones de arriba (Income / Expense)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                MovementTypeButton(
                    text = "Income",
                    isSelected = selectedMovementType == "Income",
                    onClick = {
                        selectedMovementType = "Income"
                        selectedCategory = freelanceCategories.first() // Reinicia la categoría al cambiar
                    },
                    modifier = Modifier
                        .width(130.dp)
                        .height(buttonHeight)
                )

                MovementTypeButton(
                    text = "Expense",
                    isSelected = selectedMovementType == "Expense",
                    onClick = {
                        selectedMovementType = "Expense"
                        selectedCategory = expenseCategories.first() // Reinicia la categoría al cambiar
                    },
                    modifier = Modifier
                        .width(130.dp)
                        .height(buttonHeight)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(15.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fondo_contenedor_categoria),
                    contentDescription = "Fondo contenedor categoria",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                ) {
                    Text(
                        text = "Select Category",
                        // fontFamily = InterFont, // <-- Comentado/removido si no está definido
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = (labelFontSize.value + 4).sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = commonFieldModifier
                    ) {
                        Text(
                            text = selectedCategory,
                            color = Color.White,
                            // fontFamily = InterFont, // <-- Comentado/removido si no está definido
                            fontWeight = FontWeight.Normal,
                            fontSize = labelFontSize,
                            modifier = Modifier
                                .menuAnchor()
                                .padding(12.dp)
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            currentCategories.forEach { category -> // <-- Usa la lista de categorías actual
                                androidx.compose.material3.DropdownMenuItem(
                                    text = {
                                        Text(
                                            // fontFamily = InterFont, // <-- Comentado/removido si no está definido
                                            fontWeight = FontWeight.Normal,
                                            fontSize = labelFontSize,
                                            text = category,
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = amount,
                        onValueChange = { newValue ->
                            // --- Inicio: Validación de monto ---
                            // Permite solo dígitos y un único punto decimal
                            if (newValue.all { it.isDigit() || it == '.' } && newValue.count { it == '.' } <= 1) {
                                amount = newValue
                            }
                            // --- Fin: Validación de monto ---
                        },
                        label = {
                            Text(
                                text = "Assign an amount",
                                // fontFamily = InterFont, // <-- Comentado/removido si no está definido
                                fontWeight = FontWeight.Normal,
                                fontSize = labelFontSize,
                                color = Color(0xFF868686)
                            )
                        },
                        singleLine = true,
                        modifier = commonFieldModifier,
                        textStyle = TextStyle(
                            // fontFamily = InterFont, // <-- Comentado/removido si no está definido
                            color = Color.White,
                            fontSize = labelFontSize
                        ),
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = "Dollar symbol",
                                tint = Color.White
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "Add a short description if you want to",
                        color = Color.White,
                        // fontFamily = InterFont, // <-- Comentado/removido si no está definido
                        fontSize = (labelFontSize.value - 2).sp,
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 30.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        label = {
                            Text(
                                text = "Add a description",
                                // fontFamily = InterFont, // <-- Comentado/removido si no está definido
                                fontWeight = FontWeight.Normal,
                                fontSize = labelFontSize,
                                color = Color(0xFF868686)
                            )
                        },
                        singleLine = true,
                        modifier = commonFieldModifier.padding(vertical = 30.dp),
                        textStyle = TextStyle(
                            // fontFamily = InterFont, // <-- Comentado/removido si no está definido
                            color = Color.White,
                            fontSize = labelFontSize
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    // Botones inferiores independientes (Save / Cancel)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MovementTypeButton(
                            text = "Save",
                            isSelected = bottomButtonSelected == "Save",
                            onClick = {
                                bottomButtonSelected = "Save"
                                val amountDouble = amount.toDoubleOrNull()
                                if (amountDouble != null && amountDouble > 0) {
                                    if (selectedMovementType == "Income") {
                                        addMovementViewModel.saveFreelanceIncome(
                                            amount = amountDouble,
                                            category = selectedCategory,
                                            description = description
                                        )
                                        // Navega a la lista de ingresos después de guardar un ingreso
                                        navController.navigate(Routes.FreelanceIncomeListScreen.route) {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    } else { // Si selectedMovementType es "Expense"
                                        addMovementViewModel.saveExpense(
                                            amount = amountDouble,
                                            category = selectedCategory,
                                            description = description
                                        )
                                        // Navega a la nueva pantalla de lista de gastos después de guardar un gasto
                                        navController.navigate(Routes.ExpenseListScreen.route) {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    }
                                    // Limpiar campos después de guardar exitosamente
                                    amount = ""
                                    description = ""
                                    selectedCategory = currentCategories.first() // Reiniciar la categoría a la primera de la lista actual
                                } else {
                                    println("Error: El monto no es válido o es cero.")
                                    // Considera mostrar un Toast o un Snackbar al usuario aquí para feedback
                                }
                            },
                            modifier = Modifier.height(36.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        MovementTypeButton(
                            text = "Cancel",
                            isSelected = bottomButtonSelected == "Cancel",
                            onClick = {
                                bottomButtonSelected = "Cancel"
                                amount = ""
                                description = ""
                                selectedMovementType = "Income" // Resetear a Income por defecto
                                selectedCategory = freelanceCategories.first() // Reiniciar categoría a la primera de ingresos
                                navController.popBackStack() // Regresa a la pantalla anterior
                            },
                            modifier = Modifier.height(36.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MovementTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF4c6ef5) else Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.linearGradient(listOf(Color.Gray, Color.Gray))
        ) else null,
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}