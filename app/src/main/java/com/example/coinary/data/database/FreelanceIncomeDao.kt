package com.example.coinary.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.coinary.data.model.FreelanceIncome
import kotlinx.coroutines.flow.Flow

@Dao // Declara esta interfaz como un DAO de Room
interface FreelanceIncomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Método para insertar o reemplazar un ingreso
    suspend fun insertIncome(income: FreelanceIncome)

    @Query("SELECT * FROM freelance_incomes ORDER BY date DESC") // Consulta para obtener todos los ingresos, ordenados por fecha descendente
    fun getAllIncomes(): Flow<List<FreelanceIncome>> // Devuelve un Flow para recibir actualizaciones en tiempo real
}