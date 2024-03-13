package com.example.trackingapps.bind

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trackingapps.R
import com.example.trackingapps.model.Track

class TrackAdapter(private val trackList: ArrayList<Track>) :
    RecyclerView.Adapter<TrackAdapter.MyViewHolder>() {
    private var mListener: onItemClickListener? = null
    var onItemClick: ((Track) -> Unit)? = null

    interface onItemClickListener {
        fun onItemClick(view: View, track: Track)
    }


    fun setOnClickListener(listener: onItemClickListener) {
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_list_track,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = trackList[position]
        holder.kodeTrayek.text = currentitem.kodeTrayek.toString()
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentitem)
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val kodeTrayek: TextView = itemView.findViewById(R.id.tv_kode_trayek)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(trackList[adapterPosition])
            }
        }
    }

}