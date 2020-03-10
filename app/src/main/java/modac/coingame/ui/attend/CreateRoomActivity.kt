package modac.coingame.ui.attend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.spartons.qrcodegeneratorreader.models.UserObject
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_create_room.*
import kotlinx.android.synthetic.main.activity_create_room.adView
import modac.coingame.ui.attend.recycler.AttenderAdapter
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.network.SOCKET_JOIN_ROOM
import modac.coingame.network.SOCKET_LEAVE_ROOM
import modac.coingame.network.SOCKET_START_GAME
import modac.coingame.network.SOCKET_USERLIST
import modac.coingame.ui.attend.data.Attendees
import modac.coingame.ui.attend.data.GameStateData
import modac.coingame.util.VerticalItemDecorator
import modac.coingame.ui.attend.qrcode.EncryptionHelper
import modac.coingame.ui.attend.qrcode.QRCodeHelper
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.ingame.AnswerActivity
import modac.coingame.ui.ingame.SelectQuestionActivity
import modac.coingame.ui.intro.MainActivity.Companion.socket
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList

class CreateRoomActivity : AppCompatActivity() {

    val attendeesDatas = ArrayList<Attendees>()
    lateinit var attendeesAdapter : AttenderAdapter
    companion object {
        private const val SCANNED_STRING: String = "scanned_string"
        fun getScannedActivity(callingClassContext: Context, encryptedString: String): Intent {
            return Intent(callingClassContext, CreateRoomActivity::class.java)
                .putExtra(SCANNED_STRING, encryptedString)
                .putExtra("isCreate",false)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)
        init()
    }
    private fun init(){
        socket.open()
        adView.loadAd(AdRequest.Builder().build())
        if(checkRoom()){//create : true, waiting : false
            Log.d("checkRoom","방을 만든 유저입니다")
            createQRCode()
            setBtnVisible()
        }else{
            Log.d("checkRoom","방에 참가하는 유저입니다")
            joinRoom()
            setBtnGone()
        }
        setListenner()
        setRecycler()
    }

    private fun joinRoom(){
        if (intent.getSerializableExtra(SCANNED_STRING) == null)
            throw RuntimeException("No encrypted String found in intent")
        val decryptedString = EncryptionHelper.getInstance().getDecryptionString(intent.getStringExtra(SCANNED_STRING))
        val userObject = Gson().fromJson(decryptedString, UserObject::class.java)
        App.prefs.room_data = userObject.room_url//방 데이터.
        socket.emit(SOCKET_JOIN_ROOM,App.prefs.room_data, App.prefs.user_nick)
        Log.d("socket","소켓 emit 요청 완료~~~~~~~~~~~")
        setQRCodeData()
    }

    private fun setListenner(){
        socket.on(SOCKET_USERLIST,onUserReceived)
        socket.on(SOCKET_START_GAME,onGameStartReceived)
        tv_gameStart.setOnClickListener {
            socket.emit(SOCKET_START_GAME,App.prefs.room_data)
            startActivity(Intent(this,SelectQuestionActivity::class.java))
        }
        img_out.setOnClickListener { finish() }
        img_question.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
    }

    private val onUserReceived = Emitter.Listener {
        attendeesDatas.clear()
        val receiveMessage = it[0] as JSONArray
        val r = Runnable {
            Log.d("socket","받은 데이터 : ${receiveMessage}")
            for (i in 0 until receiveMessage.length()){
                val jsonObj = receiveMessage[i]
                val inGameData = Gson().fromJson(jsonObj.toString(),Attendees::class.java)
                attendeesDatas.add(inGameData)
            }
            runOnUiThread{
                attendeesAdapter.notifyDataSetChanged()
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private val onGameStartReceived = Emitter.Listener {
        val receiveMessage = it[0] as JSONObject
        val r = Runnable {
            var myData : Attendees? = null
            val gameStateData = Gson().fromJson(receiveMessage.toString(), GameStateData::class.java)
            for (i in 0 until gameStateData.userList.size){
                val attendees = gameStateData.userList[i]
                if(attendees.userNickname.equals(App.prefs.user_nick)){
                    myData = attendees
                    break
                }
                //socket으로 게임 시작되었다는 것 받아옴.
                //자신과 일치하는 데이터 셋 추출 -> 자신이 질문자인지 판별하여 뷰 이동.
            }
            runOnUiThread{
                if(myData!=null){
                    App.prefs.king = myData.king
                    when(myData.queryUser){
                        (true) -> {
                            val intent = Intent(this,SelectQuestionActivity::class.java)
                            intent.putExtra("question",gameStateData.question)
                            startActivity(intent)
                        }
                        (false) -> {
                            val intent = Intent(this,AnswerActivity::class.java)
                            intent.putExtra("queryUser",false)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private fun checkRoom() : Boolean{
        val intent = intent
        return intent.getBooleanExtra("isCreate",true)
    }
    private fun setBtnGone(){
        tv_gameStart.visibility = View.GONE
        v_gameStart.visibility = View.GONE
    }
    private fun setBtnVisible(){
        tv_gameStart.visibility =View.VISIBLE
        v_gameStart.visibility = View.VISIBLE
    }
    override fun onDestroy() {
        socket.emit(SOCKET_LEAVE_ROOM,App.prefs.room_data)
        socket.off()
        Log.d("socket","방 나갑니다~~~~~~~~~~~")
        super.onDestroy()
    }
    private fun createQRCode(){//QRCode 데이터 생성.
        val randomNum : Double = Math.random()
        val randomRoomData = randomNum.toString()
        App.prefs.room_data = randomRoomData
        socket.emit(SOCKET_JOIN_ROOM,randomRoomData, App.prefs.user_nick)
        setQRCodeData()
    }
    private fun setQRCodeData(){
        val randomRoomComplexData = App.prefs.room_data
        val user = UserObject(room_url = randomRoomComplexData!!)
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
    private fun setRecycler(){
        attendeesAdapter = AttenderAdapter(this)
        attendeesAdapter.datas = attendeesDatas
        rv_attendees.apply {
            addItemDecoration(VerticalItemDecorator(24))
            adapter = attendeesAdapter
            layoutManager = LinearLayoutManager(this@CreateRoomActivity,LinearLayoutManager.VERTICAL,false)
        }
    }
}
