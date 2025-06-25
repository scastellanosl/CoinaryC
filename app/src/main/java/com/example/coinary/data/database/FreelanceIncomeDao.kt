package com.example.coinary.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.coinary.data.model.FreelanceIncome
import kotlinx.coroutines.flow.Flow

@Dao
interface FreelanceIncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFreelanceIncome(income: FreelanceIncome) // Asegúrate de que el nombre del método sea este

    @Query("SELECT * FROM freelance_income_table ORDER BY date DESC") // Y este nombre para la consulta
    fun getAllFreelanceIncome(): Flow<List<FreelanceIncome>>
}