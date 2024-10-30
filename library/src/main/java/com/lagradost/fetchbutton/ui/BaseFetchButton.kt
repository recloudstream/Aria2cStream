package com.lagradost.fetchbutton.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.fetchbutton.Aria2Save.getKey
import com.lagradost.fetchbutton.aria2c.*
import com.lagradost.fetchbutton.aria2c.DownloadListener.sessionIdToGid
import com.lagradost.fetchbutton.aria2c.DownloadListener.sessionIdToLastRequest

abstract class BaseFetchButton(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    var persistentId: Long? = null // used to save sessions

    data class SavedData(
        @JsonProperty("uriRequest") val uriRequest: UriRequest,
        @JsonProperty("files") val files: List<AbstractClient.JsonFile>
    )

    lateinit var progressBar: ContentLoadingProgressBar
    val gid: String? get() = sessionIdToGid[persistentId]

    // used for resuming data
    var _lastRequestOverride: UriRequest? = null
    var lastRequest: UriRequest?
        get() = _lastRequestOverride ?: sessionIdToLastRequest[persistentId]
        set(value) {
            _lastRequestOverride = value
        }

    protected var isZeroBytes: Boolean = true
    var files: List<AbstractClient.JsonFile> = emptyList()

    fun inflate(@LayoutRes layout: Int) {
        inflate(context, layout, this)
    }

    init {
        resetViewData()
    }

    open fun resetViewData() {
        lastRequest = null
        isZeroBytes = true
        persistentId = null
    }

    fun setPersistentId(id: Long) {
        persistentId = id
        gid?.let { _ ->
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
                // some extra padding for just in case
                setStatus(if (downloadedBytes > 1024L && downloadedBytes + 1024L >= totalBytes) DownloadStatusTell.Complete else DownloadStatusTell.Paused)
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
        val info = DownloadListener.getInfo(gid ?: return)
        files = info.items.flatMap { it.files }

        updateViewOnDownload(info)
    }

    /**
     * No checks required. Arg will always include a download with current id
     * */
    abstract fun updateViewOnDownload(metadata: Metadata)

    /**
     * Look at all global downloads, used to subscribe to one of them.
     * */
    private fun observeStatusUpdate(observer: (Int) -> Unit) {
        this.findViewTreeLifecycleOwner()?.let {
            DownloadListener.observe(it, observer)
        }
    }

    /**
     * Get a clean slate again, might be useful in recyclerview?
     * */
    abstract fun resetView()

    open fun performDownload(request: UriRequest) {
        Aria2Starter.instance?.client?.download(request) { _ ->
            lastRequest = null
        }
    }

    open fun performFailQueueDownload(request: List<UriRequest>) {
        Aria2Starter.instance?.client?.downloadFailQueue(request) { _, _ ->
            lastRequest = null
        }
    }

    fun pauseDownload() {
        setStatus(DownloadStatusTell.Waiting)
        val localGid = gid ?: return
        Aria2Starter.instance?.client?.run {
            pause(localGid)
            forceUpdate()
        }
    }

    fun resumeDownload(): Boolean {
        setStatus(DownloadStatusTell.Waiting)
        gid?.let { localGid ->
            Aria2Starter.instance?.client?.run {
                unpause(localGid)
                forceUpdate()
            }?.also {
                return true
            }
        } ?: run {
            //val req = lastRequest ?: return@run
            //var hasAny = false
            //for (file in files) {
            //    try {
            //        val filePath = file.path + ".aria2"
            //        val f = File(filePath)
            //        if (f.exists()) {
            //            performDownload(req.copy(uris = listOf(filePath)))
            //            hasAny = true
            //        }
            //    } catch (e: Exception) {
            //        println(e)
            //    }
            //}
            //
            //if (!hasAny)
            performDownload(lastRequest ?: return@run)
            return true
        }
        return false
    }

    fun redownload() {
        //val localGid = gid ?: return
        setStatus(DownloadStatusTell.Waiting)
        Aria2Starter.instance?.client?.run {
            //remove(localGid)
            download(lastRequest ?: return@run) { }
            forceUpdate()
        }
    }

    fun deleteAllFiles() {
        Aria2Starter.delete(gid, persistentId, files)

        // update UI
        updateViewOnDownload(Metadata(arrayListOf()))
    }
}