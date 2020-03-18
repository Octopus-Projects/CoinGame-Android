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
import modac.coingame.ui.attend.data.Attendees
import modac.coingame.ui.attend.data.GameStateData
import modac.coingame.util.VerticalItemDecorator
import modac.coingame.ui.attend.qrcode.EncryptionHelper
import modac.coingame.ui.attend.qrcode.QRCodeHelper
import modac.coingame.ui.dialog.InfoDialog
import modac.coingame.ui.ingame.AnswerActivity
import modac.coingame.ui.ingame.SelectQuestionActivity
import modac.coingame.ui.intro.StartingActivity.Companion.socket
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList

class CreateRoomActivity : AppCompatActivity() {

    companion object {
        private const val SCANNED_STRING: String = "scanned_string"
        fun getScannedActivity(callingClassContext: Context, encryptedString: String): Intent {
            return Intent(callingClassContext, CreateRoomActivity::class.java)
                .putExtra(SCANNED_STRING, encryptedString)
                .putExtra("isCreate",false)
        }
        lateinit var attendeesAdapter : AttenderAdapter
        val attendeesDatas = ArrayList<Attendees>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)
        socketOn()
        init()
    }

    override fun onStop() {
        super.onStop()
        socket.off("userList")
    }

    override fun onRestart() {
        super.onRestart()
        socket.on("userList",onUserReceived)
        attendeesAdapter.notifyDataSetChanged()
        tv_attendeesNum.text = attendeesDatas.size.toString()
    }
    override fun onResume() {
        super.onResume()
        Log.d("socket","CreateRoomActivity에서 gameState 다시 켬.")
        socket.off("gameState")
        socket.on("gameState",onGameStateReceived)
    }
    private fun socketOn(){
        Log.d("socket","CreateRoomActivity에서 소켓 gameState, userList를 켬.")
        socket.on("userList",onUserReceived)
        socket.on("gameState",onGameStateReceived)
        socket.on("ping",onPingReceived)
    }

    private val onPingReceived = Emitter.Listener {}
    private fun init(){
        adView.loadAd(AdRequest.Builder().build())
        if(checkRoom()){//create : true, waiting : false
            createQRCode()
            setBtnVisible()
        }else{
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
        val roomData = userObject.room_url
        socket.emit("joinRoom",roomData, App.prefs.user_nick)
        Log.d("socket","소켓 emit 요청 완료~~~~~~~~~~~ 방 이름 : ${userObject.room_url}")
        setQRCodeData()
    }

    private fun setListenner(){
        tv_gameStart.setOnClickListener {
            socket.emit("startGame",App.prefs.room_data)
        }
        img_out.setOnClickListener { finish() }
        img_question.setOnClickListener { InfoDialog(this).show(supportFragmentManager,"tag") }
    }

    private val onUserReceived = Emitter.Listener {
        Log.d("socket","유저 리스트를 받았습니다 ")
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
                tv_attendeesNum.text = attendeesDatas.count().toString()
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    private val onGameStateReceived = Emitter.Listener {
        val receiveMessage = it[0] as JSONObject
        val r = Runnable {
            val gameStateData = Gson().fromJson(receiveMessage.toString(), GameStateData::class.java)
            val myData : Attendees? = gameStateData.userList.find { it.userID.equals(App.prefs.user_id) }
            socket.off("userList")
            socket.off("gameState")
            runOnUiThread{
                Log.d("socket","받아온 랜덤 질문 : ${gameStateData.question}")
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
        super.onDestroy()
        socket.off("userList")
        socket.off("gameState")
        Log.d("socket","gameState 껐습니다")
        socket.emit("leaveRoom", App.prefs.room_data)
        Log.d("socket","leaveRoom 쏨")
    }
    private fun createQRCode(){//QRCode 데이터 생성.
        val randomNum : Long = (Math.random()*99999999).toLong()+1
        val randomRoomData = randomNum.toString()
        App.prefs.room_data = randomRoomData
        socket.emit("joinRoom",randomRoomData, App.prefs.user_nick)
        Log.d("socket","소켓 emit 요청 완료~~~~~~~~~~~ 방 이름 : ${randomRoomData}")
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
        attendeesAdapter = AttenderAdapter(this,tv_gameStart,v_gameStart)
        attendeesAdapter.datas = attendeesDatas
        rv_attendees.apply {
            addItemDecoration(VerticalItemDecorator(24))
            adapter = attendeesAdapter
            layoutManager = LinearLayoutManager(this@CreateRoomActivity,LinearLayoutManager.VERTICAL,false)
        }
    }
}
