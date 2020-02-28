package modac.coingame.ui.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import modac.coingame.R
import modac.coingame.util.sendToast

class MainActivity : AppCompatActivity() {
    var isBtnEnable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }
    private fun init(){
        setListener()
        setBtnDisable()
    }
    private fun setListener(){
        et_nickname.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setBtnEnable()
                if(s.isNullOrBlank()){
                    setBtnDisable()
                }
            }
        })
        tv_start_btn.setOnClickListener{
            when(isBtnEnable){
                (true) -> {
                    startActivity(Intent(this, StartingActivity::class.java))
                    finish()
                }
                (false) -> sendToast("닉네임을 입력하세요")
            }

        }
    }
    private fun setBtnEnable(){
        isBtnEnable = true
        v_start.background = ContextCompat.getDrawable(applicationContext,
            R.drawable.bg_green_round
        )
        tv_start_btn.background = ContextCompat.getDrawable(applicationContext,
            R.drawable.bg_transparent_round
        )
        tv_start_btn.setTextColor(ContextCompat.getColor(this,
            R.color.colorBlack
        ))
    }
    private fun setBtnDisable(){
        isBtnEnable = false
        v_start.background = ContextCompat.getDrawable(applicationContext,
            R.drawable.bg_gray_round
        )
        tv_start_btn.background = ContextCompat.getDrawable(applicationContext,
            R.drawable.bg_transparent_gray_round
        )
        tv_start_btn.setTextColor(ContextCompat.getColor(this,
            R.color.colorGray
        ))
    }
}
