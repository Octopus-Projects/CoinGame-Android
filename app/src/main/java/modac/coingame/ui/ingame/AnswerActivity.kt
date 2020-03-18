package modac.coingame.ui.ingame

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_answer.*
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.ui.attend.data.Attendees
import modac.coingame.ui.attend.data.GameStateData
import modac.coingame.ui.ingame.data.VoteNum
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.dialog.QuestionWaitingDialog
import modac.coingame.ui.dialog.WaitingDialog
import modac.coingame.ui.dialog.WaitingDialog.Companion.tv_voteNum
import modac.coingame.ui.ingame.data.VoteResult
import modac.coingame.ui.intro.StartingActivity.Companion.socket
import modac.coingame.util.sendToast
import org.json.JSONObject

class AnswerActivity : AppCompatActivity() {
    lateinit var waitingDialog: WaitingDialog
    lateinit var waiteQuestionDialog : QuestionWaitingDialog
    var isFront : Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
        init()
    }
    private fun init(){
        checkIntent()
        adView.loadAd(AdRequest.Builder().build())
        setListener()
        tv_submit.isEnabled = false
    }
    private fun setSocket(){
        Log.d("socket","gameState켰습니다.")
        socket.apply {
            on("gameState",onGameStateReceived)
            on("questionOK",onQuestionOKReceived)
            on("voteOK",onVoteOKReceived)
            on("voteNum",onVoteExtraNum)
        }
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
        socket.apply {
            off("gameState")
            off("questionOK")
            off("voteOK")
            off("voteNum")
        }
    }
    private fun checkIntent(){
        val intent = intent
        if(intent.getBooleanExtra("queryUser",true)==false){
            if(!::waiteQuestionDialog.isInitialized){
                waiteQuestionDialog = QuestionWaitingDialog(this)
                waiteQuestionDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                waiteQuestionDialog.setCancelable(false)
            }
            waiteQuestionDialog.show()
        }
    }
    private fun setListener(){
        rb_yes_btn.setOnClickListener {
            submitEnable()
            isFront = true
        }
        rb_no_btn.setOnClickListener {
            submitEnable()
            isFront = false
        }
        img_info.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
        tv_submit.setOnClickListener {
            if(isFront!=null){
                socket.emit("vote", App.prefs.room_data,isFront)
                showDialog()
            }
        }
    }
    private val onQuestionOKReceived = Emitter.Listener {
        val r = Runnable {
            runOnUiThread {
                if(waiteQuestionDialog.isShowing){
                    waiteQuestionDialog.dismiss()
                }
//                sendToast("")
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private val onGameStateReceived = Emitter.Listener {
        val receiveMessage = it[0] as JSONObject
        val r = Runnable {
            val gameStateData = Gson().fromJson(receiveMessage.toString(), GameStateData::class.java)
            val myData : Attendees? = gameStateData.userList.find { it.userNickname.equals(App.prefs.user_nick) }
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
                    }
                }
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private val onVoteOKReceived = Emitter.Listener {
        val receiveMessage = it[0] as JSONObject
        val r = Runnable {
            runOnUiThread {
                if(waitingDialog.isShowing){
                    waitingDialog.dismiss()
                }
                val voteResult = Gson().fromJson(receiveMessage.toString(),VoteResult::class.java)
                val intent = Intent(this,MixActivity::class.java)
                Log.d("socket","앞면 : ${voteResult.front} 뒷면 : ${voteResult.back}")
                intent.putExtra("front",voteResult.front)
                intent.putExtra("back",voteResult.back)
                startActivity(intent)
                finish()
            }
        }
        val thread = Thread(r)
        thread.start()
    }

    private fun submitEnable(){
        tv_submit.isEnabled = true
        v_submit.background = ContextCompat.getDrawable(applicationContext,
            R.drawable.bg_green_round
        )
        tv_submit.background = ContextCompat.getDrawable(applicationContext,
            R.drawable.bg_transparent_round
        )
        tv_submit.setTextColor(
            ContextCompat.getColor(this,
                R.color.colorBlack
            ))
    }
    private fun showDialog(){
        if(!::waitingDialog.isInitialized){
            waitingDialog = WaitingDialog(this)
            waitingDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            waitingDialog.setCancelable(false)
        }
        waitingDialog.show()
    }
    private val onVoteExtraNum = Emitter.Listener {
        val receiveMessage = it[0] as JSONObject
        val r = Runnable {
            runOnUiThread {
                Log.d("socket","받은 객체 : ${receiveMessage}")
                if(::waitingDialog.isInitialized&&waitingDialog.isShowing){
                    val extraNum = Gson().fromJson(receiveMessage.toString(),
                        VoteNum::class.java)
                    tv_voteNum.text = extraNum.voteNum.toString()
                    Log.d("socket","받아온 투표 안한 사람 수 : ${extraNum.voteNum}")
                }
            }
        }
        val thread = Thread(r)
        thread.start()
    }

    override fun onBackPressed() {
    }
}
