package modac.coingame.ui.ingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_answer.adView
import kotlinx.android.synthetic.main.activity_result.*
import modac.coingame.R
import modac.coingame.ui.dialog.InfoDialog

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        init()
    }
    private fun init(){
        adView.loadAd(AdRequest.Builder().build())
        setListener()
    }
    private fun setListener(){
        img_question.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
        img_out.setOnClickListener { finish() }
        tv_invite
        tv_continue.setOnClickListener {
            startActivity(Intent(this,SelectQuestionActivity::class.java))
            finish()
        }
    }
}
