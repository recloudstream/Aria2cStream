package com.lagradost.fetchbutton.aria2c

import android.app.Activity
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.lagradost.fetchbutton.Aria2Save.removeKey
import com.lagradost.fetchbutton.aria2c.DownloadListener.sessionIdToLastRequest
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
    //TODO https://developer.android.com/training/data-storage/shared/documents-files
    /*fun checkPermission(request: UriRequest): Boolean {
        request.directory?.let { dir ->
            if (!File(dir).canWrite()) {
                println("CANT WRITE TO $dir")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveActivity.get()?.let { act ->
                        act.contentResolver
                        val sm: StorageManager =
                            act.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
                        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        val uri: Uri =
                            intent.getParcelableExtra("android.provider.extra.INITIAL_URI") ?: return false
                        var scheme = uri.toString()
                        Log.d("TAG", "INITIAL_URI scheme: $scheme")
                        scheme = scheme.replace("/root/", "/document/")
                        val finalDirPath = "$scheme%3A$dir"
                        val furi = Uri.parse(finalDirPath)
                        intent.putExtra("android.provider.extra.INITIAL_URI", furi)
                        intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
                        Log.d("TAG", "uri: $furi")
                        act.startActivityForResult(intent, 6);
                    }
                }
                return false
            }
        }
        return true
    }*/

    fun download(request: UriRequest) {
      //  if (!checkPermission(request)) return

        //
//
        //
        //
        //
        //
        //
        //
        //
        //
//
        //
        client?.download(request) {}
    }

    fun download(request: List<UriRequest>) {
        //if (request.any { !checkPermission(it) }) return

        client?.downloadFailQueue(request) { _, _ -> }
    }

    fun download(vararg requests: UriRequest) {
       // if (requests.any { !checkPermission(it) }) return
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
            client?.run {
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