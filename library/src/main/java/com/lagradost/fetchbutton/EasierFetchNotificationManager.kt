package com.lagradost.fetchbutton

/*
import android.content.Context
import androidx.core.app.NotificationCompat
import com.tonyodev.fetch2.DefaultFetchNotificationManager
import com.tonyodev.fetch2.DownloadNotification
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration


var globalFetch: Fetch? = null

fun initFetch(context: Context): Fetch {
    val fetchConfiguration = FetchConfiguration.Builder(context)
        .setDownloadConcurrentLimit(5)
        .setNotificationManager(EasierFetchNotificationManager(context))
        .build()
    return fetchConfiguration.getNewFetchInstanceFromConfiguration().also { globalFetch = it }
}

/**
 * Override getSubtitleText and updateNotification
 * and you have a proper notifications
 *
 * getSubtitleText: "$progressPercentage % - $timeLeft"
 * updateNotification: Add to builder to get a proper notification
 * */

open class EasierFetchNotificationManager(
    private val context: Context,
) :
    DefaultFetchNotificationManager(context) {
//    private val identifierHashMap: HashMap<Int, Long> = hashMapOf()

    override fun getFetchInstanceForNamespace(namespace: String): Fetch {
        return globalFetch ?: initFetch(context)
    }

    override fun shouldCancelNotification(downloadNotification: DownloadNotification): Boolean {
        return false
    }

    override fun getNotificationTimeOutMillis(): Long {
        return 30_000L
    }

//    override fun updateNotification(
//        notificationBuilder: NotificationCompat.Builder,
//        downloadNotification: DownloadNotification,
//        context: Context
//    ) {
//        DefaultNotificationBuilder.createNotification(
//            context,
//            NotificationMetaData(
//                ...
//            ),
//            downloadNotification,
//            pendingIntent = null,
//            notificationBuilder
//        )
//        super.updateNotification(notificationBuilder, downloadNotification, context)
//    }

//    override fun postDownloadUpdate(download: Download): Boolean {
//        identifierHashMap[download.id] = download.identifier
//        return super.postDownloadUpdate(download)
//    }

    override fun cancelNotification(notificationId: Int) {
        super.cancelNotification(notificationId)
        globalFetch?.remove(notificationId)
    }
}*/