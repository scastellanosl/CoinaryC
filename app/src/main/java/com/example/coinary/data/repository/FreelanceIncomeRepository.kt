package com.example.coinary.data.repository
import com.example.coinary.data.model.FreelanceIncome
import kotlinx.coroutines.flow.Flow



interface FreelanceIncomeRepository {
    suspend fun addIncome(income: FreelanceIncome)
    fun getAllIncomes(): Flow<List<FreelanceIncome>>
}