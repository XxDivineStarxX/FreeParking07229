package com.example.freeparking07229.Adapter

import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.freeparking07229.Activity.CreateNewReservationActivity
import com.example.freeparking07229.Activity.CreateNewUsingSpaceActivity
import com.example.freeparking07229.Activity.ReservationActivity
import com.example.freeparking07229.Model.ParkingSpace
import com.example.freeparking07229.Model.UsingSpace
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParkingSpaceRvAdapter(val context:Context, val parkingSpaceList:List<ParkingSpace>):
    RecyclerView.Adapter<ParkingSpaceRvAdapter.ViewHolder>(){

        companion object{
            val RESERVATION_MODE = -1
            val CHECK_MODE = 0
            val INSERT_MODE = 1
            val DELETE_MODE = 2
        }
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var space_card = view.findViewById<androidx.cardview.widget.CardView>(R.id.space_card)
        var space_id = view.findViewById<TextView>(R.id.space_id)
        var space_state = view.findViewById<TextView>(R.id.space_state)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingSpaceRvAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.parking_space_item , parent ,false)
        val holder =  ViewHolder(view)
        val mysqlHelper = MysqlHelper()
        val identity = context.getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE).getInt("identity",0)
        val mode = context.getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE).getInt("mode",-1)

        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val space = parkingSpaceList[position]
            if(identity==0){

                val intent = Intent(context, ReservationActivity::class.java).apply {
                    putExtra(ReservationActivity.SPACE_SELECTED,space.space_id)
                    putExtra(ReservationActivity.PARKING_LOT_SELECTED,space.parking_lot)
                }
                context.startActivity(intent)
                //点击预约该车位

            }else if(identity==1){

                when(mode){

                    CHECK_MODE ->{
                        if(space.state==0){
                            AlertDialog.Builder(context).apply {
                                setTitle("车位信息")
                                setMessage("该车位为空闲")
                                setCancelable(false)
                                setPositiveButton("OK") { dialog, which ->
                                }

                                show()
                        }

                        }else {//车位被占用
                            var usingSpace = UsingSpace()
                            var info =""
                            var builder = StringBuilder()
                            CoroutineScope(Dispatchers.Main).launch {
                                withContext(Dispatchers.IO){
                                    usingSpace =  mysqlHelper.getUsingSpaceInfoByLotAndSpace(space.parking_lot,space.space_id)
                                }
                            AlertDialog.Builder(context).apply {
                                setTitle("车位信息")
                                    if(usingSpace.is_reserved ==1){
                                        builder.append("停车卡id:${usingSpace.parking_id}\n")
                                        builder.append("车牌号:${usingSpace.car_number}\n")
                                        builder.append("预约时间:${usingSpace.lock_time.toString()}")
                                    }else if(usingSpace.is_reserved ==0) {
                                        builder.append("停车卡id:${usingSpace.parking_id}\n")
                                        builder.append("车牌号:${usingSpace.car_number}\n")
                                        builder.append("上锁时间:${usingSpace.lock_time.toString()}\n")
                                        builder.append("停车时间时间:${usingSpace.time}\n")
                                        builder.append("计费:${usingSpace.cost.toString()}")
                                    }
                                    info = builder.toString()
                                    setMessage(info)
                                    setCancelable(false)
                                    setPositiveButton("OK") { dialog, which ->
                                    }

                                    show()
                                }


                            }

                        }
                    }

                    RESERVATION_MODE->{
                        if(space.state!=0){
                            AlertDialog.Builder(context).apply {
                                setTitle("提示")
                                setMessage("该车位已被占用或预约。")
                                setCancelable(false)
                                setPositiveButton("OK") { dialog, which ->
                                }
                                show()
                            }
                        }
                        else{
                            val intent = Intent(context, CreateNewReservationActivity::class.java).apply {
                                putExtra(CreateNewReservationActivity.SPACE_SELECTED,space.space_id)
                                putExtra(CreateNewReservationActivity.PARKING_LOT_SELECTED,space.parking_lot)
                            }
                            context.startActivity(intent)
                        }
                    }

                    INSERT_MODE ->{
                        if(space.state!=0){
                            AlertDialog.Builder(context).apply {
                                setTitle("提示")
                                setMessage("该车位已被占用或预约。")
                                setCancelable(false)
                                setPositiveButton("OK") { dialog, which ->
                                }
                                show()
                            }
                        }
                        else{
                            val intent = Intent(context, CreateNewUsingSpaceActivity::class.java).apply {
                                putExtra(CreateNewUsingSpaceActivity.SPACE_SELECTED,space.space_id)
                                putExtra(CreateNewUsingSpaceActivity.PARKING_LOT_SELECTED,space.parking_lot)
                            }
                            context.startActivity(intent)
                        }
                    }

                    DELETE_MODE ->{
                        if(space.state==0){
                            AlertDialog.Builder(context).apply {
                                setTitle("警告")
                                setMessage("该车位为空闲，无法操作")
                                setCancelable(false)
                                setPositiveButton("OK") { dialog, which ->
                                }
                                show()
                            }
                        }
                        else if(space.state==1){
                            AlertDialog.Builder(context).apply {
                                setTitle("提示")
                                setMessage("该车位已预约，暂无车停放而无法取车，是否取消预约？")
                                setCancelable(false)
                                setPositiveButton("OK") { dialog, which ->
                                    run {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            withContext(Dispatchers.IO) {
                                                val spaceAvail = mysqlHelper.getParkLotInfoByName(space.parking_lot).space_available
                                                mysqlHelper.deleteUsingSpaceByParkingLotAndSpace(
                                                    space.parking_lot,
                                                    space.space_id
                                                )
                                                mysqlHelper.updateParkingSpaceToZeroByParkingLotAndSpace(
                                                    space.parking_lot,
                                                    space.space_id,
                                                    spaceAvail)
                                            }
                                            holder.space_card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white))
                                            holder.space_state.text = "空闲"
                                            holder.space_state.setTextColor(ContextCompat.getColor(context,R.color.black))
                                        }

                                    }
                                }
                                show()
                            }
                        }
                        else if(space.state==2){
                            AlertDialog.Builder(context).apply {
                                setTitle("提示")
                                setMessage("是否确认取车？")
                                setCancelable(false)
                                setPositiveButton("OK") { dialog, which ->
                                    run {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            withContext(Dispatchers.IO) {
                                                val spaceAvail = mysqlHelper.getParkLotInfoByName(space.parking_lot).space_available
                                                mysqlHelper.deleteUsingSpaceByParkingLotAndSpace(
                                                    space.parking_lot,
                                                    space.space_id
                                                )
                                                mysqlHelper.updateParkingSpaceToZeroByParkingLotAndSpace(
                                                    space.parking_lot,
                                                    space.space_id,
                                                    spaceAvail
                                                )
                                            }
                                            holder.space_card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white))
                                            holder.space_state.text = "空闲"
                                            holder.space_state.setTextColor(ContextCompat.getColor(context,R.color.black))
                                        }

                                    }
                                }
                                show()
                            }
                        }
                    }



                    else ->{
                        val intent = Intent(context, ReservationActivity::class.java).apply {
                            putExtra(ReservationActivity.SPACE_SELECTED,space.space_id)
                            putExtra(ReservationActivity.PARKING_LOT_SELECTED,space.parking_lot)
                        }
                        context.startActivity(intent)
                        //点击预约该车位
                    }

                }
            }


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

