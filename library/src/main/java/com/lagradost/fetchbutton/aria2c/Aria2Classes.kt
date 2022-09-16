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

data class UriRequest(
    val uris: List<String>,
    val headers: Map<String, String> = emptyMap(),
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

data class JsonMethodResponse(
    @JsonProperty("method") val method: String,
    @JsonProperty("params") val params: ArrayList<Map<String, String>> = arrayListOf(),
)

data class JsonError(
    @JsonProperty("code") val code: Int,
    @JsonProperty("message") val message: String,
)