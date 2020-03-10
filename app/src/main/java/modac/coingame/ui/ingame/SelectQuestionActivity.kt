package modac.coingame.ui.ingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_select_question.*
import kotlinx.android.synthetic.main.activity_select_question.adView
import kotlinx.android.synthetic.main.activity_select_question.img_info
import modac.coingame.R
import modac.coingame.network.SOCKET_QUESTION_OK
import modac.coingame.network.SOCKET_QUESTION_PASS
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.dialog.RandomQuestionDialog
import modac.coingame.ui.intro.MainActivity.Companion.socket
import org.json.JSONArray

class SelectQuestionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_question)
        init()
    }
    private fun init(){
        adView.loadAd(AdRequest.Builder().build())
        setListener()
        setSocket()
    }
    private fun setSocket(){
        socket.on(SOCKET_QUESTION_OK,onQuestionOKReceived)
    }
    private fun setListener(){
        ll_question_complete.setOnClickListener{
            socket.emit(SOCKET_QUESTION_OK)//질문 OK
        }
        ll_question_pass.setOnClickListener{
            socket.emit(SOCKET_QUESTION_PASS)//TODO 질문 패스에 따른 처리...
        }
        img_info.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
        ll_random_question.setOnClickListener {
            RandomQuestionDialog(this,intent.getStringExtra("question")).show(supportFragmentManager,"tag")
        }
    }
    private val onQuestionOKReceived = Emitter.Listener {
        val receiveMessage = it[0] as JSONArray
        val r = Runnable {
            runOnUiThread {
                startActivity(Intent(this,AnswerActivity::class.java))
                finish()
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
