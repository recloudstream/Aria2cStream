package com.lagradost.fetchbutton.aria2c

import android.app.Activity
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.fetchbutton.Aria2Save.removeKey
import com.lagradost.fetchbutton.aria2c.DownloadListener.sessionIdToLastRequest
import com.lagradost.fetchbutton.utils.Coroutines.mainThread
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.lang.ref.WeakReference
import java.net.ServerSocket

/// https://gist.github.com/vorburger/3429822
fun findPort(): Int? {
    var socket: ServerSocket? = null
    try {
        socket = ServerSocket(0)
        socket.reuseAddress = true
        val port = socket.localPort
        try {
            socket.close()
        } catch (_: Throwable) {
        }
        return port
    } catch (_: Throwable) {
    } finally {
        if (socket != null) {
            try {
                socket.close()
            } catch (_: Throwable) {
            }
        }
    }
    return null
}


data class Aria2Settings(
    @JsonProperty("token")
    val token: String,
    /** if randomizePort is on, it will choose this port as a backup, otherwise it will use this port directly **/
    @JsonProperty("port")
    val port: Int,
    @JsonProperty("dir")
    val dir: String,
    @JsonProperty("sessionDir")
    val sessionDir: String? = null, // null for not save
    @JsonProperty("randomizePort")
    val randomizePort: Boolean = true,
    //val checkCertificate : Boolean = false,
) {
    val availablePort: Int by lazy {
        if (randomizePort) {
            findPort() ?: port
        } else {
            port
        }
    }
}

data class Aria2OverrideClientSettings(
    val statusUpdateRateMs: Long = 1000,
    val timeout: Int = 10,
    val client: ((AbstractClient.Profile) -> AbstractClient)? = null
)

data class Aria2Bundle(
    val client: AbstractClient,
    val settings: Aria2Settings,
    val server: Aria2,
) {
    fun restartServer(): Boolean {
        return try {
            server.delete() and server.start()
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }
    }

    fun restartClient() {
        client.close()
        client.connect()
    }

    fun refresh() {
        if (!server.isRunning) {
            restartServer()
        }
        if (!client.isRunning) {
            restartClient()
        }
    }

    suspend fun stop() {
        client.shutdownAsync() // stop by sending the shutdown signal
        server.delete() // stop it forcefully
        client.close() // stop the client
    }
}

object Aria2Starter {
    const val TAG = "Aria2"
    var instance: Aria2Bundle? = null

    // this is used to store keys
    var saveActivity: WeakReference<Activity> = WeakReference(null)

    fun refresh() {
        instance?.refresh()
    }

    suspend fun shutdown() {
        instance?.stop()
        instance = null
    }

    fun download(request: UriRequest) {
        instance?.client?.download(request) {}
    }

    fun download(request: List<UriRequest>) {
        instance?.client?.downloadFailQueue(request) { _, _ -> }
    }

    fun download(vararg requests: UriRequest) {
        instance?.client?.downloadFailQueue(requests.toList()) { _, _ -> }
    }

    fun pause(gid: String, all: Boolean = true) {
        instance?.client?.run {
            pause(gid, all)
        }
    }

    fun pauseAll() {
        instance?.client?.pauseAll()
    }

    fun unpauseAll() {
        instance?.client?.unpauseAll()
    }

    fun unpause(gid: String, all: Boolean = true) {
        instance?.client?.run {
            unpause(gid, all)
            //forceUpdate()
        }
    }

    fun deleteFiles(files: List<AbstractClient.JsonFile>) {
        files.map { file -> file.path }.forEach { path ->
            try {
                File(path).delete()
                File("$path.aria2").delete()
            } catch (_: Throwable) {
            }
        }
    }

    fun delete(gid: String?, id: Long?, files: List<AbstractClient.JsonFile> = emptyList()) {
        // delete files
        if (files.isEmpty()) {
            gid?.let { localGid ->
                deleteFiles(DownloadListener.getInfo(localGid).items.map { it.files }
                    .flatten())
            }
        } else {
            deleteFiles(files)
        }


        // remove keys
        if (id == null) return

        saveActivity.get()?.removeKey(id)
        sessionIdToLastRequest[id]?.notificationMetaData?.id?.let { nid ->
            mainThread {
                NotificationManagerCompat.from(
                    saveActivity.get() ?: return@mainThread
                ).cancel(nid)
            }
        }
        sessionIdToLastRequest.remove(id)

        gid?.let { localGid ->
            // remove id from session
            DownloadListener.remove(localGid, id)

            // remove aria2
            instance?.client?.run {
                DownloadListener.failQueueMapMutex.withLock {
                    DownloadListener.failQueueMap.remove(localGid)
                }

                DownloadListener.currentDownloadStatus.remove(localGid)

                remove(localGid)
            }
        }
    }


    fun start(
        activity: Activity,
        settings: Aria2Settings,
        clientSettings: Aria2OverrideClientSettings = Aria2OverrideClientSettings()
    ) {
        saveActivity = WeakReference(activity)

        if (instance != null) {
            refresh()
            return
        }

        val server = Aria2.get().also { ar ->
            val parent: File = activity.filesDir
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
        }

        val profile = AbstractClient.Profile(
            serverSsl = false,
            serverAddr = "localhost",
            serverEndpoint = "/jsonrpc",
            serverPort = settings.availablePort,
            timeout = clientSettings.timeout,
            token = settings.token,
            statusUpdateRateMs = clientSettings.statusUpdateRateMs,
        )

        val client = clientSettings.client?.invoke(profile) ?: WebsocketClient(profile).apply {
            connect()
            initStatusUpdateLoop()
        }

        instance = Aria2Bundle(client = client, server = server, settings = settings)
    }
}