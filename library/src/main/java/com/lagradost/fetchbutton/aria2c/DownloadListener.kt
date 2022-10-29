package com.lagradost.fetchbutton.aria2c

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

object DownloadListener {
    //private val currentDownloadData: HashMap<String, DownloadStatus> = hashMapOf()
    private val downloadDataUpdateCount = MutableLiveData<Int>()
    val sessionIdToGid = hashMapOf<Long, String>()
    val sessionGidToId = hashMapOf<String, Long>()

    fun insert(gid: String, id: Long) {
        sessionIdToGid[id] = gid
        sessionGidToId[gid] = id
    }

    fun remove(gid: String?, id: Long?) {
        sessionIdToGid.remove(id ?: sessionGidToId[gid] ?: return)
        sessionGidToId.remove(gid ?: sessionIdToGid[id] ?: return)
    }

    val sessionIdToLastRequest = hashMapOf<Long, UriRequest>()
    //val sessionGIdToNotification = hashMapOf<String, NotificationMetaData>()

    //private val currentDownloadDataMutex = Mutex()

    val currentDownloadStatus: HashMap<String, AbstractClient.JsonTell> = hashMapOf()

    var mainListener : ((Metadata) -> Unit)? = null

    // this points to the parent gid
    //val follow: HashMap<String, String> = hashMapOf()

    private fun getStatus(gid: String): AbstractClient.JsonTell? {
        return currentDownloadStatus[gid]
    }

    val failQueueMap = hashMapOf<String, List<UriRequest>>()
    val failQueueMapMutex = Mutex()

    fun observe(scope: LifecycleOwner, collector: (Int) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            downloadDataUpdateCount.observe(scope) {
                collector(it)
            }
        }
    }

    //fun getDownloadStatus(gid: String): DownloadStatus? {
    //    return currentDownloadData[gid]
    //}

    fun addCount(new: Int) {
        downloadDataUpdateCount.postValue(new)
    }

    fun getInfo(gid: String): Metadata {
        val out = ArrayList<AbstractClient.JsonTell>()
        val current = getStatus(gid) ?: return Metadata(arrayListOf())
        out.add(current)
        for (followers in current.followedBy) {
            out.addAll(getInfo(followers).items)
        }
        return Metadata(out)
    }

    // returns if gid was added
    /*suspend fun insertGid(gid: String): Boolean {
        gidMutex.withLock {
            if (gids.contains(gid)) {
                return false
            } else {
                gids += gid
                return true
            }
        }
    }*/

    //suspend fun setDownloadStatus(gid: String, status: DownloadStatus) {
    //    currentDownloadDataMutex.lock {
    //        currentDownloadData[gid] = status
    //    }
    //    // return insertGid(gid)
    //}
}
