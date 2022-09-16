package com.lagradost.fetchbutton.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.lagradost.fetchbutton.aria2c.AbstractClient
import com.lagradost.fetchbutton.aria2c.Aria2Starter
import com.lagradost.fetchbutton.aria2c.UriRequest

abstract class BaseFetchButton(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    lateinit var progressBar: ContentLoadingProgressBar
    var gid: String? = null

    fun inflate(@LayoutRes layout: Int) {
        inflate(context, layout, this)
    }

    /**
     * Create your view here with inflate(layout) and other stuff.
     * Akin to onCreateView.
     * */
    abstract fun init()

    init {
        init()
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
        resetView()
        Aria2Starter.client?.download(request) {
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
        val localGid = gid ?: return
        Aria2Starter.client?.run {
            unpause(localGid)
            forceUpdate()
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