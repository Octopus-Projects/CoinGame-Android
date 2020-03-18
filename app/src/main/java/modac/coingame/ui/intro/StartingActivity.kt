package modac.coingame.ui.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_starting.*
import kotlinx.android.synthetic.main.activity_starting.adView
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.network.SocketApplication
import modac.coingame.ui.attend.CreateRoomActivity
import modac.coingame.ui.attend.FindRoomActivity
import modac.coingame.ui.dialog.InfoDialog
import org.json.JSONObject

class StartingActivity : AppCompatActivity() {

    companion object{
        lateinit var socket: Socket
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting)
        settingSocket()
        init()
    }



    private fun settingSocket(){
        socket = SocketApplication.get()
        socket.on(Socket.EVENT_CONNECT,onConnected)
        socket.on(Socket.EVENT_DISCONNECT,onDisconnected)
        socket.on("start",onStarted)
        socket.connect()
    }

    private val onStarted = Emitter.Listener {
        App.prefs.user_id = it[0].toString()
    }
    private val onDisconnected = Emitter.Listener {
        val r = Runnable {
            runOnUiThread{
                Log.d("socket disconnected","소켓 연결 disconnect 되었습니다")
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private val onConnected = Emitter.Listener {
        val r = Runnable {
            runOnUiThread{
                Log.d("socket success","소켓연결 성공했습니다")
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private fun init(){
        adView.loadAd(AdRequest.Builder().build())
        tv_findRoom.setOnClickListener {
            startActivity(Intent(this, FindRoomActivity::class.java))
        }
        tv_createRoom.setOnClickListener {
            startActivity(Intent(this, CreateRoomActivity::class.java))
        }
        img_nick_change.setOnClickListener {
            startActivity(Intent(this, ChangeNickActivity::class.java))
        }
        img_question.setOnClickListener {
            InfoDialog(this).show(supportFragmentManager,"tag")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        socket.off("ping")
        socket.off(Socket.EVENT_DISCONNECT)
        socket.off(Socket.EVENT_CONNECT)
    }
}
