package modac.coingame.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.wang.avi.AVLoadingIndicatorView
import io.socket.emitter.Emitter
import modac.coingame.R
import modac.coingame.ui.intro.MainActivity.Companion.socket
import org.json.JSONArray
import org.json.JSONObject

class WaitingDialog(context: Context) : Dialog(context) {

    companion object{
        lateinit var tv_voteNum : TextView
    }
    lateinit var avlProgress: AVLoadingIndicatorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_waiting)
        avlProgress = findViewById(R.id.aviWaitingDialog)
        avlProgress.show()
        tv_voteNum = findViewById(R.id.tv_not_vote)
    }

}
