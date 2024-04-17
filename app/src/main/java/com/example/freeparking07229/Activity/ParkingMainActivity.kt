package com.example.freeparking07229.Activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freeparking07229.Adapter.ParkingLotRvAdapter
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParkingMainActivity : AppCompatActivity() {

    val mysqlHelper = MysqlHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_main)

        val parkingLotRv = findViewById<RecyclerView>(R.id.parkingLotRvList)
        CoroutineScope(Dispatchers.Main).launch{
            val parkingLotList = withContext(Dispatchers.IO){
                mysqlHelper.getParkingList()
            }
            if(parkingLotList.isNotEmpty()){

                val layoutManager = LinearLayoutManager(this@ParkingMainActivity)
                parkingLotRv.layoutManager = layoutManager
                val adapter = ParkingLotRvAdapter(this@ParkingMainActivity, parkingLotList)
                parkingLotRv.adapter = adapter
            }else{

                Log.d("ParkingMainActivity","parkingLotList为空")
            }
        }


    }



}