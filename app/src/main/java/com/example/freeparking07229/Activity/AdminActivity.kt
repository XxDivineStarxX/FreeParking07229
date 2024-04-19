package com.example.freeparking07229.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.freeparking07229.Adapter.ParkingSpaceRvAdapter
import com.example.freeparking07229.Model.ParkingLot
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminActivity : AppCompatActivity() {

    inner class AdminViewModel :ViewModel(){
        var admin_name = MutableLiveData<String>()
    }

    val mysqlHelper = MysqlHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val adminViewModel = AdminViewModel()
        val admin_nameTV=findViewById<TextView>(R.id.admin_name)


        val account = getSharedPreferences("data", MODE_PRIVATE).getString("account","null")

        var parkingLot = ParkingLot()
        CoroutineScope(Dispatchers.Main).launch {
            findViewById<ProgressBar>(R.id.admin_progressbar).visibility= View.VISIBLE
            parkingLot = withContext(Dispatchers.IO){
                var name = account?.let { mysqlHelper.getNameInfoByAccount(it) }
                if (name != null) {
                    mysqlHelper.getParkLotInfoByAdmin(name)
                }else
                    ParkingLot()
            }
            adminViewModel.admin_name.value=parkingLot.admin
            findViewById<ProgressBar>(R.id.admin_progressbar).visibility= View.GONE
            Log.i("AdminActivity","应该的parkingLot"+parkingLot.toString())

        }
        adminViewModel.admin_name.observe(this, Observer  {admin->
            admin_nameTV.text=admin
        })
        findViewById<LinearLayout>(R.id.admin_parkinglot).setOnClickListener {
            val intent = Intent(this,ParkingInfoActivity::class.java).apply {
                putExtra(ParkingInfoActivity.PARKING_LOT_NAME ,parkingLot.parking_name)
            }
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.admin_space).setOnClickListener {
            val intent = Intent(this,SpaceActivity::class.java).apply {
                putExtra(SpaceActivity.PARKINGLOT_NAME,parkingLot.parking_name)
            }
            this.getSharedPreferences("data", MODE_PRIVATE).edit().apply {
                putInt("mode",ParkingSpaceRvAdapter.CHECK_MODE)
                apply()
            }
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.admin_reservation).setOnClickListener {
            val intent = Intent(this,SpaceActivity::class.java).apply {
                putExtra(SpaceActivity.PARKINGLOT_NAME,parkingLot.parking_name)
            }
            this.getSharedPreferences("data", MODE_PRIVATE).edit().apply {
                putInt("mode",ParkingSpaceRvAdapter.RESERVATION_MODE)
                apply()
            }
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.admin_parking).setOnClickListener {
            val intent = Intent(this,SpaceActivity::class.java).apply {
                putExtra(SpaceActivity.PARKINGLOT_NAME,parkingLot.parking_name)
            }
            this.getSharedPreferences("data", MODE_PRIVATE).edit().apply {
                putInt("mode",ParkingSpaceRvAdapter.INSERT_MODE)
                apply()
            }
            startActivity(intent)
        }


        findViewById<LinearLayout>(R.id.admin_getcar).setOnClickListener {
            val intent = Intent(this,SpaceActivity::class.java)
            this.getSharedPreferences("data", MODE_PRIVATE).edit().apply {
                putInt("mode",ParkingSpaceRvAdapter.INSERT_MODE)
                apply()
            }
        }

        findViewById<LinearLayout>(R.id.admin_update).setOnClickListener {
            val intent = Intent(this,UpdateParkingLotActivity::class.java)
            startActivity(intent)
        }

    }
}