package com.example.coinary.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.coinary.data.model.Expense
import com.example.coinary.data.model.FreelanceIncome

@Database(
    entities = [FreelanceIncome::class, Expense::class],
    version = 3, // Asegúrate de que aquí sea 2, como lo cambiaste antes
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun freelanceIncomeDao(): FreelanceIncomeDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coinary_database"
                )
                    .fallbackToDestructiveMigration() // <-- ¡AÑADE ESTA LÍNEA!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}