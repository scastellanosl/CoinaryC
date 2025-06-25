package com.example.coinary.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.coinary.data.database.AppDatabase
import com.example.coinary.data.model.Expense
import com.example.coinary.data.model.FreelanceIncome
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val expenses = remember { mutableStateListOf<Expense>() }
    val incomes = remember { mutableStateListOf<FreelanceIncome>() }

    val selectedYear = remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    val selectedMonth = remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) } // 0-11

    val monthNames = remember {
        listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
    }

    val permissionsToRequest = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) o superior
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else { // Android 12 (API 32) o inferior
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    val requestMultiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val allGranted = grants.entries.all { it.value }

        if (allGranted) {
            generatePdfReport(
                context,
                selectedYear.value,
                selectedMonth.value,
                expenses,
                incomes
            )
        } else {
            Toast.makeText(context, "Permiso de almacenamiento denegado. No se puede generar el reporte.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte Mensual")},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    YearPicker(selectedYear.value) { year ->
                        selectedYear.value = year
                        coroutineScope.launch {
                            loadDataForMonth(context, selectedYear.value, selectedMonth.value, expenses, incomes)
                        }
                    }

                    MonthPicker(selectedMonth.value) { month ->
                        selectedMonth.value = month
                        coroutineScope.launch {
                            loadDataForMonth(context, selectedYear.value, selectedMonth.value, expenses, incomes)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val allPermissionsGranted = permissionsToRequest.all {
                            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                        }

                        if (allPermissionsGranted) {
                            generatePdfReport(
                                context,
                                selectedYear.value,
                                selectedMonth.value,
                                expenses,
                                incomes
                            )
                        } else {
                            requestMultiplePermissionsLauncher.launch(permissionsToRequest)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Generar Reporte PDF")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Aquí es donde se muestra la cantidad de registros
                Text("Datos para ${monthNames[selectedMonth.value]} ${selectedYear.value}:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Gastos: ${expenses.size} registros", fontSize = 16.sp)
                Text("Ingresos: ${incomes.size} registros", fontSize = 16.sp) // <-- Aquí aparece 0
            }
        }
    )

    LaunchedEffect(selectedYear.value, selectedMonth.value) {
        // Asegúrate de que esta llamada inicial también use la nueva lógica de carga
        loadDataForMonth(context, selectedYear.value, selectedMonth.value, expenses, incomes)
    }
}

