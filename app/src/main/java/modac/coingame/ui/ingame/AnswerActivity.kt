package modac.coingame.ui.ingame

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_answer.*
import modac.coingame.R
import modac.coingame.ui.dialog.WaitingDialog

class AnswerActivity : AppCompatActivity() {

    lateinit var waitingDialog: WaitingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
        init()
    }
    private fun init(){
        tv_submit.isEnabled = false
        rb_yes_btn.setOnClickListener {
            submitEnable()
        }
        rb_no_btn.setOnClickListener {
            submitEnable()
        }
        tv_submit.setOnClickListener {
            //TODO 동전 제출 요청
            showDialog()
        }
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
            waitingDialog.setCancelable(true)
        }
        waitingDialog.show()
        val r = Runnable {
            Thread.sleep(3000)
            runOnUiThread {
                startActivity(Intent(applicationContext,MixActivity::class.java))
                finish()
            }
        }
        val thread = Thread(r)
        thread.start()
    }
}
