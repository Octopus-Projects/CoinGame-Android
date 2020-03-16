package modac.coingame.ui.intro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_change_nick.*
import kotlinx.android.synthetic.main.activity_change_nick.et_nickname
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.util.sendToast

class ChangeNickActivity : AppCompatActivity() {
    var isBtnEnable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_nick)
        init()
    }
    private fun init(){
        et_nickname.setText(App.prefs.user_nick)
        setListener()
    }
    private fun setListener(){
        img_back.setOnClickListener {
            finish()
        }
        et_nickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setBtnEnable()
                if(s.isNullOrBlank()){
                    setBtnDisable()
                }
            }
        })
        tv_start_btn.setOnClickListener {
            App.prefs.user_nick = et_nickname.text.toString()
            sendToast("닉네임이 변경되었습니다.")
            finish()
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
        tv_start_btn.setTextColor(
            ContextCompat.getColor(this,
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
        tv_start_btn.setTextColor(
            ContextCompat.getColor(this,
                R.color.colorGray
            ))
    }
}