@Composable
fun YearPicker(selectedYear: Int, onYearSelected: (Int) -> Unit) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 5..currentYear + 5).toList()

    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Año: $selectedYear")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = { Text(year.toString()) },
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MonthPicker(selectedMonth: Int, onMonthSelected: (Int) -> Unit) {
    val monthNames = remember {
        listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
    }
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Mes: ${monthNames[selectedMonth]}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            monthNames.forEachIndexed { index, name ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onMonthSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}
private suspend fun loadDataForMonth(
    context: Context,
    year: Int,
    month: Int, // 0-11
    expenses: SnapshotStateList<Expense>,
    incomes: SnapshotStateList<FreelanceIncome>
) {
    val db = AppDatabase.getDatabase(context)

    val startCalendar = Calendar.getInstance().apply {
        set(year, month, 1, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val startDate = startCalendar.timeInMillis

    val endCalendar = Calendar.getInstance().apply {
        set(year, month, 1, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.MONTH, 1)
    }
    val endDateExclusive = endCalendar.timeInMillis

    val dateFormatWithMillis = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.getDefault())

    android.util.Log.d("COINARY_DATE_DEBUG", "--- Load Data for ${month + 1}/${year} ---")
    android.util.Log.d("COINARY_DATE_DEBUG", "Range (Inclusive Start): ${dateFormatWithMillis.format(Date(startDate))} (${startDate} ms)")
    android.util.Log.d("COINARY_DATE_DEBUG", "Range (Exclusive End): ${dateFormatWithMillis.format(Date(endDateExclusive))} (${endDateExclusive} ms)")

    // Limpia las listas al inicio de la carga
    expenses.clear()
    incomes.clear()

    // Usar 'coroutineScope' de kotlinx.coroutines para lanzar sub-rutinas concurrently
    kotlinx.coroutines.coroutineScope { // Crea un nuevo scope estructurado
        // Colección para Gastos
        launch {
            db.expenseDao().getAllExpenses().collect { allExpenses ->
                val filteredExpenses = allExpenses.filter {
                    it.date >= startDate && it.date < endDateExclusive
                }
                expenses.clear() // Limpiar antes de añadir, para actualizar la lista observable
                expenses.addAll(filteredExpenses)
                android.util.Log.d("COINARY_DATA_SUMMARY", "Expenses: Found ${allExpenses.size} raw. Loaded ${expenses.size} filtered.")
                filteredExpenses.forEach { expense ->
                    android.util.Log.d("COINARY_EXPENSE_DETAIL", "Expense: ${expense.description}, Date: ${dateFormatWithMillis.format(Date(expense.date))} (${expense.date} ms)")
                }
            }
        }

        // Colección para Ingresos
        launch {
            android.util.Log.d("COINARY_DEBUG", "Attempting to collect FreelanceIncomes...")
            db.freelanceIncomeDao().getAllFreelanceIncome().collect { allIncomes ->
                android.util.Log.d("COINARY_DATA_SUMMARY", "Incomes: Found ${allIncomes.size} raw from DB. (Inside collect block)")
                allIncomes.forEach { income ->
                    android.util.Log.d("COINARY_INCOME_RAW", "Raw Income: ${income.description}, Date: ${dateFormatWithMillis.format(Date(income.date))} (${income.date} ms)")
                }

                val filteredIncomes = allIncomes.filter {
                    it.date >= startDate && it.date < endDateExclusive
                }
                incomes.clear() // Limpiar antes de añadir, para actualizar la lista observable
                incomes.addAll(filteredIncomes)
                android.util.Log.d("COINARY_DATA_SUMMARY", "Incomes: Loaded ${incomes.size} filtered after applying date range. (Inside collect block)")
                filteredIncomes.forEach { income ->
                    android.util.Log.d("COINARY_INCOME_FILTERED", "Filtered Income: ${income.description}, Date: ${dateFormatWithMillis.format(Date(income.date))} (${income.date} ms)")
                }
            }
        }
    }
    android.util.Log.d("COINARY_DEBUG", "Finished launching all data collection flows.")
}
private fun generatePdfReport(
    context: Context,
    year: Int,
    month: Int, // 0-11
    expenses: List<Expense>,
    incomes: List<FreelanceIncome>
) {
    android.util.Log.d("PDF_GEN_COINARY", "Starting PDF generation. Incomes size: ${incomes.size}, Expenses size: ${expenses.size}")

    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
    val page = document.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(
        Calendar.getInstance().apply { set(year, month, 1) }.time
    )

    var y = 50f
    val x = 50f

    // Título
    paint.color = Color.BLACK
    paint.textSize = 24f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    canvas.drawText("Reporte Mensual Coinary", x, y, paint)
    y += 30f
    paint.textSize = 18f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
    canvas.drawText("Mes: $monthName", x, y, paint)
    y += 50f

    // Resumen de Ingresos
    paint.textSize = 16f
    paint.color = Color.rgb(40, 167, 69) // Verde para ingresos
    canvas.drawText("INGRESOS:", x, y, paint)
    y += 25f

    if (incomes.isEmpty()) {
        paint.color = Color.BLACK
        paint.textSize = 14f
        canvas.drawText("No hay ingresos registrados para este mes.", x + 20, y, paint)
        y += 20f
    } else {
        var totalIncome = 0.0
        paint.color = Color.BLACK
        paint.textSize = 14f
        for (income in incomes) {
            val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(income.date))
            canvas.drawText("$dateStr - ${income.description}: $${String.format("%.2f", income.amount)}", x + 20, y, paint)
            y += 20f
            totalIncome += income.amount
        }
        y += 10f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("Total Ingresos: $${String.format("%.2f", totalIncome)}", x + 20, y, paint)
        y += 30f
    }

    // Resumen de Gastos
    paint.color = Color.rgb(220, 53, 69) // Rojo para gastos
    paint.textSize = 16f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    canvas.drawText("GASTOS:", x, y, paint)
    y += 25f

    if (expenses.isEmpty()) {
        paint.color = Color.BLACK
        paint.textSize = 14f
        canvas.drawText("No hay gastos registrados para este mes.", x + 20, y, paint)
        y += 20f
    } else {
        var totalExpense = 0.0
        paint.color = Color.BLACK
        paint.textSize = 14f
        for (expense in expenses) {
            val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(expense.date))
            canvas.drawText("$dateStr - ${expense.description}: $${String.format("%.2f", expense.amount)}", x + 20, y, paint)
            y += 20f
            totalExpense += expense.amount
        }
        y += 10f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("Total Gastos: $${String.format("%.2f", totalExpense)}", x + 20, y, paint)
        y += 30f
    }

    document.finishPage(page)

    val fileName = "Coinary_Reporte_${monthName.replace(" ", "_")}_${year}.pdf"
    val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    if (!directory.exists()) {
        val created = directory.mkdirs()
        if (!created) {
            Toast.makeText(context, "Error: No se pudo crear el directorio de descargas", Toast.LENGTH_LONG).show()
            android.util.Log.e("PDF_ERROR_COINARY", "No se pudo crear el directorio: ${directory.absolutePath}")
            document.close()
            return
        }
    }
    if (!directory.isDirectory) {
        Toast.makeText(context, "Error: La ruta de descargas no es un directorio válido", Toast.LENGTH_LONG).show()
        android.util.Log.e("PDF_ERROR_COINARY", "La ruta de descargas no es un directorio: ${directory.absolutePath}")
        document.close()
        return
    }
    if (!directory.canWrite()) {
        Toast.makeText(context, "Error: El directorio de descargas no es escribible (puede ser un problema de permisos o de ruta)", Toast.LENGTH_LONG).show()
        android.util.Log.e("PDF_ERROR_COINARY", "Directorio no escribible: ${directory.absolutePath} (Esto puede ser engañoso en Android 10+).")
    }

    val file = File(directory, fileName)

    android.util.Log.d("PDF_DEBUG_COINARY", "Intentando guardar PDF en: ${file.absolutePath}")

    try {
        document.writeTo(FileOutputStream(file))
        Toast.makeText(context, "Reporte PDF guardado en ${file.absolutePath}", Toast.LENGTH_LONG).show()
        android.util.Log.i("PDF_SUCCESS_COINARY", "PDF guardado exitosamente en: ${file.absolutePath}")
    } catch (e: Exception) {
        android.util.Log.e("PDF_ERROR_COINARY", "Error al generar PDF: ${e.message}", e)
        Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        document.close()
    }
}