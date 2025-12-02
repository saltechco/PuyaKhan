package ir.saltech.puyakhan.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.net.WebSocketManager
import ir.saltech.puyakhan.data.net.WebSocketManager.jsonFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration

const val LOCAL_SERVER_NET_PORT = 8080

private const val LOCAL_SERVER_SERVICE_NOTIFY_CHANNEL = "OtpLanServerChannel"

class LocalServerService : Service() {

    private var serverEngine:
            EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        startServer()
    }

    private fun startServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                serverEngine = embeddedServer(Netty, port = LOCAL_SERVER_NET_PORT) {
                    install(ContentNegotiation) {
                        json(jsonFormatter)
                    }
                    install(WebSockets) {
                        pingPeriod = 15.toDuration(DurationUnit.SECONDS)
                        timeout = 15.toDuration(DurationUnit.SECONDS)
                        maxFrameSize = Long.MAX_VALUE
                        masking = false
                    }

                    routing {
                        webSocket("/otp") {
                            WebSocketManager.addSession(this)
                            try {
                                for (frame in incoming) {
                                    // TODO: Receiving message from client .. (temp empty)
                                    Log.i("TAG", "Received frame from client: $frame")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                WebSocketManager.removeSession(this)
                            }
                        }
                    }
                }
                serverEngine?.start(wait = true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serverEngine?.stop(1000, 2000)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundService() {
        val channelId = LOCAL_SERVER_SERVICE_NOTIFY_CHANNEL

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.lan_network_server_service_channel),
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification: Notification =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.lan_network_server_service_notify_title))
                .setContentText(
                    getString(
                        R.string.lan_network_server_service_notify_message,
                        LOCAL_SERVER_NET_PORT
                    )
                )
                .setSmallIcon(android.R.drawable.stat_notify_sync).build()

        startForeground(1, notification)
    }
}
