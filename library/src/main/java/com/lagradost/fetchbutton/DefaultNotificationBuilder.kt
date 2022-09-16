package com.lagradost.fetchbutton
/*
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.lagradost.fetchbutton.services.VideoDownloadService
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.DownloadNotification
import com.tonyodev.fetch2.Status

//const val DOWNLOAD_CHANNEL_ID = "fetch.downloading"
//const val DOWNLOAD_CHANNEL_NAME = "Downloads"
//const val DOWNLOAD_CHANNEL_DESCRIPTION = "The download notification channel"

enum class DownloadActionType {
    Pause,
    Resume,
    Stop,
}

data class NotificationMetaData(
    @ColorInt val iconColor: Int,
    val contentTitle: String,
    val subText: String?,
    val rowTwoExtra: String?,
    val posterBitmap: Bitmap?,
    val linkName: String?,
    val secondRow: String
)

object DefaultNotificationBuilder {
    @DrawableRes
    val imgDone = R.drawable.download_icon_done

    @DrawableRes
    val imgDownloading = R.drawable.download_icon_load

    @DrawableRes
    val imgPaused = R.drawable.download_icon_pause

    @DrawableRes
    val imgStopped = R.drawable.download_icon_error

    @DrawableRes
    val imgError = R.drawable.download_icon_error

    @DrawableRes
    val pressToPauseIcon = R.drawable.ic_baseline_pause_24

    @DrawableRes
    val pressToResumeIcon = R.drawable.ic_baseline_play_arrow_24

    @DrawableRes
    val pressToStopIcon = R.drawable.ic_baseline_stop_24


    fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        channelDescription: String
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = channelDescription
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(
        context: Context,
        notificationMetaData: NotificationMetaData,
        download: DownloadNotification,
        pendingIntent: PendingIntent?,
        inputBuilder: NotificationCompat.Builder? = null
    ): Notification? {
        try {
            if (download.downloaded <= 0) return null // crash, invalid data
            createNotificationChannel(
                context,
                context.getString(R.string.download_channel_id),
                context.getString(R.string.download_channel_name),
                context.getString(R.string.download_channel_description)
            )

            val realBuilder =
                inputBuilder ?: NotificationCompat.Builder(
                    context,
                    context.getString(R.string.download_channel_id)
                )

            val builder = realBuilder
                .setAutoCancel(true)
                .setColorized(true)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(notificationMetaData.iconColor)
                .setContentTitle(notificationMetaData.contentTitle)
                .setSmallIcon(
                    when (download.status) {
                        Status.COMPLETED -> imgDone
                        Status.DOWNLOADING -> imgDownloading
                        Status.QUEUED, Status.PAUSED, Status.ADDED -> imgPaused
                        Status.FAILED -> imgError
                        Status.REMOVED, Status.CANCELLED, Status.DELETED -> imgStopped
                        else -> imgDownloading
                    }
                )

            if (notificationMetaData.subText != null) {
                builder.setSubText(notificationMetaData.subText)
            }

            if (pendingIntent != null) {
                builder.setContentIntent(pendingIntent)
            }

            if (download.status == Status.DOWNLOADING || download.status == Status.PAUSED) {
                if (download.progress < 0) {
                    builder.setProgress(0, 0, true)
                } else {
                    builder.setProgress(100, download.progress, false)
                }
            }

            val downloadFormat = context.getString(R.string.download_format)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (notificationMetaData.posterBitmap != null)
                    builder.setLargeIcon(notificationMetaData.posterBitmap)


                val progressMbString = "%.1f MB".format(download.total / 1000000f)
                val totalMbString = "%.1f MB".format(download.total / 1000000f)
                val suffix = ""

                val bigText =
                    when (download.status) {
                        Status.DOWNLOADING, Status.PAUSED -> {
                            (notificationMetaData.linkName?.let { "$it\n" }
                                ?: "") + "${notificationMetaData.secondRow}\n${download.progress} % ($progressMbString/$totalMbString)$suffix"
                        }
                        Status.FAILED -> {
                            downloadFormat.format(
                                context.getString(R.string.download_failed),
                                notificationMetaData.secondRow
                            )
                        }
                        Status.COMPLETED -> {
                            downloadFormat.format(
                                context.getString(R.string.download_done),
                                notificationMetaData.secondRow
                            )
                        }
                        else -> {
                            downloadFormat.format(
                                context.getString(R.string.download_canceled),
                                notificationMetaData.secondRow
                            )
                        }
                    }

                val bodyStyle = NotificationCompat.BigTextStyle()
                bodyStyle.bigText(bigText)
                builder.setStyle(bodyStyle)
            } else {
                val txt =
                    when (download.status) {
                        Status.DOWNLOADING, Status.PAUSED -> {
                            notificationMetaData.secondRow
                        }
                        Status.FAILED -> {
                            downloadFormat.format(
                                context.getString(R.string.download_failed),
                                notificationMetaData.secondRow
                            )
                        }
                        Status.COMPLETED -> {
                            downloadFormat.format(
                                context.getString(R.string.download_done),
                                notificationMetaData.secondRow
                            )
                        }
                        else -> {
                            downloadFormat.format(
                                context.getString(R.string.download_canceled),
                                notificationMetaData.secondRow
                            )
                        }
                    }

                builder.setContentText(txt)
            }

            if ((download.status == Status.DOWNLOADING || download.status == Status.PAUSED) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val actionTypes: MutableList<DownloadActionType> = ArrayList()
                if (download.status == Status.DOWNLOADING) {
                    actionTypes.add(DownloadActionType.Pause)
                    actionTypes.add(DownloadActionType.Stop)
                }

                if (download.status == Status.PAUSED) {
                    actionTypes.add(DownloadActionType.Resume)
                    actionTypes.add(DownloadActionType.Stop)
                }

                // ADD ACTIONS
                for ((index, i) in actionTypes.withIndex()) {
                    val actionResultIntent = Intent(context, VideoDownloadService::class.java)

                    actionResultIntent.putExtra(
                        "type", i.ordinal
                    )

                    actionResultIntent.putExtra("id", download.notificationId)

                    val pending: PendingIntent = PendingIntent.getService(
                        // BECAUSE episodes lying near will have the same id +1, index will give the same requested as the previous episode, *100000 fixes this
                        context, (4337 + index * 1000000 + download.notificationId),
                        actionResultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    builder.addAction(
                        NotificationCompat.Action(
                            when (i) {
                                DownloadActionType.Resume -> pressToResumeIcon
                                DownloadActionType.Pause -> pressToPauseIcon
                                DownloadActionType.Stop -> pressToStopIcon
                            }, when (i) {
                                DownloadActionType.Resume -> context.getString(R.string.resume)
                                DownloadActionType.Pause -> context.getString(R.string.pause)
                                DownloadActionType.Stop -> context.getString(R.string.cancel)
                            }, pending
                        )
                    )
                }
            }
            return builder.build()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}*/
