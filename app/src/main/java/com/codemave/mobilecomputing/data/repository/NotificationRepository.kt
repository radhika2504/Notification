package com.codemave.mobilecomputing.data.repository

import com.codemave.mobilecomputing.data.entity.Notification
import com.codemave.mobilecomputing.data.room.PaymentDao
import com.codemave.mobilecomputing.data.room.PaymentToCategory
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [Notification] instances
 */
class NotificationRepository(
    private val paymentDao: PaymentDao
) {
    /**
     * Returns a flow containing the list of payments associated with the category with the
     * given [categoryId]
     */
    fun paymentsInCategory(categoryId: Long) : Flow<List<PaymentToCategory>> {
        return paymentDao.paymentsFromCategory(categoryId)
    }

    /**
     * Add a new [Notification] to the payment store
     */
    suspend fun addPayment(payment: Notification) = paymentDao.insert(payment)
}