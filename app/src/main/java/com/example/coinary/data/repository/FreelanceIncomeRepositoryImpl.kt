package com.example.coinary.data.repository


import com.example.coinary.data.database.FreelanceIncomeDao
import com.example.coinary.data.model.FreelanceIncome
import kotlinx.coroutines.flow.Flow

// Ya no necesitamos MutableStateFlow.asStateFlow() ni @Singleton aquí si no usas Hilt para este repo

class FreelanceIncomeRepositoryImpl(
    private val freelanceIncomeDao: FreelanceIncomeDao // <-- Inyecta el DAO de Room aquí
) : FreelanceIncomeRepository {

    // Antes teníamos un MutableStateFlow en memoria. Ahora el DAO de Room maneja la persistencia.

    override suspend fun addIncome(income: FreelanceIncome) {
        freelanceIncomeDao.insertIncome(income) // <-- Usa el DAO para insertar en la BD
        println("FreelanceIncomeRepository: Ingreso freelance guardado PERSISTENTEMENTE: $income")
    }

    override fun getAllIncomes(): Flow<List<FreelanceIncome>> {
        return freelanceIncomeDao.getAllIncomes() // <-- Usa el DAO para obtener los ingresos como un Flow
    }
}