package modac.coingame.ui.attend.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import modac.coingame.R
import modac.coingame.data.App
import modac.coingame.ui.attend.data.Attendees
import org.w3c.dom.Text

class AttenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val tv_userNick = itemView.findViewById<TextView>(R.id.tv_userNick)
    val tv_number = itemView.findViewById<TextView>(R.id.tv_number)
    val img_king = itemView.findViewById<ImageView>(R.id.img_king)
    fun bind(attendees : Attendees,position: Int,tv_gameStart:TextView, v_gameStart : View){
        tv_userNick.text = attendees.userNickname
        tv_number.text = (position+1).toString()
        if(attendees.king) {
            img_king.visibility = View.VISIBLE
        }
        else {
            img_king.visibility = View.GONE
        }
        if(attendees.king&&App.prefs.user_id.equals(attendees.userID)){
            tv_gameStart.visibility = View.VISIBLE
            v_gameStart.visibility = View.VISIBLE
        }
    }
}