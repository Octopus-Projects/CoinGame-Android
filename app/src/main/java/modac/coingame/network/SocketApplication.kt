package modac.coingame.network

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketApplication {
    private val BASE_URL = "https://dd9d29dd.ngrok.io"
    private lateinit var socket : Socket
    //URI를 세팅하는 과정은 반드시 예외처리가 필요
    fun get(): Socket {
        try {
            socket = IO.socket(BASE_URL)
            Log.d("socket","소켓 연결 성공!!!!!!!!!!!!!!!")
        } catch (e: URISyntaxException) {
            Log.d("error","소켓 연결 에러 발생!!!!!!!!!!!!!!!")
            e.printStackTrace()
        }
        return socket
    }
}