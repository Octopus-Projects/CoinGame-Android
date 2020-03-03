package modac.coingame.ui.attend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.spartons.qrcodegeneratorreader.models.UserObject
import kotlinx.android.synthetic.main.activity_waiting_room.*
import modac.coingame.R
import modac.coingame.ui.attend.qrcode.EncryptionHelper

class WaitingRoomActivity : AppCompatActivity() {
    companion object {
        private const val SCANNED_STRING: String = "scanned_string"
        fun getScannedActivity(callingClassContext: Context, encryptedString: String): Intent {
            return Intent(callingClassContext, WaitingRoomActivity::class.java)
                .putExtra(SCANNED_STRING, encryptedString)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)
        init()
    }
    private fun init(){
        if (intent.getSerializableExtra(SCANNED_STRING) == null)
            throw RuntimeException("No encrypted String found in intent")
        val decryptedString = EncryptionHelper.getInstance().getDecryptionString(intent.getStringExtra(
            SCANNED_STRING
        ))
        val userObject = Gson().fromJson(decryptedString, UserObject::class.java)
        tv_test.text = userObject.room_url
    }
}
