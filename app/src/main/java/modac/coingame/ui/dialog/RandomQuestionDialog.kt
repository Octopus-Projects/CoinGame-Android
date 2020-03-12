package modac.coingame.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import io.socket.client.Socket
import kotlinx.android.synthetic.main.fragment_dialog_question.*
import modac.coingame.R


class RandomQuestionDialog(private val activity : Activity,private val randomQuery : String?) : DialogFragment() {
    lateinit var socket : Socket
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_question, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        tv_randomQuery.text = randomQuery
        tv_confirm.setOnClickListener {
            socket.emit("SOCKET_QUESTION_OK")
            dismiss()
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(activity,R.drawable.bg_white_big_round))
        return dialog
    }
}