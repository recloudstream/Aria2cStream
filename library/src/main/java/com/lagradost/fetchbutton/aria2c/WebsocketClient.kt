package com.lagradost.fetchbutton.aria2c

import android.util.Log
import com.lagradost.fetchbutton.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class WebsocketClient(profile: Profile) : AbstractClient(profile) {
    companion object {
        const val TAG = "WebSocClient"
    }

    private var socket: WebSocket? = null

    override fun close() {
        closed = true
        try {
            socket?.close(0, "Terminated by user")
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            socket = null
        }
    }

    private val pendingMutex = Mutex()
    private val pendingJson: HashMap<String, String> =
        hashMapOf() // TODO CLEAN THIS UP WHEN NO PENDING REQUESTS

    override fun connect() {
        pending = true
        val url =
            "${if (profile.serverSsl) "wss" else "ws"}://${profile.serverAddr}:${profile.serverPort}${profile.serverEndpoint}"
        Log.i(TAG, "connect = $url")
        val request = Request.Builder().url(url).build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                closed = false
                pending = false
                Log.i(TAG, "onOpen")
                super.onOpen(webSocket, response)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                closed = true
                Log.i(TAG, "onClosed code = $code reason = $reason")
                super.onClosed(webSocket, code, reason)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.i(TAG, "onClosing code = $code reason = $reason")
                super.onClosing(webSocket, code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.i(TAG, "onFailure t = $t")
                super.onFailure(webSocket, t, response)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch {
                    tryParseJson<SmallJsonResponse>(text)?.let { jsonResponse ->
                        pendingMutex.withLock {
                            pendingJson[jsonResponse.id] = text
                        }
                    } ?: run {
                        Log.i(TAG, "onMessage non id text = $text")
                    } /*?: run {
                        tryParseJson<JsonMethodResponse>(text)?.let { jsonResponse ->
                            jsonResponse.params.firstOrNull { map -> map.containsKey("gid") }
                                ?.get("gid")?.let { gid ->
                                    getDownloadStatus(jsonResponse.method)?.let { status ->
                                        DownloadListener.setDownloadStatus(gid, status)
                                        //if () {
                                            //scope.launch {
                                            //    sendTellStatus(gid)
                                            //}
                                        //}
                                    }
                                }
                        }
                    }*/
                }
                super.onMessage(webSocket, text)
            }
        }

        socket = client.newWebSocket(request, listener)
    }

    override suspend fun send(id: String, req: JSONObject): Result<String> {
        try {
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "Send: $req")
            }

            if (closed || socket?.send(req.toString()) != true) {
                return Result.failure(Aria2SendError(SendError.Closed))
            }

            // poll 10 times a sec until timeout for the data
            for (i in 0 until profile.timeout * 10) {
                delay(100)
                pendingMutex.withLock {
                    pendingJson[id]?.let { response ->
                        pendingJson.remove(id)
                        return Result.success(response)
                    }
                }
            }

            return Result.failure(Aria2SendError(SendError.Timeout))
        } catch (e: Throwable) {
            return Result.failure(e)
        }
    }
}