package com.example.coinary.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers // Importar Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // Importar withContext
import org.json.JSONObject

// Importaciones de OkHttp y kotlinx para la llamada de red real
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit // Para configurar timeouts


// --- Mueve esta clase aquí (fuera de cualquier función) ---
data class ChatMessage(val text: String, val isUser: Boolean)
// -----------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val chatHistory = remember { mutableStateListOf<ChatMessage>() }
    var userMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistente Coinary", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
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
                    .background(MaterialTheme.colorScheme.background) // Fondo del chat
            ) {
                // Área de chat
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    reverseLayout = true // Para que los mensajes nuevos aparezcan abajo
                ) {
                    items(chatHistory.reversed()) { message ->
                        MessageBubble(message = message)
                    }
                }

                // Indicador de carga
                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }

                // Área de entrada de texto
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userMessage,
                        onValueChange = { userMessage = it },
                        label = { Text("Escribe tu mensaje...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors( // Correcto para M3
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (userMessage.isNotBlank() && !isLoading) {
                                val currentMessage = userMessage.trim()
                                chatHistory.add(ChatMessage(currentMessage, true)) // Añadir mensaje del usuario
                                userMessage = "" // Limpiar input
                                isLoading = true // Activar indicador de carga

                                coroutineScope.launch {
                                    try {
                                        // TODO: Considerar pasar un contexto para usar una API key segura en producción
                                        val botResponse = callGeminiApi(currentMessage, chatHistory) // Llama a la función de la API
                                        chatHistory.add(ChatMessage(botResponse, false)) // Añadir respuesta del bot
                                    } catch (e: Exception) {
                                        Log.e("ChatScreen", "Error calling Gemini API: ${e.message}", e)
                                        Toast.makeText(context, "Error: No se pudo obtener respuesta del asistente.", Toast.LENGTH_SHORT).show()
                                        chatHistory.add(ChatMessage("Lo siento, hubo un error al procesar tu solicitud. Intenta de nuevo más tarde.", false))
                                    } finally {
                                        isLoading = false // Desactivar indicador de carga
                                    }
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
                    }
                }
            }
        }
    )
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.widthIn(max = 300.dp) // Limitar ancho del globo de mensaje
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(10.dp),
                color = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Función para llamar a la API de Gemini
private suspend fun callGeminiApi(prompt: String, chatHistoryList: List<ChatMessage>): String {
    val historyForApi = mutableListOf<Map<String, Any>>()

    // Traducir el historial de chat al formato esperado por la API de Gemini
    // Asegurarse de incluir el historial completo para una conversación contextual
    chatHistoryList.forEach { msg ->
        historyForApi.add(
            mapOf(
                "role" to if (msg.isUser) "user" else "model",
                "parts" to listOf(mapOf("text" to msg.text))
            )
        )
    }

    // El payload debe contener el historial completo, incluyendo el prompt actual del usuario
    val payload = mapOf(
        "contents" to historyForApi
    )

    // !!! IMPORTANTE: REEMPLAZA "TU_CLAVE_API_DE_GEMINI_AQUI" CON TU CLAVE REAL DE GOOGLE GEMINI !!!
    // NO expongas esta clave en código público o repositorios. Usa métodos más seguros en producción.
    val apiKey = "AIzaSyDM0oSp47eFkrJnMBuiwgz1ol_4VqhclTY" // <--- ¡COLOCA TU CLAVE AQUÍ!
    val apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${apiKey}"

    return try {
        val jsonPayload = JSONObject(payload).toString()
        val response = fetch(apiUrl, mapOf(
            "method" to "POST",
            "headers" to mapOf("Content-Type" to "application/json"),
            "body" to jsonPayload
        ))

        val resultJson = JSONObject(response)

        if (resultJson.has("candidates") && resultJson.getJSONArray("candidates").length() > 0) {
            val candidates = resultJson.getJSONArray("candidates")
            val firstCandidate = candidates.getJSONObject(0)
            if (firstCandidate.has("content") && firstCandidate.getJSONObject("content").has("parts")) {
                val parts = firstCandidate.getJSONObject("content").getJSONArray("parts")
                if (parts.length() > 0 && parts.getJSONObject(0).has("text")) {
                    parts.getJSONObject(0).getString("text")
                } else {
                    "No se pudo obtener el texto de la respuesta del modelo."
                }
            } else {
                "La respuesta del modelo no contiene la estructura de contenido esperada."
            }
        } else {
            "El modelo no generó ninguna respuesta válida."
        }
    } catch (e: Exception) {
        Log.e("GeminiAPI", "Error en callGeminiApi: ${e.message}", e)
        "Error al comunicarse con el asistente. Por favor, revisa tu conexión a internet o la clave API."
    }
}

// --- FUNCIÓN 'fetch' ACTUALIZADA PARA USAR OKHTTP REAL CON WITHCONTEXT(DISPATCHERS.IO) ---
private suspend fun fetch(url: String, options: Map<String, Any>): String {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para establecer conexión
        .readTimeout(30, TimeUnit.SECONDS)    // Tiempo de espera para leer respuesta
        .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo de espera para enviar cuerpo de solicitud
        // .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }) // Descomentar para logs detallados de red
        .build()

    val method = options["method"] as? String ?: "GET"
    val headersMap = options["headers"] as? Map<String, String> ?: emptyMap()
    val bodyString = options["body"] as? String

    val requestBuilder = Request.Builder().url(url)

    headersMap.forEach { (name, value) ->
        requestBuilder.addHeader(name, value)
    }

    when (method.uppercase()) {
        "GET" -> requestBuilder.get()
        "POST" -> {
            val mediaType = headersMap["Content-Type"]?.toMediaTypeOrNull()
            // Asegurarse de que el body no sea nulo para toRequestBody
            val requestBody = bodyString?.toRequestBody(mediaType)
            requestBuilder.post(requestBody ?: RequestBody.create(null, ByteArray(0))) // Si bodyString es nulo, envía un cuerpo vacío
        }
        else -> throw IllegalArgumentException("Método HTTP no soportado: $method")
    }

    return try {
        val request = requestBuilder.build()
        // !!! AQUÍ ESTÁ EL CAMBIO CLAVE: Ejecutar la llamada de red en un hilo de IO !!!
        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute() // Esto ejecuta la llamada de red bloqueante en el hilo de IO
        }

        if (response.isSuccessful) {
            response.body?.string() ?: "Respuesta vacía del cuerpo de la API."
        } else {
            val errorBody = response.body?.string()
            Log.e("GeminiAPI", "Error HTTP ${response.code}: $errorBody")
            "Error de la API: ${response.code} - ${response.message}. Detalle: $errorBody"
        }
    } catch (e: Exception) {
        Log.e("GeminiAPI", "Excepción al hacer la llamada de red: ${e.message}", e)
        "Error de red: ${e.localizedMessage ?: e.message ?: "Conexión fallida"}"
    }
}