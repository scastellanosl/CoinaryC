package com.example.coinary.data.repository

import com.example.coinary.data.database.FreelanceIncomeDao
import com.example.coinary.data.model.FreelanceIncome
import kotlinx.coroutines.flow.Flow

class FreelanceIncomeRepositoryImpl(
    private val freelanceIncomeDao: FreelanceIncomeDao // <-- Inyecta el DAO de Room aquí
) : FreelanceIncomeRepository {

    override suspend fun addIncome(income: FreelanceIncome) {
        // CORRECTED: Call insertFreelanceIncome() as defined in FreelanceIncomeDao
        freelanceIncomeDao.insertFreelanceIncome(income)
        println("FreelanceIncomeRepository: Ingreso freelance guardado PERSISTENTEMENTE: $income")
    }

    override fun getAllIncomes(): Flow<List<FreelanceIncome>> {
        // CORRECTED: Call getAllFreelanceIncome() as defined in FreelanceIncomeDao
        return freelanceIncomeDao.getAllFreelanceIncome()
    }
}