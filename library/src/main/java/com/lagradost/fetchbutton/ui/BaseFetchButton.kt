package com.lagradost.fetchbutton.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.fetchbutton.Aria2Save.getKey
import com.lagradost.fetchbutton.Aria2Save.setKey
import com.lagradost.fetchbutton.aria2c.AbstractClient
import com.lagradost.fetchbutton.aria2c.Aria2Starter
import com.lagradost.fetchbutton.aria2c.DownloadStatusTell
import com.lagradost.fetchbutton.aria2c.UriRequest

abstract class BaseFetchButton(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private var _persistentId: Long? = null // used to save sessions

    data class SavedData(
        @JsonProperty("uriRequest") val uriRequest: UriRequest,
        @JsonProperty("files") val files: List<AbstractClient.JsonFile>
    )

    lateinit var progressBar: ContentLoadingProgressBar
    protected var gid: String? = null
    protected var lastRequest: UriRequest? = null
    protected var isZeroBytes: Boolean = true
    var files: List<AbstractClient.JsonFile> = emptyList()

    companion object {
        val sessionIdToGid = hashMapOf<Long, String>()
        const val setKeyRate = 1
    }

    fun inflate(@LayoutRes layout: Int) {
        inflate(context, layout, this)
    }

    /**
     * Create your view here with inflate(layout) and other stuff.
     * Akin to onCreateView.
     * */
    abstract fun init()

    init {
        resetViewData()
        init()
    }

    open fun resetViewData() {
        gid = null
        lastRequest = null
        isZeroBytes = true
        _persistentId = null
    }

    fun setPersistentId(id: Long) {
        _persistentId = id
        sessionIdToGid[id]?.let { localGid ->
            gid = localGid
            updateViewOnDownloadWithChecks(-1)
        } ?: run {
            this.context?.getKey<SavedData>(id)?.let { savedData ->
                lastRequest = savedData.uriRequest
                files = savedData.files

                var totalBytes: Long = 0
                var downloadedBytes: Long = 0
                for (file in savedData.files) {
                    downloadedBytes += file.completedLength
                    totalBytes += file.length
                }
                setProgress(downloadedBytes, totalBytes)
                setStatus(DownloadStatusTell.Paused)
            } ?: run {
                resetView()
            }
        }
    }

    abstract fun setStatus(status: DownloadStatusTell?)

    open fun setProgress(downloadedBytes: Long, totalBytes: Long) {
        isZeroBytes = downloadedBytes == 0L
        val steps = 10000L
        progressBar.max = steps.toInt()
        // div by zero error and 1 byte off is ok impo
        val progress = (downloadedBytes * steps / (totalBytes + 1L)).toInt()

        val animation = ProgressBarAnimation(
            progressBar,
            progressBar.progress.toFloat(),
            progress.toFloat()
        ).apply {
            fillAfter = true
            duration =
                if (progress > progressBar.progress) // we don't want to animate backward changes in progress
                    100
                else
                    0L
        }
        progressBar.startAnimation(animation)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Always listens to downloads
        observeStatusUpdate(::updateViewOnDownloadWithChecks)
    }

    /**
     * Safer internal updateViewOnDownload
     * */
    private fun updateViewOnDownloadWithChecks(updateCount: Int) {
        val info = AbstractClient.DownloadListener.getInfo(gid ?: return)
        files = info.map { it.files }.flatten()
        if (updateCount % setKeyRate == 0)
            context?.setKey(
                _persistentId ?: return,
                SavedData(lastRequest ?: return, files)
            )
        updateViewOnDownload(info)
    }

    /**
     * No checks required. Arg will always include a download with current id
     * */
    abstract fun updateViewOnDownload(status: ArrayList<AbstractClient.JsonTell>)

    /**
     * Look at all global downloads, used to subscribe to one of them.
     * */
    private fun observeStatusUpdate(observer: (Int) -> Unit) {
        this.findViewTreeLifecycleOwner()?.let {
            AbstractClient.DownloadListener.observe(it, observer)
        }
    }

    /**
     * Get a clean slate again, might be useful in recyclerview?
     * */
    abstract fun resetView()

    open fun performDownload(request: UriRequest) {
        Aria2Starter.client?.download(request) {
            lastRequest = request
            gid = it
            println("GID====$gid")
        }
    }

    fun pauseDownload() {
        val localGid = gid ?: return
        Aria2Starter.client?.run {
            pause(localGid)
            forceUpdate()
        }
    }

    fun resumeDownload() {
        gid?.let { localGid ->
            Aria2Starter.client?.run {
                unpause(localGid)
                forceUpdate()
            }
        } ?: run {
            performDownload(lastRequest ?: return)
        }
    }

    fun cancelDownload() {
        val localGid = gid ?: return
        Aria2Starter.client?.run {
            remove(localGid)
            forceUpdate()
        }
    }
}