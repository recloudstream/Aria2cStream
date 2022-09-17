package com.lagradost.fetchbutton.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lagradost.fetchbutton.aria2c.Aria2Settings
import com.lagradost.fetchbutton.aria2c.Aria2Starter
import com.lagradost.fetchbutton.aria2c.newUriRequest
import com.lagradost.fetchbutton.ui.PieFetchButton
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val downloadButton = findViewById<PieFetchButton>(R.id.download_button)

        thread {
            Aria2Starter.start(
                this,
                Aria2Settings(
                    UUID.randomUUID().toString(),
                    4337,
                    filesDir.path,
                    "${filesDir.path}/session"
                )
            )
        }
        val path = this.filesDir.path + "/FetchButtonTest/test.bin"

        downloadButton.setPersistentId(1)
        downloadButton.setOnClickListener {

        }
        downloadButton.setDefaultClickListener {
            val id = downloadButton.persistentId ?: return@setDefaultClickListener listOf()
            listOf(
                //newUriRequest(id,"magnet:?xt=urn:btih:f7a543c036b06ba973beea56e98543c9e315314d&dn=%5BSubsPlease%5D%20Yofukashi%20no%20Uta%20-%2010%20%281080p%29%20%5B52314177%5D.mkv&tr=http%3A%2F%2Fnyaa.tracker.wf%3A7777%2Fannounce&tr=udp%3A%2F%2Fopen.stealth.si%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337%2Fannounce&tr=udp%3A%2F%2Fexodus.desync.com%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.torrent.eu.org%3A451%2Fannounce")
                newUriRequest(
                    id, "https://speed.hetzner.de/1010MB.bin", "Hello World",
                ),
                newUriRequest(
                    id, "https://speed.hetzner.de/100MB.bin", "Hello World2",
                ),
                //newUriRequest(
                //    id, "https://speed.hetzner.de/100MB.bin", "Hello World",
                //)
            )
        }
    }
}

