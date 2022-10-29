package com.lagradost.fetchbutton.aria2c

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.fetchbutton.NotificationMetaData

enum class DownloadStatus {
    Started,
    Paused,
    Stopped,
    Completed,
    Error,
    CompletedAndStartSeed,
}

enum class DownloadStatusTell {
    /**currently downloading/seeding downloads*/
    Active,

    /**for downloads in the queue; download is not started*/
    Waiting,

    /**for paused downloads*/
    Paused,

    /**for downloads that were stopped because of error*/
    Error,

    /**for stopped and completed downloads*/
    Complete,

    /**for the downloads removed by user*/
    Removed,
}

/**Docs over at https://aria2.github.io/manual/en/html/aria2c.html*/
data class UriRequest(
    /** THIS IS A NON ARIA2 RELATED SETTING set to null if you don't want to track it*/
    @JsonProperty("id")
    val id: Long? = null,

    /** THiS IS ONLY FOR NOTIFICATION, set to null if you don't want any notification from this download*/
    @JsonProperty("notificationMetaData")
    val notificationMetaData: NotificationMetaData? = null,

    /**uris is an array of HTTP/FTP/SFTP/BitTorrent URIs (strings) pointing to the same resource.
     * If you mix URIs pointing to different resources, then the download may fail or be corrupted
     * without aria2 complaining. When adding BitTorrent Magnet URIs, uris must have only one element
     * and it should be BitTorrent Magnet URI.*/
    @JsonProperty("uris")
    val uris: List<String>,

    /** --out, The file name of the downloaded file. It is always relative to the directory
     * given in --dir option. When the --force-sequential option is used, this option is ignored.*/
    @JsonProperty("fileName")
    val fileName: String? = null,

    /** --dir, The directory to store the downloaded file.*/
    @JsonProperty("directory")
    val directory: String? = null,

    /** --header, Append HEADER to HTTP request header. */
    @JsonProperty("headers")
    val headers: Map<String, String> = emptyMap(),

    /** --referer, Set an http referrer (Referer). This affects all http/https downloads. If * is given,
     * the download URI is also used as the referrer. This may be useful when used together with the
     * --parameterized-uri option.*/
    @JsonProperty("referer")
    val referer: String? = null,

    /** --check-integrity, Check file integrity by validating piece hashes or a hash of entire file.
     * This option has effect only in BitTorrent, Metalink downloads with checksums or HTTP(S)/FTP
     * downloads with --checksum option. If piece hashes are provided, this option can detect damaged
     * portions of a file and re-download them. If a hash of entire file is provided, hash check is
     * only done when file has been already download. This is determined by file length.
     * If hash check fails, file is re-downloaded from scratch. If both piece hashes and a hash
     * of entire file are provided, only piece hashes are used. Default: false */
    @JsonProperty("checkIntegrity")
    val checkIntegrity: Boolean? = null,

    /** --continue, Continue downloading a partially downloaded file. Use this option to resume a
     * download started by a web browser or another program which downloads files sequentially
     * from the beginning. Currently this option is only applicable to HTTP(S)/FTP downloads.
     * */
    @JsonProperty("continueDownload")
    val continueDownload: Boolean? = null,

    /** --user-agent, Set user agent for HTTP(S) downloads. Default: aria2/$VERSION, $VERSION is replaced by package version.
     * */
    @JsonProperty("userAgent")
    val userAgent: String? = null,

    /** --seed-time, Specify seeding time in (fractional) minutes. Also see the --seed-ratio option.
     * NOTE! Specifying --seed-time=0 disables seeding after download completed. */
    @JsonProperty("seedTime")
    val seedTime: Float? = null,

    /** --seed-ratio, Specify share ratio. Seed completed torrents until share ratio reaches RATIO.
     * You are strongly encouraged to specify equals or more than 1.0 here.
     * Specify 0.0 if you intend to do seeding regardless of share ratio.
     * If --seed-time option is specified along with this option, seeding ends when at least one
     * of the conditions is satisfied. Default: 1.0
     * */
    @JsonProperty("seedRatio")
    val seedRatio: Float? = null,
)

/** Creates a uri request without using the data class
 *
 * @param uri url to the raw stream or magnet link
 * @param fileName output filename, will not override torrent names, null will default the name based on the url
 * @param directory the output directory, will cause download error if you don't have access to that directory,
 * null defaults to the directory specified when starting aria2c
 * @param headers the request headers
 * @param userAgent defaults to aria2/$VERSION
 * @param seed if false, disables seeding after download completed
 * */
fun newUriRequest(
    id: Long?,
    uri: String,
    fileName: String? = null,
    directory: String? = null,
    headers: Map<String, String> = emptyMap(),
    userAgent: String? = null,
    seed: Boolean = false,
    notificationMetaData: NotificationMetaData? = null,
): UriRequest {
    return UriRequest(
        id = id,
        uris = listOf(uri),
        fileName = fileName,
        headers = headers,
        directory = directory,
        userAgent = userAgent,
        seedTime = if (seed) null else 0.0f,
        notificationMetaData = notificationMetaData
    )
}

data class SavedData(
    @JsonProperty("uriRequest") val uriRequest: UriRequest,
    @JsonProperty("files") val files: List<AbstractClient.JsonFile>
)

fun getDownloadStatusFromTell(str: String?): DownloadStatusTell? {
    return when (str) {
        "active" -> DownloadStatusTell.Active
        "complete" -> DownloadStatusTell.Complete
        "waiting" -> DownloadStatusTell.Waiting
        "error" -> DownloadStatusTell.Error
        "removed" -> DownloadStatusTell.Removed
        "paused" -> DownloadStatusTell.Paused
        else -> null
    }
}

data class SmallJsonResponse(
    @JsonProperty("id") val id: String,
)

data class JsonIdResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("error") val error: JsonError?,
    @JsonProperty("result") val resultGid: String,
)

//data class JsonMethodResponse(
//    @JsonProperty("method") val method: String,
//    @JsonProperty("params") val params: ArrayList<Map<String, String>> = arrayListOf(),
//)

data class JsonError(
    @JsonProperty("code") val code: Int,
    @JsonProperty("message") val message: String,
)

data class Metadata(
    @JsonProperty("items")
    val items: ArrayList<AbstractClient.JsonTell>
) {
    val status by lazy { getStatus(items) }
    val totalLength by lazy { items.sumOf { it.totalLength } }
    val downloadedLength by lazy { items.sumOf { it.completedLength } }
    val progressPercentage by lazy { (downloadedLength * 100L / (totalLength + 1L)).toInt() }
    val downloadSpeed by lazy { items.sumOf { it.downloadSpeed } }
}

fun getStatus(status: ArrayList<AbstractClient.JsonTell>): DownloadStatusTell? {
    val statusList = Array(status.size) { i ->
        getDownloadStatusFromTell(status[i].status)
    }

    return when { // this is the priority sorter based on all the files
        statusList.contains(DownloadStatusTell.Active) -> DownloadStatusTell.Active
        statusList.contains(DownloadStatusTell.Waiting) -> DownloadStatusTell.Waiting
        statusList.contains(DownloadStatusTell.Error) -> DownloadStatusTell.Error
        statusList.contains(DownloadStatusTell.Paused) -> DownloadStatusTell.Paused
        statusList.contains(DownloadStatusTell.Removed) -> DownloadStatusTell.Removed
        statusList.contains(DownloadStatusTell.Complete) -> DownloadStatusTell.Complete
        else -> null
    }
}