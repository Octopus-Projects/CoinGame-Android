package modac.coingame.ui.ingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_answer.*
import kotlinx.android.synthetic.main.activity_answer.adView
import kotlinx.android.synthetic.main.activity_result.*
import modac.coingame.R
import modac.coingame.data.App
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
        setView()
    }
    private fun setView(){
        if(App.prefs.king!!){
            tv_invite.visibility = View.VISIBLE
            tv_continue.visibility = View.VISIBLE
        }else{
            tv_invite.visibility = View.GONE
            tv_continue.visibility = View.GONE
        }
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
