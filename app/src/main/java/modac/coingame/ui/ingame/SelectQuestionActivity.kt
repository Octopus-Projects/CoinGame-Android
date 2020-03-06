package modac.coingame.ui.ingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_select_question.*
import kotlinx.android.synthetic.main.activity_select_question.adView
import kotlinx.android.synthetic.main.activity_select_question.img_info
import modac.coingame.R
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.dialog.RandomQuestionDialog
import modac.coingame.ui.intro.MainActivity.Companion.socket

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

    }
    private fun setListener(){
        ll_question_complete.setOnClickListener{
            socket.emit("questionOK")//질문 OK
            startActivity(Intent(this,AnswerActivity::class.java))
            finish()
        }
        ll_question_pass.setOnClickListener{
            socket.emit("questionPass")//TODO 질문 패스에 따른 처리...
        }
        img_info.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
        ll_random_question.setOnClickListener {
            RandomQuestionDialog(this,intent.getStringExtra("question")).show(supportFragmentManager,"tag")
        }
    }
}
