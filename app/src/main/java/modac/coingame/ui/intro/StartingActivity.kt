package modac.coingame.ui.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_starting.*
import modac.coingame.R
import modac.coingame.ui.attend.CreateRoomActivity
import modac.coingame.ui.attend.FindRoomActivity

class StartingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting)
        init()
    }
    private fun init(){
        tv_findRoom.setOnClickListener {
            startActivity(Intent(this, FindRoomActivity::class.java))
        }
        tv_createRoom.setOnClickListener {
            startActivity(Intent(this, CreateRoomActivity::class.java))
        }
    }
}
