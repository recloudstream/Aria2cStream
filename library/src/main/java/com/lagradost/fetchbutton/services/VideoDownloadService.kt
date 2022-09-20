package com.lagradost.fetchbutton.services

import android.app.IntentService
import android.content.Intent
import com.lagradost.fetchbutton.DownloadActionType
import com.lagradost.fetchbutton.aria2c.Aria2Starter

class VideoDownloadService : IntentService("VideoDownloadService") {
    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        println("onHandleIntent!!!")
        if (intent != null) {
            val gid = intent.getStringExtra("gid")
            val id = intent.getLongExtra("id", 0L)
            val type = intent.getIntExtra("type", -1)
            println(">>>> gid = $gid id = $id type = $type!!!")
            if (!gid.isNullOrBlank() && type != -1 && id != 0L) {
                val state = DownloadActionType.values().getOrNull(type) ?: return
                when (state) {
                    DownloadActionType.Pause -> {
                        Aria2Starter.pause(gid)
                    }
                    DownloadActionType.Stop -> {
                        Aria2Starter.delete(gid, id)
                    }
                    DownloadActionType.Resume -> {
                        Aria2Starter.unpause(gid)
                    }
                }
            }
        }
    }
}