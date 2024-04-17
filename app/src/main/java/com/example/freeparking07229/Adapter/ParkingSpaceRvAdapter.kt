package com.example.freeparking07229.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freeparking07229.Activity.ParkingInfoActivity
import com.example.freeparking07229.Model.ParkingSpace
import com.example.freeparking07229.R

class ParkingSpaceRvAdapter(val context:Context, val parkingSpaceList:List<ParkingSpace>):
    RecyclerView.Adapter<ParkingSpaceRvAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var space_card = view.findViewById<androidx.cardview.widget.CardView>(R.id.space_card)
        var space_id = view.findViewById<TextView>(R.id.space_id)
        var space_state = view.findViewById<TextView>(R.id.space_state)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingSpaceRvAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.parking_space_item , parent ,false)
        val holder =  ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val parkinglot = parkingSpaceList[position]
            //点击预约该车位

        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parkingspace = parkingSpaceList[position]
        holder.space_id.text = parkingspace.space_id.toString()
        when(parkingspace.state){

            1-> {
                holder.space_card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.junkie_parking_orange_500) )
                holder.space_state.text = "已预约"
                holder.space_state.setTextColor(ContextCompat.getColor(context,R.color.white))
            }
            2-> {
                holder.space_card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.md_red_500))
                holder.space_state.text = "已占用"
                holder.space_state.setTextColor(ContextCompat.getColor(context,R.color.white))
            }
           else -> {
            holder.space_card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white))
            holder.space_state.text = "空闲"
        }
        }
    }


    override fun getItemCount() = parkingSpaceList.size

    }

