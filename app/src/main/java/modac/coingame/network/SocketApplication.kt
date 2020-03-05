package modac.coingame.network

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketApplication {
    private val BASE_URL = "https://82d6330c.ngrok.io"
    private lateinit var socket : Socket
    //URI를 세팅하는 과정은 반드시 예외처리가 필요
    fun get(): Socket {
        try {
            socket = IO.socket(BASE_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        return socket
    }
}