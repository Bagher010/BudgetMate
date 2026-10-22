package com.example.myapplication.data.model
import androidx.compose.runtime.Stable
import androidx.room.Embedded
import androidx.room.Relation
@Stable
data class TransactionWithCategory(
    @Embedded val transaction: Transaction,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category
)