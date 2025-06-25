plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("kotlin-kapt") // <-- AÑADE ESTA LÍNEA PARA EL PROCESADOR DE ANOTACIONES DE ROOM
}

android {
    namespace = "com.example.coinary"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.coinary"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Dependencias existentes
    implementation(libs.accompanist.systemuicontroller)
    implementation(platform(libs.firebase.bom.v33130))
    implementation(libs.google.firebase.analytics)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.play.services.auth.v2070)
    implementation(libs.androidx.navigation.compose.v277)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.vision.internal.vkp)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.material.icons.extended) // Asegúrate de que esta línea exista, aunque ya estaba en tu código

    // Dependencias de Room Database <-- AÑADE ESTE BLOQUE
    val room_version = "2.6.1" // Puedes actualizar a la última versión estable si lo deseas
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // Para soporte de Coroutines con Room

    // Dependencias de Coroutines (asegúrate de tenerlas, aunque otras libs ya las incluyan)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Dependencias de Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // O la versión más reciente
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Opcional, para ver logs de red

}