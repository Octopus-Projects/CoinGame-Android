package modac.coingame.ui.attend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.spartons.qrcodegeneratorreader.models.UserObject
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_waiting_room.*
import kotlinx.android.synthetic.main.activity_waiting_room.img_out
import kotlinx.android.synthetic.main.activity_waiting_room.img_qr_code
import kotlinx.android.synthetic.main.activity_waiting_room.img_question
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.ui.attend.data.Attendees
import modac.coingame.ui.attend.qrcode.EncryptionHelper
import modac.coingame.ui.attend.qrcode.QRCodeHelper
import modac.coingame.ui.attend.recycler.AttenderAdapter
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.intro.MainActivity
import modac.coingame.util.VerticalItemDecorator
import org.json.JSONArray

class WaitingRoomActivity : AppCompatActivity() {
    val attendeesDatas = ArrayList<Attendees>()
    lateinit var attendeesAdapter : AttenderAdapter
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
        setListener()
        loadAttendees()
        joinRoom()

    }
    private val onUserReceived = Emitter.Listener {
        val receiveMessage = it[0] as JSONArray
        val r = Runnable {
            attendeesDatas.clear()
            for (i in 0 until receiveMessage.length()){
                val attendees = Gson().fromJson(receiveMessage[i] as JsonElement,Attendees::class.java)
                attendeesDatas.add(attendees)
            }
            runOnUiThread{
                attendeesAdapter.notifyDataSetChanged()
                Log.d("socket success","받은 데이터 : ${receiveMessage}")
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private fun joinRoom(){
        if (intent.getSerializableExtra(SCANNED_STRING) == null)
            throw RuntimeException("No encrypted String found in intent")
        val decryptedString = EncryptionHelper.getInstance().getDecryptionString(intent.getStringExtra(
            SCANNED_STRING
        ))
        val userObject = Gson().fromJson(decryptedString, UserObject::class.java)
        App.prefs.room_data = userObject.room_url
        MainActivity.socket.emit("joinRoom",App.prefs.room_data, App.prefs.user_nick)
        createQRCode()
    }
    private fun setListener(){
        MainActivity.socket.on("userList",onUserReceived)
        img_out.setOnClickListener { finish() }
        img_question.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
    }
    private fun createQRCode(){
        val user = App.prefs.room_data?.let { UserObject(room_url = it) }
        val serializeString = Gson().toJson(user)
        val encryptedString = EncryptionHelper.getInstance().encryptionString(serializeString).encryptMsg()
        setImageBitmap(encryptedString)
    }
    private fun setImageBitmap(encryptedString: String?) {
        val bitmap = QRCodeHelper
            .newInstance(this)
            .setContent(encryptedString)
            .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
            .setMargin(2)
            .qrcOde
        img_qr_code.setImageBitmap(bitmap)
    }
    private fun loadAttendees(){
        attendeesAdapter =
            AttenderAdapter(this)
        attendeesAdapter.datas = attendeesDatas
        rv_attendees.apply {
            addItemDecoration(VerticalItemDecorator(24))
            adapter = attendeesAdapter
            layoutManager = LinearLayoutManager(this@WaitingRoomActivity,
                LinearLayoutManager.VERTICAL,false)
        }
    }
    override fun onDestroy() {
        MainActivity.socket.emit("leaveRoom",App.prefs.room_data)
        super.onDestroy()
    }
}
