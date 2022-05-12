package com.codemave.mobilecomputing.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.codemave.mobilecomputing.R
import com.codemave.mobilecomputing.Graph
import com.codemave.mobilecomputing.data.entity.Category
import com.codemave.mobilecomputing.data.entity.Notification
import com.codemave.mobilecomputing.data.repository.CategoryRepository
import com.codemave.mobilecomputing.data.repository.NotificationRepository
import com.codemave.mobilecomputing.ui.home.HomeViewModel
import com.codemave.mobilecomputing.util.NotificationWorker
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class PaymentViewModel(
    private val paymentRepository: NotificationRepository = Graph.notificationRepository,
    private val categoryRepository: CategoryRepository = Graph.categoryRepository,
): ViewModel() {
    private val _state = MutableStateFlow(PaymentViewState())

    val state: StateFlow<PaymentViewState>
        get() = _state


    suspend fun savePayment(payment: Notification): Long {
        createPaymentMadeNotification(payment,Graph.appContext)
        return paymentRepository.addPayment(payment)
    }

    init {
        createNotificationChannel(context = Graph.appContext)
        setOneTimeNotification()
        viewModelScope.launch {
            categoryRepository.categories().collect { categories ->
                _state.value = PaymentViewState(categories)
            }
        }
    }
}

private fun setOneTimeNotification() {
    val workManager = WorkManager.getInstance(Graph.appContext)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(10, TimeUnit.SECONDS)
        .setConstraints(constraints)
        .build()

    workManager.enqueue(notificationWorker)

}

private fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "NotificationChannelName"
        val descriptionText = "NotificationChannelDescriptionText"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }
        // register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


private fun createPaymentMadeNotification(payment: Notification,context:Context) {

    val notificationId = 2

    val intent = Intent(context,HomeViewModel::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    val remoteInput: androidx.core.app.RemoteInput =androidx.core.app.RemoteInput.Builder("Key_text_Reply").setLabel("Your answer").build()
    val replyIntent=Intent(context,HomeViewModel::class.java)
    replyIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

    val replyPendingIntent:PendingIntent= PendingIntent.getActivity(context,1,replyIntent,PendingIntent.FLAG_ONE_SHOT)
    val replyAction:NotificationCompat.Action=NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground,"Reply",replyPendingIntent).addRemoteInput(remoteInput).build()
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle(payment.paymentTitle)
        .setContentText(payment.paymentDescription)
        .setAutoCancel(false)
        .setContentIntent(pendingIntent)
        .addAction(replyAction)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    with(NotificationManagerCompat.from(Graph.appContext)) {
        notify(notificationId, builder.build())
    }
}



data class PaymentViewState(
    val categories: List<Category> = emptyList()
)