package modac.coingame.ui.attend.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import modac.coingame.R
import modac.coingame.ui.attend.data.Attendees

class AttenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val tv_userNick = itemView.findViewById<TextView>(R.id.tv_userNick)
    val tv_number = itemView.findViewById<TextView>(R.id.tv_number)
    val img_king = itemView.findViewById<ImageView>(R.id.img_king)
    fun bind(attendees : Attendees){
        tv_userNick.text = attendees.userNick
        tv_number.text = attendees.userIndex.toString()
        if(tv_number.text.equals("1")) img_king.visibility = View.VISIBLE
        else img_king.visibility = View.GONE
    }
}