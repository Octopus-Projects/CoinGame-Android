package modac.coingame.ui.attend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.spartons.qrcodegeneratorreader.models.UserObject
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_create_room.*
import kotlinx.android.synthetic.main.activity_create_room.adView
import kotlinx.android.synthetic.main.activity_create_room.tv_submit
import modac.coingame.ui.attend.data.Attendees
import modac.coingame.ui.attend.recycler.AttenderAdapter
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.network.SocketApplication
import modac.coingame.util.VerticalItemDecorator
import modac.coingame.ui.attend.qrcode.EncryptionHelper
import modac.coingame.ui.attend.qrcode.QRCodeHelper
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.ingame.SelectQuestionActivity
import modac.coingame.ui.intro.MainActivity.Companion.socket
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class CreateRoomActivity : AppCompatActivity() {

    val attendeesDatas = ArrayList<Attendees>()
    lateinit var attendeesAdapter : AttenderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)
        init()
    }
    private fun init(){
        adView.loadAd(AdRequest.Builder().build())
        createQRCode()
        loadAttendees()
        setListenner()
    }

//        socket.emit("joinRoom",1,"호준")
//        socket.on("joinRoom",onMessageReceived)
        //"joinRoom",RoomName,UserNickName ->
    private val onIdReceived = Emitter.Listener {
        val receiveMessage = it[0] as JSONArray
        val r = Runnable {
            //JSONArray => JSONObject 하나씩
            for (i in 0 until receiveMessage.length()){

            }
            runOnUiThread{
                Log.d("socket success","받은 데이터 : ${receiveMessage}")
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private fun setListenner(){
        socket.on("joinRoom",onIdReceived)
        socket.on("loadAttendees",onIdReceived)//TODO 소켓 key값 확인 및 이벤트 처리.(방 유저 리스트 갱신)
        tv_submit.setOnClickListener { startActivity(Intent(this,SelectQuestionActivity::class.java)) }
        img_out.setOnClickListener { finish() }
        img_question.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
    }
    private fun loadAttendees(){
        attendeesDatas.add(
            Attendees("I816IobyaeCjGSOTAAAI", "호주니",true)
        )
        attendeesDatas.add(
            Attendees("I816IobyaeCjGSOTAAAI", "코맵동후니",false)
        )
        attendeesDatas.add(
            Attendees("I816IobyaeCjGSOTAAAI", "수군쩜아요",false)
        )
        attendeesDatas.add(
            Attendees("I816IobyaeCjGSOTAAAI", "시니루",false)
        )
        attendeesDatas.add(
            Attendees("I816IobyaeCjGSOTAAAI", "다예하고싶은거다예",false)
        )
        attendeesAdapter =
            AttenderAdapter(this)
        attendeesAdapter.datas = attendeesDatas
        rv_attendees.apply {
            addItemDecoration(VerticalItemDecorator(24))
            adapter = attendeesAdapter
            layoutManager = LinearLayoutManager(this@CreateRoomActivity,LinearLayoutManager.VERTICAL,false)
        }
    }
    private fun createQRCode(){
        val randomNum : Double = Math.random()
        val randomRoomData = randomNum.toString()
        App.prefs.room_data = randomRoomData
        socket.emit("joinRoom",randomRoomData, App.prefs.user_nick)
        val user = UserObject(room_url = randomRoomData)
        val serializeString = Gson().toJson(user)
        val encryptedString = EncryptionHelper.getInstance().encryptionString(serializeString).encryptMsg()
        setImageBitmap(encryptedString)
    }
    private fun updateAttendeesList(){

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

    override fun onDestroy() {
        socket.emit("leaveRoom",App.prefs.room_data,App.prefs.user_id)
        super.onDestroy()
    }
}
