package com.example.coinary.data.database // Asegúrate que este sea el paquete correcto para tu DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.coinary.data.model.Expense // Importa tu entidad Expense

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC") // <-- ¡CORREGIDO AQUÍ!
    fun getAllExpenses(): Flow<List<Expense>>
}