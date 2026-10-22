package com.example.myapplication.utils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
fun getCategoryIcon(categoryName: String): ImageVector {
    return when (categoryName) {
        "Salary" -> Icons.Filled.AttachMoney
        "Freelance" -> Icons.Filled.Computer
        "Investment" -> Icons.AutoMirrored.Filled.TrendingUp
        "Gift" -> Icons.Filled.CardGiftcard
        "Other Income" -> Icons.Filled.AttachMoney
        "Food" -> Icons.Filled.Restaurant
        "Transport" -> Icons.Filled.DirectionsCar
        "Shopping" -> Icons.Filled.ShoppingBag
        "Entertainment" -> Icons.Filled.Movie
        "Health" -> Icons.Filled.LocalHospital
        "Education" -> Icons.Filled.School
        "Bills" -> Icons.Filled.Receipt
        "Rent" -> Icons.Filled.Home
        "Other Expense" -> Icons.Filled.CreditCard
        else -> Icons.Filled.AttachMoney
    }
}