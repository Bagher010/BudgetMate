package com.example.myapplication.data.model
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String = "",
    val email: String = "",
    val avatarColor: String = "#6200EE",
    val notes: String = "",
    val createdAt: Date = Date()
)
@Entity(
    tableName = "contact_transactions",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("contactId")]
)
data class ContactTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactId: Long,
    val amount: Double,
    val description: String,
    val type: ContactTransactionType,
    val date: Date = Date(),
    val isSettled: Boolean = false,
    val settledDate: Date? = null
)
enum class ContactTransactionType(val displayName: String) {
    LENT("Lent"),
    BORROWED("Borrowed"),
    BILL_SPLIT("Bill Split"),
    SETTLED("Settled")
}
data class ContactWithBalance(
    val contact: Contact,
    val totalOwed: Double,
    val totalOwing: Double,
    val netBalance: Double,
    val transactionCount: Int
)