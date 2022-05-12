package com.codemave.mobilecomputing.data.room

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.codemave.mobilecomputing.data.entity.Category
import com.codemave.mobilecomputing.data.entity.Notification
import java.util.*

class PaymentToCategory {
    @Embedded
    lateinit var notification: Notification

    @Relation(parentColumn = "payment_category_id", entityColumn = "id")
    lateinit var _categories: List<Category>

    @get:Ignore
    val category: Category
        get() = _categories[0]

    /**
     * Allow this class to be destructured by consumers
     */
    operator fun component1() = notification
    operator fun component2() = category

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is PaymentToCategory -> notification== other.notification && _categories == other._categories
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(notification, _categories)
}