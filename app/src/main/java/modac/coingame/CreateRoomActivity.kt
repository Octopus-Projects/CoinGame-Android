package modac.coingame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.spartons.qrcodegeneratorreader.models.UserObject
import kotlinx.android.synthetic.main.activity_create_room.*
import modac.coingame.qrcode.EncryptionHelper
import modac.coingame.qrcode.QRCodeHelper

class CreateRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)
        init()
    }
    private fun init(){
        val user = UserObject(room_url = "여기 방 주소 들어갈 것.")
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
}
