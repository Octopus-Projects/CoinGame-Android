package modac.coingame.ui.ingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_select_question.*
import modac.coingame.R
import modac.coingame.ui.dialog.ExitDialog
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.dialog.QRCodeDialog
import modac.coingame.ui.dialog.RandomQuestionDialog

class SelectQuestionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_question)
        init()
    }
    private fun init(){
        setListener()

    }
    private fun setListener(){
        ll_question_complete.setOnClickListener{
            startActivity(Intent(this,AnswerActivity::class.java))
            finish()
        }
        img_qr_code_explain.setOnClickListener { QRCodeDialog(this).show(supportFragmentManager,"tag") }
        img_info.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
        img_out.setOnClickListener { ExitDialog(this).show(supportFragmentManager,"tag") }
        ll_random_question.setOnClickListener { RandomQuestionDialog(this).show(supportFragmentManager,"tag") }
    }
}
