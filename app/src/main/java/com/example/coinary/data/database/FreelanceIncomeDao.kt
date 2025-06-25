package com.example.coinary.data.database // Make sure this package is correct for your DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.coinary.data.model.FreelanceIncome
import kotlinx.coroutines.flow.Flow

@Dao
interface FreelanceIncomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFreelanceIncome(income: FreelanceIncome)

    // The query should use "freelance_income_table" as specified in your entity
    @Query("SELECT * FROM freelance_income_table ORDER BY date DESC") // <-- Use the exact table name
    fun getAllFreelanceIncome(): Flow<List<FreelanceIncome>>
}