package com.example.coinary.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.coinary.data.model.FreelanceIncome

// Define la base de datos de Room
@Database(
    entities = [FreelanceIncome::class], // Lista de entidades (tablas) en esta base de datos
    version = 1, // Versión de la base de datos. Incrementa si cambias el esquema.
    exportSchema = false // Por ahora, no exportamos el esquema JSON
)
abstract class AppDatabase : RoomDatabase() {

    // Define los DAOs asociados a esta base de datos
    abstract fun freelanceIncomeDao(): FreelanceIncomeDao

    companion object {
        @Volatile // Los cambios a esta instancia son inmediatamente visibles para todos los hilos
        private var INSTANCE: AppDatabase? = null

        // Método para obtener la instancia única de la base de datos (Singleton)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { // Bloquea este hilo para asegurar una única instancia
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Usa el ApplicationContext para evitar fugas de memoria
                    AppDatabase::class.java,
                    "coinary_database" // Nombre del archivo de la base de datos
                )
                    // .fallbackToDestructiveMigration() // Opcional: Destruye y recrea la base de datos si la versión cambia (solo para desarrollo)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}