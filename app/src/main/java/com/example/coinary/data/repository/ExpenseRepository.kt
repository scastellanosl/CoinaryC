package com.example.coinary.data.repository

import com.example.coinary.data.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense)
    fun getAllExpenses(): Flow<List<Expense>>
}