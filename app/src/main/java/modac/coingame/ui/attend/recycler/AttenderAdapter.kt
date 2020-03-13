package modac.coingame.ui.attend.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import modac.coingame.R
import modac.coingame.ui.attend.data.Attendees

class AttenderAdapter(private val context : Context,private val tv_gameStart : TextView,private val v_gameStart : View) : RecyclerView.Adapter<AttenderViewHolder>(){

    var datas = ArrayList<Attendees>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttenderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_room_person,parent,false)
        return AttenderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: AttenderViewHolder, position: Int) {
        holder.bind(datas[position],position,tv_gameStart,v_gameStart)
    }

}