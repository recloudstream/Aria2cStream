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
import com.lagradost.fetchbutton.aria2c.DownloadStatusTell
import com.lagradost.fetchbutton.aria2c.Metadata
import com.lagradost.fetchbutton.aria2c.UriRequest
import com.lagradost.fetchbutton.utils.Coroutines.ioThread


open class PieFetchButton(context: Context, attributeSet: AttributeSet) :
    BaseFetchButton(context, attributeSet) {

    private var waitingAnimation: Int = 0
    private var animateWaiting: Boolean = false
    private var activeOutline: Int = 0
    private var nonActiveOutline: Int = 0

    private var iconInit: Int = 0
    private var iconError: Int = 0
    private var iconComplete: Int = 0
    private var iconActive: Int = 0
    private var iconWaiting: Int = 0
    private var iconRemoved: Int = 0
    private var iconPaused: Int = 0
    private var hideWhenIcon: Boolean = true

    companion object {
        val fillArray = arrayOf(
            R.drawable.circular_progress_bar_clockwise,
            R.drawable.circular_progress_bar_counter_clockwise,
            R.drawable.circular_progress_bar_small_to_large,
            R.drawable.circular_progress_bar_top_to_bottom,
        )
        val videoFormats = listOf(
            ".3g2",
            ".3gp",
            ".amv",
            ".asf",
            ".avi",
            ".drc",
            ".flv",
            ".f4v",
            ".f4p",
            ".f4a",
            ".f4b",
            ".gif",
            ".gifv",
            ".m4v",
            ".mkv",
            ".mng",
            ".mov",
            ".qt",
            ".mp4",
            ".m4p",
            ".mpg", ".mp2", ".mpeg", ".mpe", ".mpv",
            ".mpg", ".mpeg", ".m2v",
            ".MTS", ".M2TS", ".TS",
            ".mxf",
            ".nsv",
            ".ogv", ".ogg",
            //".rm", // Made for RealPlayer
            //".rmvb", // Made for RealPlayer
            ".svi",
            ".viv",
            ".vob",
            ".webm",
            ".wmv",
            ".yuv"
        )
    }

    private var progressBarBackground: View
    private var statusView: ImageView

    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.PieFetchButton, 0, 0).apply {
            inflate(
                getResourceId(
                    R.styleable.PieFetchButton_aria2c_layout,
                    R.layout.download_button_view
                )
            )

            progressBar = findViewById(R.id.progress_downloaded)
            progressBarBackground = findViewById(R.id.progress_downloaded_background)
            statusView = findViewById(R.id.image_download_status)

            animateWaiting = getBoolean(
                R.styleable.PieFetchButton_aria2c_animate_waiting,
                true
            )
            hideWhenIcon = getBoolean(
                R.styleable.PieFetchButton_aria2c_hide_when_icon,
                true
            )

            waitingAnimation = getResourceId(
                R.styleable.PieFetchButton_aria2c_waiting_animation,
                R.anim.rotate_around_center_point
            )

            activeOutline = getResourceId(
                R.styleable.PieFetchButton_aria2c_outline_active, R.drawable.circle_shape
            )

            nonActiveOutline = getResourceId(
                R.styleable.PieFetchButton_aria2c_outline_non_active, R.drawable.circle_shape_dotted
            )
            iconInit = getResourceId(
                R.styleable.PieFetchButton_aria2c_icon_init, R.drawable.netflix_download
            )
            iconError = getResourceId(
                R.styleable.PieFetchButton_aria2c_icon_paused, R.drawable.download_icon_error
            )
            iconComplete = getResourceId(
                R.styleable.PieFetchButton_aria2c_icon_complete, R.drawable.download_icon_done
            )
            iconPaused = getResourceId(
                R.styleable.PieFetchButton_aria2c_icon_paused, 0//R.drawable.download_icon_pause
            )
            iconActive = getResourceId(
                R.styleable.PieFetchButton_aria2c_icon_active, 0 //R.drawable.download_icon_load
            )
            iconWaiting = getResourceId(
                R.styleable.PieFetchButton_aria2c_icon_waiting, 0
            )
            iconRemoved = getResourceId(
                R.styleable.PieFetchButton_aria2c_icon_removed, R.drawable.netflix_download
            )

            val fillIndex = getInt(R.styleable.PieFetchButton_aria2c_fill, 0)

            val progressDrawable = getResourceId(
                R.styleable.PieFetchButton_aria2c_fill_override, fillArray[fillIndex]
            )

            progressBar.progressDrawable = ContextCompat.getDrawable(context, progressDrawable)

            recycle()
        }
        resetView()
    }

    var currentStatus: DownloadStatusTell? = null

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

    fun getVideos() = getFiles(suffixList = videoFormats, ignoreCase = true)

    fun getFiles(suffixList: List<String> = emptyList(), ignoreCase: Boolean = true): List<String> {
        return if (suffixList.isEmpty()) {
            files.map { file -> file.path }
        } else {
            files.map { file -> file.path }.filter { path ->
                suffixList.any { suf -> path.endsWith(suffix = suf, ignoreCase = ignoreCase) }
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

        if (animateWaiting && (status == DownloadStatusTell.Waiting || isPreActive)) {
            val animation = AnimationUtils.loadAnimation(context, waitingAnimation)
            progressBarBackground.startAnimation(animation)
        } else {
            progressBarBackground.clearAnimation()
        }

        val progressDrawable =
            if (status == DownloadStatusTell.Active && !isPreActive) activeOutline else nonActiveOutline

        progressBarBackground.background =
            ContextCompat.getDrawable(context, progressDrawable)

        val drawable = getDrawableFromStatus(status)
        statusView.setImageDrawable(drawable)
        val isDrawable = drawable != null

        statusView.isVisible = isDrawable
        val hide = hideWhenIcon && isDrawable
        if (hide) {
            progressBar.clearAnimation()
            progressBarBackground.clearAnimation()
        }
        progressBarBackground.isGone = hide
        progressBar.isGone = hide
    }

    override fun resetView() {
        setStatus(null)
        isZeroBytes = true
        progressBar.progress = 0
    }

    override fun updateViewOnDownload(metadata: Metadata) {
        if (metadata.items.isEmpty()) {
            resetView()
            return
        }

        val newStatus = metadata.status

        if (newStatus == null) {
            resetView()
            return
        }

        val isDone =
            newStatus == DownloadStatusTell.Complete || (metadata.downloadedLength > 1024 && metadata.downloadedLength + 1024 >= metadata.totalLength)

        if (isDone)
            setStatus(DownloadStatusTell.Complete)
        else {
            setProgress(metadata.downloadedLength, metadata.totalLength)
            setStatus(newStatus)
        }
    }

    open fun getDrawableFromStatus(status: DownloadStatusTell?): Drawable? {
        val drawableInt = when (status) {
            DownloadStatusTell.Paused -> iconPaused
            DownloadStatusTell.Waiting -> iconWaiting
            DownloadStatusTell.Active -> iconActive
            DownloadStatusTell.Error -> iconError
            DownloadStatusTell.Complete -> iconComplete
            DownloadStatusTell.Removed -> iconRemoved
            null -> iconInit
        }
        if (drawableInt == 0) {
            return null
        }
        return ContextCompat.getDrawable(this.context, drawableInt)
    }
}