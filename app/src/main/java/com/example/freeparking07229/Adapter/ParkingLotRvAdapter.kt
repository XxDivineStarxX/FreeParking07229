package com.example.freeparking07229.Adapter

import android.content.*
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freeparking07229.Activity.ParkingInfoActivity
import com.example.freeparking07229.Activity.ParkingMainActivity
import com.example.freeparking07229.Model.ParkingLot
import com.example.freeparking07229.R

class ParkingLotRvAdapter (val context: Context, val parkingLotList:List<ParkingLot>):
    RecyclerView.Adapter<ParkingLotRvAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val parkingLotImage: ImageView = view.findViewById(R.id.ParkingLotRvListImage)
        val parkingLotName: TextView = view.findViewById(R.id.ParkingLotRvListNameText)
        val parkingLotLoc: TextView = view.findViewById(R.id.ParkingLotRvListLocText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.parking_lot_item , parent ,false)
        val holder =  ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val parkinglot = parkingLotList[position]
            val intent = Intent(context, ParkingInfoActivity::class.java).apply {
                putExtra(ParkingInfoActivity.PARKING_LOT_NAME,parkinglot.parking_name)
            }
            context.startActivity(intent)

        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parkinglot = parkingLotList[position]
        holder.parkingLotName.text = parkinglot.parking_name
        holder.parkingLotLoc.text = parkinglot.location
        Glide.with(context).load(parkinglot.parking_picture).placeholder(R.mipmap.ic_launcher).into(holder.parkingLotImage)
    }

    override fun getItemCount() = parkingLotList.size
}