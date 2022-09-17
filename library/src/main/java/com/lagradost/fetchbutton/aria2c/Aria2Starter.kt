package com.lagradost.fetchbutton.aria2c

import android.app.Activity
import android.util.Log
import java.io.File

data class Aria2Settings(
    val token: String,
    val port: Int,
    val dir: String,
    val sessionDir: String?, // null for not save
    //val checkCertificate : Boolean = false,
)

data class Aria2OverrideClientSettings(
    val statusUpdateRateMs: Long = 1000,
    val timeout: Int = 10,
    val client: ((AbstractClient.Profile) -> AbstractClient)? = null
)

object Aria2Starter {
    const val TAG = "Aria2"

    var client: AbstractClient? = null
    var aria2: Aria2? = null

    fun start(
        activity: Activity,
        settings: Aria2Settings,
        clientSettings: Aria2OverrideClientSettings = Aria2OverrideClientSettings()
    ) {
        val parent: File = activity.filesDir

        aria2 = Aria2.get().also { ar ->
            ar.loadEnv(
                parent,
                File(activity.applicationInfo.nativeLibraryDir, "libaria2c.so"),
                File(parent, "session"),
                settings
            )

            ar.addListener(object : Aria2.MessageListener {
                override fun onMessage(msg: Message) {
                    Log.i(TAG, msg.toString())
                }
            })

            ar.start()
            val profile = AbstractClient.Profile(
                serverSsl = false,
                serverAddr = "localhost",
                serverEndpoint = "/jsonrpc",
                serverPort = settings.port,
                timeout = clientSettings.timeout,
                token = settings.token,
                statusUpdateRateMs = clientSettings.statusUpdateRateMs,
            )

            client = clientSettings.client?.invoke(profile) ?: WebsocketClient(profile).apply {
                connect()
                initStatusUpdateLoop()
            }
        }
    }
}