package modac.coingame.ui.ingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_answer.adView
import kotlinx.android.synthetic.main.activity_create_room.*
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.activity_result.img_out
import kotlinx.android.synthetic.main.activity_result.img_question
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.ui.attend.CreateRoomActivity.Companion.attendeesAdapter
import modac.coingame.ui.attend.CreateRoomActivity.Companion.attendeesDatas
import modac.coingame.ui.attend.data.Attendees
import modac.coingame.ui.attend.data.GameStateData
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.intro.StartingActivity
import modac.coingame.ui.intro.StartingActivity.Companion.socket
import modac.coingame.util.sendToast
import org.json.JSONArray
import org.json.JSONObject

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        socketOn()
        init()
    }
    private fun socketOn(){
        socketOff()
        socket.on("gameStatus",onGameStatusRecieved)
        socket.on("gameState",onGameStateReceived)
        socket.on("userList",onUserReceived)
        Log.d("socket","ResultActivity에서 소켓 켰습니다")
    }
    private fun socketOff(){
        socket.off("gameState")
        socket.off("gameStatus")
        socket.off("userList")
    }
    private val onUserReceived = Emitter.Listener {
        Log.d("socket","유저 리스트를 받았습니다 ")
        attendeesDatas.clear()
        val receiveMessage = it[0] as JSONArray
        val r = Runnable {
            Log.d("socket","받은 데이터 : ${receiveMessage}")
            for (i in 0 until receiveMessage.length()){
                val jsonObj = receiveMessage[i]
                val inGameData = Gson().fromJson(jsonObj.toString(),Attendees::class.java)
                attendeesDatas.add(inGameData)
            }
            runOnUiThread{
                sendToast("친구 한명이 나갔어!")
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private val onGameStateReceived = Emitter.Listener {
        val receiveMessage = it[0] as JSONObject
        val r = Runnable {
            Log.d("socket","ResultActivity에서 GameState를 받아왔습니다")
            var myData : Attendees? = null
            val gameStateData = Gson().fromJson(receiveMessage.toString(), GameStateData::class.java)
            for (i in 0 until gameStateData.userList.size){
                val attendees = gameStateData.userList[i]
                if(attendees.userNickname.equals(App.prefs.user_nick)){
                    myData = attendees
                    break
                }
            }
            runOnUiThread{
                if(myData!=null){
                    App.prefs.king = myData.king
                    when(myData.queryUser){
                        (true) -> {
                            val intent = Intent(this,SelectQuestionActivity::class.java)
                            intent.putExtra("question",gameStateData.question)
                            startActivity(intent)
                            socketOff()
                            finish()
                        }
                        (false) -> {
                            val intent = Intent(this,AnswerActivity::class.java)
                            intent.putExtra("queryUser",false)
                            startActivity(intent)
                            socketOff()
                            finish()
                        }
                    }
                }
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private val onGameStatusRecieved = Emitter.Listener {
        val r = Runnable {
            runOnUiThread {
                Log.d("socket","ResultActivity에서 GameStatus를 받아왔고 소켓 껐습니다")
                socketOff()
                finish()
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private fun init(){
        adView.loadAd(AdRequest.Builder().build())
        setListener()
        setView()
    }
    private fun setView(){
        tv_front_num.text = intent.getIntExtra("front",0).toString()
        tv_back_num.text = intent.getIntExtra("back",0).toString()
        if(App.prefs.king!!){
            v_submit.visibility = View.VISIBLE
            v_submit2.visibility = View.VISIBLE
            tv_invite.visibility = View.VISIBLE
            tv_continue.visibility = View.VISIBLE
        }else{
            v_submit.visibility = View.GONE
            v_submit2.visibility = View.GONE
            tv_invite.visibility = View.GONE
            tv_continue.visibility = View.GONE
        }
    }

    private fun setListener(){
        img_question.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
        img_out.setOnClickListener {
            val roomId = App.prefs.room_data
            if(App.prefs.king!!){//방장이 나간 경우
                socket.emit("gameStatus",roomId)
            }
            socketOff()
            socket.emit("leaveRoom",roomId)
            finishAffinity()
            startActivity(Intent(this,StartingActivity::class.java))
            finish()
        }
        tv_invite.setOnClickListener {
            val roomId = App.prefs.room_data
            socket.emit("gameStatus",roomId)
        }
        tv_continue.setOnClickListener {//OK
            val roomId = App.prefs.room_data
            socket.emit("continueGame",roomId)
        }
    }

    override fun onBackPressed() {}
}
