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
import com.example.freeparking07229.Adapter.ParkingSpaceRvAdapter
import com.example.freeparking07229.Model.ParkingSpace
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify

class SpaceActivity : AppCompatActivity() {

    companion object{
        val PARKINGLOT_NAME ="PARKING_LOT_NAME"
    }
    val mysqlHelper = MysqlHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space)

        val parkingSpaceRv1 = findViewById<RecyclerView>(R.id.space_recyclerView1)
        val parkingSpaceRv2 = findViewById<RecyclerView>(R.id.space_recyclerView2)

        val parkingName=intent.getStringExtra(PARKINGLOT_NAME)

        CoroutineScope(Dispatchers.Main).launch{
            val parkingSpaceList:ArrayList<ParkingSpace> = withContext(Dispatchers.IO){
                if(parkingName!=null){
                    Log.d("SpaceActivity","parkingName不为空")
                    mysqlHelper.getParkSpaceInfoByName(parkingName)

                }
                else{
                    Log.d("SpaceActivity","parkingName为空")
                    val list = ArrayList<ParkingSpace>()
                    list.add(ParkingSpace())
                    list
                }

            }
            if(parkingSpaceList.isNotEmpty()){
                val parkingSpaceList1 = parkingSpaceList.subList(0,5).toList()
                val parkingSpaceList2 = parkingSpaceList.subList(5,parkingSpaceList.size).toList()

                val layoutManager1 = LinearLayoutManager(this@SpaceActivity)
                val layoutManager2 = LinearLayoutManager(this@SpaceActivity)

                parkingSpaceRv1.layoutManager = layoutManager1
                parkingSpaceRv2.layoutManager = layoutManager2

                layoutManager1.orientation=LinearLayoutManager.HORIZONTAL
                layoutManager2.orientation=LinearLayoutManager.HORIZONTAL

                val adapter1 = ParkingSpaceRvAdapter(this@SpaceActivity, parkingSpaceList1)
                val adapter2 = ParkingSpaceRvAdapter(this@SpaceActivity, parkingSpaceList2)
                parkingSpaceRv1.adapter = adapter1
                parkingSpaceRv2.adapter = adapter2

            }else{

                Log.d("SpaceActivity","parkingSpaceList为空")
            }
        }
    }

    override fun onRestart() {
        super.onRestart()

        val parkingSpaceRv1 = findViewById<RecyclerView>(R.id.space_recyclerView1)
        val parkingSpaceRv2 = findViewById<RecyclerView>(R.id.space_recyclerView2)

        val parkingName=intent.getStringExtra(PARKINGLOT_NAME)

        CoroutineScope(Dispatchers.Main).launch{
            val parkingSpaceList:ArrayList<ParkingSpace> = withContext(Dispatchers.IO){
                if(parkingName!=null){
                    mysqlHelper.getParkSpaceInfoByName(parkingName)

                }
                else{
                    val list = ArrayList<ParkingSpace>()
                    list.add(ParkingSpace())
                    list
                }

            }
            if(parkingSpaceList.isNotEmpty()){
                val parkingSpaceList1 = parkingSpaceList.subList(0,5).toList()
                val parkingSpaceList2 = parkingSpaceList.subList(5,parkingSpaceList.size).toList()

                val layoutManager1 = LinearLayoutManager(this@SpaceActivity)
                val layoutManager2 = LinearLayoutManager(this@SpaceActivity)

                parkingSpaceRv1.layoutManager = layoutManager1
                parkingSpaceRv2.layoutManager = layoutManager2

                layoutManager1.orientation=LinearLayoutManager.HORIZONTAL
                layoutManager2.orientation=LinearLayoutManager.HORIZONTAL

                val adapter1 = ParkingSpaceRvAdapter(this@SpaceActivity, parkingSpaceList1)
                val adapter2 = ParkingSpaceRvAdapter(this@SpaceActivity, parkingSpaceList2)
                parkingSpaceRv1.adapter = adapter1
                parkingSpaceRv2.adapter = adapter2

            }else{

                Log.d("SpaceActivity","RESTART,parkingSpaceList为空")
            }
        }
    }
}