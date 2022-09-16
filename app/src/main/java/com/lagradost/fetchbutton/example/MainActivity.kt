package com.lagradost.fetchbutton.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lagradost.fetchbutton.aria2c.Aria2Starter
import com.lagradost.fetchbutton.aria2c.UriRequest
import com.lagradost.fetchbutton.ui.PieFetchButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val downloadButton = findViewById<PieFetchButton>(R.id.download_button)

        Aria2Starter.start(this)

        val path = this.filesDir.path + "/FetchButtonTest/test.bin"

        downloadButton.setDefaultClickListener {
            UriRequest(
                //listOf("magnet:?xt=urn:btih:498990357dc9b88bd808525c21fbfb54aa12f559&dn=%5BSubsPlease%5D%20Tensei%20Kenja%20no%20Isekai%20Life%20-%2012%20%281080p%29%20%5B49E2D53B%5D.mkv&tr=http%3A%2F%2Fnyaa.tracker.wf%3A7777%2Fannounce&tr=udp%3A%2F%2Fopen.stealth.si%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337%2Fannounce&tr=udp%3A%2F%2Fexodus.desync.com%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.torrent.eu.org%3A451%2Fannounce")
                listOf("https://speed.hetzner.de/100MB.bin")
                //listOf("https://speedtest-co.turnkeyinternet.net/1000mb.bin"),
            )
        }
    }
}

