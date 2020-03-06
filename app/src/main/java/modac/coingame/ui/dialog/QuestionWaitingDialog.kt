package modac.coingame.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.wang.avi.AVLoadingIndicatorView
import modac.coingame.R

class QuestionWaitingDialog(context: Context) : Dialog(context) {

    lateinit var avlProgress: AVLoadingIndicatorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_waiting_question)

        avlProgress = findViewById(R.id.aviWaitingDialog)
        avlProgress.show()
    }
}
