package com.example.myapplication.data
import android.content.Context
import com.example.myapplication.data.database.FinanceDatabase
import com.example.myapplication.data.repository.FinanceRepository
object RepositoryFactory {
    fun createRepository(context: Context): FinanceRepository {
        val database = FinanceDatabase.getDatabase(context)
        return FinanceRepository(
            transactionDao = database.transactionDao(),
            categoryDao = database.categoryDao(),
            subscriptionDao = database.subscriptionDao(),
            contactDao = database.contactDao()
        )
    }
    fun createRepository(database: FinanceDatabase): FinanceRepository {
        return FinanceRepository(
            transactionDao = database.transactionDao(),
            categoryDao = database.categoryDao(),
            subscriptionDao = database.subscriptionDao(),
            contactDao = database.contactDao()
        )
    }
}