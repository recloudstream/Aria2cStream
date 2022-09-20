package com.lagradost.fetchbutton

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.fetchbutton.aria2c.DownloadStatusTell
import com.lagradost.fetchbutton.aria2c.Metadata
import com.lagradost.fetchbutton.services.VideoDownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL


//const val DOWNLOAD_CHANNEL_ID = "fetch.downloading"
//const val DOWNLOAD_CHANNEL_NAME = "Downloads"
//const val DOWNLOAD_CHANNEL_DESCRIPTION = "The download notification channel"

enum class DownloadActionType {
    Pause,
    Resume,
    Stop,
}

data class NotificationMetaData(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("iconColor")
    @ColorInt val iconColor: Int,
    @JsonProperty("contentTitle")
    val contentTitle: String,
    @JsonProperty("subText")
    val subText: String?,
    @JsonProperty("rowTwoExtra")
    val rowTwoExtra: String?,
    @JsonProperty("posterUrl")
    val posterUrl: String?,
    @JsonProperty("linkName")
    val linkName: String?,
    @JsonProperty("secondRow")
    val secondRow: String
) {
    companion object {
        val bitmaps: HashMap<String, Bitmap> = hashMapOf()
    }

    val posterBitmap: Bitmap?
        get() {
            val url = posterUrl
            val bitmap = bitmaps[url]
            if (bitmap == null && !url.isNullOrBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val image =
                            BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
                        bitmaps[url] = image
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return bitmap
        }
}

object DefaultNotificationBuilder {
    @DrawableRes
    var imgWaiting = R.drawable.download_icon_load

    @DrawableRes
    var imgDone = R.drawable.download_icon_done

    @DrawableRes
    var imgDownloading = R.drawable.download_icon_load

    @DrawableRes
    var imgPaused = R.drawable.download_icon_pause

    @DrawableRes
    var imgStopped = R.drawable.download_icon_error

    @DrawableRes
    var imgError = R.drawable.download_icon_error

    @DrawableRes
    var pressToPauseIcon = R.drawable.ic_baseline_pause_24

    @DrawableRes
    var pressToResumeIcon = R.drawable.ic_baseline_play_arrow_24

    @DrawableRes
    var pressToStopIcon = R.drawable.ic_baseline_stop_24


    private var hasCreatedNotificationChannel = false
    private fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        channelDescription: String
    ) {
        if (hasCreatedNotificationChannel) return
        hasCreatedNotificationChannel = true
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

    data class NotificationData(
        val notification: Notification,
        val id: Int,
    )

    fun createNotification(
        context: Context,
        notificationMetaData: NotificationMetaData,
        download: Metadata,
        pendingIntent: PendingIntent?,
        inputBuilder: NotificationCompat.Builder? = null,
        gid: String? = null,
        id: Long? = null,
    ): NotificationData? {
        try {
            if (download.items.isEmpty()) return null // crash, invalid data
            // if(download.status == DownloadStatusTell.Waiting) return null

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
                        DownloadStatusTell.Complete -> imgDone
                        DownloadStatusTell.Active -> imgDownloading
                        DownloadStatusTell.Paused -> imgPaused
                        DownloadStatusTell.Error -> imgError
                        DownloadStatusTell.Removed -> imgStopped
                        DownloadStatusTell.Waiting -> imgWaiting
                        else -> imgDownloading
                    }
                )

            if (notificationMetaData.subText != null) {
                builder.setSubText(notificationMetaData.subText)
            }

            if (pendingIntent != null) {
                builder.setContentIntent(pendingIntent)
            }

            if (download.status == DownloadStatusTell.Active
                || download.status == DownloadStatusTell.Paused
                || download.status == DownloadStatusTell.Waiting
            ) {
                if (download.downloadedLength <= 1024 || download.status == DownloadStatusTell.Waiting) {
                    builder.setProgress(0, 0, true)
                } else {
                    builder.setProgress(100, download.progressPercentage, false)
                }
            }

            val downloadFormat = context.getString(R.string.download_format)
            val downloadProgressFormat = context.getString(R.string.download_progress_format)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val posterBitmap = notificationMetaData.posterBitmap
                if (posterBitmap != null)
                    builder.setLargeIcon(posterBitmap)

                val progressMb = download.downloadedLength / 1000000f
                val totalMb = download.totalLength / 1000000f
                val downloadSpeedMb = download.downloadSpeed / 1000000f

                val bigText =
                    when (download.status) {
                        DownloadStatusTell.Active, DownloadStatusTell.Paused -> {
                            (notificationMetaData.linkName?.let { "$it\n" }
                                ?: "") +
                                    downloadProgressFormat.format(
                                        notificationMetaData.secondRow,
                                        download.progressPercentage,
                                        progressMb,
                                        totalMb,
                                        downloadSpeedMb
                                    )
                        }
                        DownloadStatusTell.Error -> {
                            downloadFormat.format(
                                context.getString(R.string.download_failed),
                                notificationMetaData.secondRow
                            )
                        }
                        DownloadStatusTell.Complete -> {
                            downloadFormat.format(
                                context.getString(R.string.download_done),
                                notificationMetaData.secondRow
                            )
                        }
                        DownloadStatusTell.Removed -> {
                            downloadFormat.format(
                                context.getString(R.string.download_canceled),
                                notificationMetaData.secondRow
                            )
                        }
                        else -> ""
                    }

                val bodyStyle = NotificationCompat.BigTextStyle()
                bodyStyle.bigText(bigText)
                builder.setStyle(bodyStyle)
            } else {
                val txt =
                    when (download.status) {
                        DownloadStatusTell.Active, DownloadStatusTell.Paused -> {
                            notificationMetaData.secondRow
                        }
                        DownloadStatusTell.Error -> {
                            downloadFormat.format(
                                context.getString(R.string.download_failed),
                                notificationMetaData.secondRow
                            )
                        }
                        DownloadStatusTell.Complete -> {
                            downloadFormat.format(
                                context.getString(R.string.download_done),
                                notificationMetaData.secondRow
                            )
                        }
                        DownloadStatusTell.Removed -> {
                            downloadFormat.format(
                                context.getString(R.string.download_canceled),
                                notificationMetaData.secondRow
                            )
                        }
                        else -> ""
                    }

                builder.setContentText(txt)
            }

            if ((download.status == DownloadStatusTell.Active || download.status == DownloadStatusTell.Paused) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val actionTypes: MutableList<DownloadActionType> = ArrayList()
                if (download.status == DownloadStatusTell.Active) {
                    actionTypes.add(DownloadActionType.Pause)
                    actionTypes.add(DownloadActionType.Stop)
                }

                if (download.status == DownloadStatusTell.Paused) {
                    actionTypes.add(DownloadActionType.Resume)
                    actionTypes.add(DownloadActionType.Stop)
                }

                // ADD ACTIONS
                for ((index, i) in actionTypes.withIndex()) {
                    val actionResultIntent = Intent(context, VideoDownloadService::class.java)

                    actionResultIntent.putExtra(
                        "type", i.ordinal
                    )

                    actionResultIntent.putExtra("gid", gid)
                    actionResultIntent.putExtra("id", id)

                    val pending: PendingIntent = PendingIntent.getService(
                        // BECAUSE episodes lying near will have the same id +1, index will give the same requested as the previous episode, *100000 fixes this
                        context, (4337 + index * 1000000 + notificationMetaData.id),
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
            return NotificationData(builder.build(), notificationMetaData.id)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
