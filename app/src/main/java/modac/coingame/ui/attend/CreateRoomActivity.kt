package modac.coingame.ui.attend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.spartons.qrcodegeneratorreader.models.UserObject
import kotlinx.android.synthetic.main.activity_create_room.*
import modac.coingame.ui.attend.data.Attendees
import modac.coingame.ui.attend.recycler.AttenderAdapter
import modac.coingame.R
import modac.coingame.util.VerticalItemDecorator
import modac.coingame.ui.attend.qrcode.EncryptionHelper
import modac.coingame.ui.attend.qrcode.QRCodeHelper
import modac.coingame.ui.ingame.SelectQuestionActivity

class CreateRoomActivity : AppCompatActivity() {

    val attendeesDatas = ArrayList<Attendees>()
    lateinit var attendeesAdapter : AttenderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)
        init()
    }
    private fun init(){
        createQRCode()
        loadAttendees()
        setListenner()
    }
    private fun setListenner(){
        tv_submit.setOnClickListener { startActivity(Intent(this,SelectQuestionActivity::class.java)) }
    }
    private fun loadAttendees(){
        attendeesDatas.add(
            Attendees(1, "호주니")
        )
        attendeesDatas.add(
            Attendees(2, "코맵동후니")
        )
        attendeesDatas.add(
            Attendees(3, "수군쩜아요")
        )
        attendeesDatas.add(
            Attendees(4, "시니루")
        )
        attendeesDatas.add(
            Attendees(5, "다예하고싶은거다예")
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
        val user = UserObject(room_url = "여기 방 주소 들어갈 것.")
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
}
