package com.lagradost.fetchbutton.aria2c

import com.fasterxml.jackson.annotation.JsonProperty

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
    /**uris is an array of HTTP/FTP/SFTP/BitTorrent URIs (strings) pointing to the same resource.
     * If you mix URIs pointing to different resources, then the download may fail or be corrupted
     * without aria2 complaining. When adding BitTorrent Magnet URIs, uris must have only one element
     * and it should be BitTorrent Magnet URI.*/
    val uris: List<String>,
    /** --out, The file name of the downloaded file. It is always relative to the directory
     * given in --dir option. When the --force-sequential option is used, this option is ignored.*/
    val fileName: String? = null,
    /** --dir, The directory to store the downloaded file.*/
    val directory: String? = null,
    /** --header, Append HEADER to HTTP request header. */
    val headers: Map<String, String> = emptyMap(),
    /** --referer, Set an http referrer (Referer). This affects all http/https downloads. If * is given,
     * the download URI is also used as the referrer. This may be useful when used together with the
     * --parameterized-uri option.*/
    val referer: String? = null,
    /** --check-integrity, Check file integrity by validating piece hashes or a hash of entire file.
     * This option has effect only in BitTorrent, Metalink downloads with checksums or HTTP(S)/FTP
     * downloads with --checksum option. If piece hashes are provided, this option can detect damaged
     * portions of a file and re-download them. If a hash of entire file is provided, hash check is
     * only done when file has been already download. This is determined by file length.
     * If hash check fails, file is re-downloaded from scratch. If both piece hashes and a hash
     * of entire file are provided, only piece hashes are used. Default: false */
    val checkIntegrity: Boolean? = null,
    /** --continue, Continue downloading a partially downloaded file. Use this option to resume a
     * download started by a web browser or another program which downloads files sequentially
     * from the beginning. Currently this option is only applicable to HTTP(S)/FTP downloads.
     * */
    val continueDownload: Boolean? = null,
    /** --user-agent, Set user agent for HTTP(S) downloads. Default: aria2/$VERSION, $VERSION is replaced by package version.
     * */
    val userAgent: String? = null,
    /** --seed-time, Specify seeding time in (fractional) minutes. Also see the --seed-ratio option.
     * NOTE! Specifying --seed-time=0 disables seeding after download completed. */
    val seedTime: Float? = null,
    /** --seed-ratio, Specify share ratio. Seed completed torrents until share ratio reaches RATIO.
     * You are strongly encouraged to specify equals or more than 1.0 here.
     * Specify 0.0 if you intend to do seeding regardless of share ratio.
     * If --seed-time option is specified along with this option, seeding ends when at least one
     * of the conditions is satisfied. Default: 1.0
     * */
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
    uri: String,
    fileName: String? = null,
    directory: String? = null,
    headers: Map<String, String> = emptyMap(),
    userAgent: String? = null,
    seed: Boolean = false,
): UriRequest {
    return UriRequest(
        uris = listOf(uri),
        fileName = fileName,
        headers = headers,
        directory = directory,
        userAgent = userAgent,
        seedTime = if (seed) null else 0.0f
    )
}

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