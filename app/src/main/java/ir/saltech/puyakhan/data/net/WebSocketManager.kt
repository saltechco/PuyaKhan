package ir.saltech.puyakhan.data.net

import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.model.OtpPayload
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import java.util.Collections

object WebSocketManager {
    private val activeSessions =
        Collections.synchronizedSet(LinkedHashSet<DefaultWebSocketSession>())

    val jsonFormatter = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun addSession(session: DefaultWebSocketSession) {
        activeSessions.add(session)
    }

    fun removeSession(session: DefaultWebSocketSession) {
        activeSessions.remove(session)
    }

    suspend fun broadcastOtpCode(otpCode: OtpCode) {
        val payload = OtpPayload(
            otp = otpCode.otp,
            amount = otpCode.price,
            bank = otpCode.bank,
            sentTime = otpCode.sms.sentTime,
            expirationTime = otpCode.expirationTime,
            elapsedTime = otpCode.elapsedTime
        )

        val jsonMessage = jsonFormatter.encodeToString(payload)

        val iterator = activeSessions.iterator()
        while (iterator.hasNext()) {
            val session = iterator.next()
            if (session.isActive) {
                try {
                    session.send(Frame.Text(jsonMessage))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                iterator.remove()
            }
        }
    }
}
