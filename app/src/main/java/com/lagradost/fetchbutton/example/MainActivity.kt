package com.lagradost.fetchbutton.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lagradost.fetchbutton.aria2c.Aria2Settings
import com.lagradost.fetchbutton.aria2c.Aria2Starter
import com.lagradost.fetchbutton.aria2c.newUriRequest
import com.lagradost.fetchbutton.ui.PieFetchButton
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val downloadButton = findViewById<PieFetchButton>(R.id.download_button)

        Aria2Starter.start(
            this,
            Aria2Settings(
                UUID.randomUUID().toString(),
                4337,
                filesDir.path,
                "${filesDir.path}/session"
            )
        )

        val path = this.filesDir.path + "/FetchButtonTest/test.bin"

        downloadButton.setPersistentId(1)
        downloadButton.setDefaultClickListener {
            newUriRequest(
                "https://speed.hetzner.de/100MB.bin", "Hello World",
            )
        }
    }
}

