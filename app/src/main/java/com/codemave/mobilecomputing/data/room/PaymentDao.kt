package com.codemave.mobilecomputing.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.codemave.mobilecomputing.data.entity.Notification
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PaymentDao {
    @Query("""
        SELECT payments.* FROM payments
        INNER JOIN categories ON payments.payment_category_id = categories.id
        WHERE payment_category_id = :categoryId
    """)
    abstract fun paymentsFromCategory(categoryId: Long): Flow<List<PaymentToCategory>>

    @Query("""SELECT * FROM payments WHERE id = :paymentId""")
    abstract fun payment(paymentId: Long): Notification?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Notification): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Notification)

    @Delete
    abstract suspend fun delete(entity: Notification): Int
}