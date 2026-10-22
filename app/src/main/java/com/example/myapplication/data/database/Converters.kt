package com.example.myapplication.data.database
import androidx.room.TypeConverter
import com.example.myapplication.data.model.TransactionType
import com.example.myapplication.data.model.BillingCycle
import com.example.myapplication.data.model.ContactTransactionType
import java.util.Date
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }
    @TypeConverter
    fun toTransactionType(type: String): TransactionType {
        return TransactionType.valueOf(type)
    }
    @TypeConverter
    fun fromBillingCycle(cycle: BillingCycle): String {
        return cycle.name
    }
    @TypeConverter
    fun toBillingCycle(cycle: String): BillingCycle {
        return BillingCycle.valueOf(cycle)
    }
    @TypeConverter
    fun fromContactTransactionType(type: ContactTransactionType): String {
        return type.name
    }
    @TypeConverter
    fun toContactTransactionType(type: String): ContactTransactionType {
        return ContactTransactionType.valueOf(type)
    }
}