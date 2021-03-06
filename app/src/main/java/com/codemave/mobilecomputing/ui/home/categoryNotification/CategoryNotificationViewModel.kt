package com.codemave.mobilecomputing.ui.home.categoryNotification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codemave.mobilecomputing.Graph
import com.codemave.mobilecomputing.data.repository.NotificationRepository
import com.codemave.mobilecomputing.data.room.PaymentToCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CategoryPaymentViewModel(
    private val categoryId: Long,
    private val paymentRepository: NotificationRepository = Graph.notificationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryPaymentViewState())

    val state: StateFlow<CategoryPaymentViewState>
        get() = _state

    init {
        viewModelScope.launch {
            paymentRepository.paymentsInCategory(categoryId).collect { list ->
                _state.value = CategoryPaymentViewState(
                    payments = list
                )
            }
        }
    }
}

data class CategoryPaymentViewState(
    val payments: List<PaymentToCategory> = emptyList()
)