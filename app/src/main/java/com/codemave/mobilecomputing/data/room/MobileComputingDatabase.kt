package com.codemave.mobilecomputing.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codemave.mobilecomputing.data.entity.Category
import com.codemave.mobilecomputing.data.entity.Notification

/**
 * The [RoomDatabase] for this app
 */
@Database(
    entities = [Category::class, Notification::class],
    version = 2,
    exportSchema = false
)
abstract class MobileComputingDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun paymentDao(): PaymentDao
}