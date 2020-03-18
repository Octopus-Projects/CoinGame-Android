package modac.coingame.ui.ingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_select_question.*
import kotlinx.android.synthetic.main.activity_select_question.adView
import kotlinx.android.synthetic.main.activity_select_question.img_info
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.ui.attend.data.Attendees
import modac.coingame.ui.attend.data.GameStateData
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.dialog.RandomQuestionDialog
import modac.coingame.ui.intro.StartingActivity.Companion.socket
import org.json.JSONObject

class SelectQuestionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_question)
        init()
    }
    private fun init(){
        adView.loadAd(AdRequest.Builder().build())
        setListener()
    }

    override fun onResume() {
        super.onResume()
        setSocket()
    }

    override fun onPause() {
        super.onPause()
        offSocket()
    }
    private fun offSocket(){
        socket.off("questionOK")
        socket.off("gameState")
        Log.d("socket","SelectQuestionActivity에서 소켓 껐습니다")
    }
    private fun onSocket(){
        socket.on("questionOK",onQuestionOKReceived)
        socket.on("gameState",onGameStateReceived)
        Log.d("socket","SelectQuestionActivity에서 소켓 켰습니다")
    }
    private fun setSocket(){
        offSocket()
        onSocket()
    }
    private fun setListener(){
        ll_question_complete.setOnClickListener{
            val roomData = App.prefs.room_data
            socket.emit("questionOK", roomData)//질문 OK
            Log.d("socket","questionOK 쐈습니다 방 데이터:${App.prefs.room_data}")
        }
        ll_question_pass.setOnClickListener{
            val roomData = App.prefs.room_data
            socket.emit("questionPass",roomData)
            Log.d("socket","questionPass 쐈습니다 방 데이터:${App.prefs.room_data}")
        }
        img_info.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
        ll_random_question.setOnClickListener {
            RandomQuestionDialog(this,intent.getStringExtra("question")).show(supportFragmentManager,"tag")
        }
    }

    private val onQuestionOKReceived = Emitter.Listener {
        val r = Runnable {
            runOnUiThread {
                startActivity(Intent(this,AnswerActivity::class.java))
                finish()
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private val onGameStateReceived = Emitter.Listener {
        Log.d("socket","SelectQuestionActivity에서 GameState 받았습니다")
        val receiveMessage = it[0] as JSONObject
        val r = Runnable {
            val gameStateData = Gson().fromJson(receiveMessage.toString(), GameStateData::class.java)
            val myData : Attendees? = gameStateData.userList.find { it.userID.equals(App.prefs.user_id) }
            runOnUiThread{
                if(myData!=null){
                    App.prefs.king = myData.king
                    when(myData.queryUser){
                        (true) -> {
                            val intent = Intent(this,SelectQuestionActivity::class.java)
                            intent.putExtra("question",gameStateData.question)
                            startActivity(intent)
                            finish()
                        }
                        (false) -> {
                            val intent = Intent(this,AnswerActivity::class.java)
                            intent.putExtra("queryUser",false)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
        val thread = Thread(r)
        thread.start()
    }

    override fun onBackPressed() {}
}
