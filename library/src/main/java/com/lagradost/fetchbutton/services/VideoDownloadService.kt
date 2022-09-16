package com.lagradost.fetchbutton.services

import android.app.IntentService
import android.content.Intent

class VideoDownloadService : IntentService("VideoDownloadService") {
    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val id = intent.getIntExtra("id", -1)
            val type = intent.getIntExtra("type", -1)
            if (id != -1 && type != -1) {
                //val state = DownloadActionType.values().getOrNull(type) ?: return
                //runDownloadAction(state, id)
            }
        }
    }
}