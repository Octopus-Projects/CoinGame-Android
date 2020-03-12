package modac.coingame.ui.ingame

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_answer.*
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.dialog.QuestionWaitingDialog
import modac.coingame.ui.dialog.WaitingDialog
import modac.coingame.ui.ingame.data.VoteResult
import org.json.JSONArray
import org.json.JSONObject

class AnswerActivity : AppCompatActivity() {
    lateinit var socket : Socket
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
        setSocket()
    }
    private fun setSocket(){
        socket.on("gameState",onGameStateReceived)
        socket.on("questionOK",onQuestionOKReceived)
        socket.on("voteOK",onVoteOKReceived)
        socket.on("voteNum",onVoteExtraNum)
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
        val receiveMessage = it[0] as JSONObject
        val r = Runnable {
            runOnUiThread {
                if(waiteQuestionDialog.isShowing){
                    waiteQuestionDialog.dismiss()
                }

            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private val onGameStateReceived = Emitter.Listener {
        val receiveMessage = it[0] as JSONArray
        val r = Runnable {
            runOnUiThread {

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
        socket.on("voteNum",onVoteExtraNum)
    }
    private val onVoteExtraNum = Emitter.Listener {
        val receiveMessage = it[0] as JSONObject
        val r = Runnable {
            runOnUiThread {
                //숫자를 계속 받는다.
                //투표를 안한 사람도 숫자를 계속 받는다.
                //받은 숫자가 waitingDialog에 표시된다.
            }
        }
        val thread = Thread(r)
        thread.start()
    }

    override fun onDestroy() {
        socket.off()
        super.onDestroy()
    }
}
