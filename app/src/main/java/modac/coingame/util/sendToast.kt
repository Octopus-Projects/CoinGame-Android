package modac.coingame.util

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import modac.coingame.R

fun Activity.sendToast(msg : String){
    val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = inflater.inflate(
        R.layout.custom_toast,this.findViewById(
            R.id.custom_toast_container
        ))
    Toast(this).apply {
        layout.findViewById<TextView>(R.id.tv_toast_message).text = msg
        setGravity(Gravity.BOTTOM,0,100)
        duration = Toast.LENGTH_SHORT
        view = layout
        show()
    }
}

fun Fragment.sendToast(msg : String){
    this.activity?.sendToast(msg)
}