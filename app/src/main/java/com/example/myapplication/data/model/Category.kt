package com.example.myapplication.data.model
import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
@Entity(
    tableName = "categories",
    indices = [Index(value = ["name", "type"], unique = true)] 
)
@Stable
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val type: TransactionType
)