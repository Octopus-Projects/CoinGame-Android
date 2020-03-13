package modac.coingame.ui.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.MobileAds
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.network.SocketApplication
import modac.coingame.util.sendToast
import org.json.JSONObject

//main
//starting
//- create_room -> 게임시작하기 -> select_question -> 질문OK -> answer
//-> 동전제출시 대기 다이얼로그 -> mix -> result
//- find_room -> waiting_room
class MainActivity : AppCompatActivity() {

    var isBtnEnable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }
    private fun init(){
        MobileAds.initialize(this){}
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
                    val userNick = et_nickname.text.toString()
                    App.prefs.user_nick = userNick
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
