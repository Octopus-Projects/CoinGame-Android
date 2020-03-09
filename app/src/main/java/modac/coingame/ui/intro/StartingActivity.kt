package modac.coingame.ui.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_starting.*
import kotlinx.android.synthetic.main.activity_starting.adView
import modac.coingame.R
import modac.coingame.ui.attend.CreateRoomActivity
import modac.coingame.ui.attend.FindRoomActivity
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.intro.MainActivity.Companion.socket
import java.io.IOException

class StartingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting)
        init()
    }
    private fun init(){
        adView.loadAd(AdRequest.Builder().build())
        tv_findRoom.setOnClickListener {
            startActivity(Intent(this, FindRoomActivity::class.java))
        }
        tv_createRoom.setOnClickListener {
            startActivity(Intent(this, CreateRoomActivity::class.java))
        }
        img_nick_change.setOnClickListener {
            startActivity(Intent(this, ChangeNickActivity::class.java))
        }
        img_question.setOnClickListener {
            InfoDialog(this).show(supportFragmentManager,"tag")
        }
    }

    override fun onStop() {
        super.onStop()
        try{
            socket.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        socket.off()
        socket.disconnect()
        socket.close()
        super.onDestroy()
    }
}
