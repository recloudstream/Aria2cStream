package com.lagradost.fetchbutton.aria2c

import android.app.Activity
import java.io.File
import java.util.*

data class Aria2Settings(
    val token: String,
    val port: Int,
)

object Aria2Starter {
    var client: AbstractClient? = null
    fun start(activity: Activity) {
        val port = 6093//nextInt(2000, 8000)
        val settings = Aria2Settings(UUID.randomUUID().toString(), port)

        val profile = AbstractClient.Profile(
            serverSsl = false,
            serverAddr = "localhost",
            serverEndpoint = "/jsonrpc",
            serverPort = settings.port,
            timeout = 10,
            token = settings.token,
            statusUpdateRateMs = 1000,
        )
        val parent: File = activity.filesDir

        val aria2 = Aria2.get().also { ar ->
            ar.loadEnv(
                parent,
                File(activity.applicationInfo.nativeLibraryDir, "libaria2c.so"),
                File(parent, "session"),
                settings
            )
            ar.addListener { message ->
                println("MESSAGE: $message")
            }
            ar.start()
            client = WebsocketClient(profile).apply {
                connect()
                initStatusUpdateLoop()
            }
        }
    }
}