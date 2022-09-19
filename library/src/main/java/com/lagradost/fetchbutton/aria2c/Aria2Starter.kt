package com.lagradost.fetchbutton.aria2c

import android.app.Activity
import android.util.Log
import java.io.File
import java.lang.ref.WeakReference

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

    // this is used to store keys
    var saveActivity: WeakReference<Activity> = WeakReference(null)

    fun download(request: UriRequest) {
        client?.download(request) {}
    }

    fun download(request: List<UriRequest>) {
        client?.downloadFailQueue(request) { _, _ -> }
    }

    fun download(vararg requests: UriRequest) {
        client?.downloadFailQueue(requests.toList()) { _, _ -> }
    }

    //fun pause(gid: String, all: Boolean = true) {
    //    client?.run {
    //        pause(gid, all)
    //        //forceUpdate()
    //    }
    //}

    //fun unpause(gid: String, all: Boolean = true) {
    //    client?.run {
    //        unpause(gid, all)
    //        //forceUpdate()
    //    }
    //}

    fun start(
        activity: Activity,
        settings: Aria2Settings,
        clientSettings: Aria2OverrideClientSettings = Aria2OverrideClientSettings()
    ) {
        saveActivity = WeakReference(activity)
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