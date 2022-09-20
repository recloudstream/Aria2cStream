package com.lagradost.fetchbutton.aria2c

import android.app.Activity
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.lagradost.fetchbutton.Aria2Save.removeKey
import com.lagradost.fetchbutton.aria2c.AbstractClient.DownloadListener.sessionIdToLastRequest
import com.lagradost.fetchbutton.utils.Coroutines.mainThread
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.lang.ref.WeakReference

data class Aria2Settings(
    val token: String,
    val port: Int,
    val dir: String,
    val sessionDir: String? = null, // null for not save
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

    fun pause(gid: String, all: Boolean = true) {
        client?.run {
            pause(gid, all)
        }
    }

    fun unpause(gid: String, all: Boolean = true) {
        client?.run {
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
                deleteFiles(AbstractClient.DownloadListener.getInfo(localGid).items.map { it.files }
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
            AbstractClient.DownloadListener.remove(localGid, id)

            // remove aria2
            client?.run {
                AbstractClient.DownloadListener.failQueueMapMutex.withLock {
                    AbstractClient.DownloadListener.failQueueMap.remove(localGid)
                }

                AbstractClient.DownloadListener.currentDownloadStatus.remove(localGid)

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