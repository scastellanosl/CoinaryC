package com.example.coinary.data.repository

import com.example.coinary.data.database.ExpenseDao
import com.example.coinary.data.model.Expense
import kotlinx.coroutines.flow.Flow

class ExpenseRepositoryImpl(private val expenseDao: ExpenseDao) : ExpenseRepository {
    override suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses()
    }
}