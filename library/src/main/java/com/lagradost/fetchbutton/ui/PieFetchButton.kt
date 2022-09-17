package com.lagradost.fetchbutton.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.lagradost.fetchbutton.R
import com.lagradost.fetchbutton.aria2c.AbstractClient
import com.lagradost.fetchbutton.aria2c.DownloadStatusTell
import com.lagradost.fetchbutton.aria2c.UriRequest
import com.lagradost.fetchbutton.aria2c.getDownloadStatusFromTell
import com.lagradost.fetchbutton.utils.Coroutines.ioThread

open class PieFetchButton(context: Context, attributeSet: AttributeSet) :
    BaseFetchButton(context, attributeSet) {

    private lateinit var progressBarBackground: View
    private lateinit var statusView: ImageView

    private var currentStatus: DownloadStatusTell? = null

    override fun init() {
        inflate(R.layout.download_button_view)
        progressBar = findViewById(R.id.progress_downloaded)
        progressBarBackground = findViewById(R.id.progress_downloaded_background)
        statusView = findViewById(R.id.image_download_status)
        setStatus(null)
    }

    open fun setDefaultClickListener(requestGetter: suspend BaseFetchButton.() -> List<UriRequest>) {
        this.setOnClickListener {
            when (this.currentStatus) {
                null -> {
                    setStatus(DownloadStatusTell.Waiting)
                    ioThread {
                        val request = requestGetter.invoke(this)
                        if (request.size == 1) {
                            performDownload(request.first())
                        } else if (request.isNotEmpty()) {
                            performFailQueueDownload(request)
                        }
                    }
                }
                DownloadStatusTell.Paused -> {
                    resumeDownload()
                }
                DownloadStatusTell.Active -> {
                    pauseDownload()
                }
                DownloadStatusTell.Error -> {
                    redownload()
                }
                else -> {}
            }
        }
    }

    /** Also sets currentStatus */
    override fun setStatus(status: DownloadStatusTell?) {
        currentStatus = status

        //progressBar.isVisible =
        //    status != null && status != DownloadStatusTell.Complete && status != DownloadStatusTell.Error
        //progressBarBackground.isVisible = status != null && status != DownloadStatusTell.Complete
        val isPreActive = isZeroBytes && status == DownloadStatusTell.Active

        if (status == DownloadStatusTell.Waiting || isPreActive) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.rotate_around_center_point)
            progressBarBackground.startAnimation(animation)
        } else {
            progressBarBackground.clearAnimation()
        }

        val progressDrawable =
            if (status == DownloadStatusTell.Active && !isPreActive) R.drawable.circle_shape else R.drawable.circle_shape_dotted

        progressBarBackground.background =
            ContextCompat.getDrawable(context, progressDrawable)

        val drawable = getDrawableFromStatus(status)
        statusView.setImageDrawable(drawable)
        val isDrawable = drawable != null

        statusView.isVisible = isDrawable
        if (isDrawable) progressBar.clearAnimation()
        if (isDrawable) progressBarBackground.clearAnimation()
        progressBarBackground.isVisible = status == DownloadStatusTell.Error || !isDrawable
        progressBar.isGone = isDrawable
    }

    override fun resetView() {
        setStatus(null)
        isZeroBytes = true
        progressBar.progress = 0
    }

    override fun updateViewOnDownload(status: ArrayList<AbstractClient.JsonTell>) {
        println("updateViewOnDownload = $status")
        if (status.isEmpty()) {
            resetView()
            return
        }

        var downloadedBytes: Long = 0
        var totalBytes: Long = 0

        val statusList = Array(status.size) { i ->
            getDownloadStatusFromTell(status[i].status)
        }

        val newStatus = when { // this is the priority sorter based on all the files
            statusList.contains(DownloadStatusTell.Active) -> DownloadStatusTell.Active
            statusList.contains(DownloadStatusTell.Waiting) -> DownloadStatusTell.Waiting
            statusList.contains(DownloadStatusTell.Error) -> DownloadStatusTell.Error
            statusList.contains(DownloadStatusTell.Paused) -> DownloadStatusTell.Paused
            statusList.contains(DownloadStatusTell.Removed) -> DownloadStatusTell.Removed
            statusList.contains(DownloadStatusTell.Complete) -> DownloadStatusTell.Complete
            else -> null
        }

        if (newStatus == null) {
            resetView()
            return
        }

        for (item in status) {
            totalBytes += item.totalLength
            downloadedBytes += item.completedLength
        }

        val isDone =
            newStatus == DownloadStatusTell.Complete || (downloadedBytes > 1024 && downloadedBytes + 1024 >= totalBytes)

        if (isDone)
            setStatus(DownloadStatusTell.Complete)
        else {
            setProgress(downloadedBytes, totalBytes)
            setStatus(newStatus)
        }
    }

    open fun getDrawableFromStatus(status: DownloadStatusTell?): Drawable? {
        val drawableInt = when (status) {
            DownloadStatusTell.Paused, DownloadStatusTell.Waiting, DownloadStatusTell.Active -> null
            DownloadStatusTell.Error -> R.drawable.download_icon_error
            DownloadStatusTell.Complete -> R.drawable.download_icon_done
            DownloadStatusTell.Removed, null -> R.drawable.netflix_download
        }
        return drawableInt?.let { ContextCompat.getDrawable(this.context, it) }
    }
}