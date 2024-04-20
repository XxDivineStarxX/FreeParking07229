package com.example.freeparking07229.Activity

import android.content.EntityIterator
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.freeparking07229.Model.ParkingLot
import com.example.freeparking07229.Model.ParkingSpace
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParkingLotApplicationActivity : AppCompatActivity() {

    companion object{
        val PARKING_LOT_NAME = "0"
        val FORM_MODE = "MODE"
        val INSERT = 0
        val UPDATE = 1
    }
    inner class ParkingLotApplicationViewModel:ViewModel(){

        var longitude=MutableLiveData<Double>()
        var latitude=MutableLiveData<Double>()
        var location=MutableLiveData<String>()
        var parking_name=MutableLiveData<String>()
        var description=MutableLiveData<String>()
        var space_number=MutableLiveData<Int>()
        var admin=MutableLiveData<String>()

    }

    val mysqlHelper = MysqlHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_lot_application)

        val account=getSharedPreferences("data", MODE_PRIVATE).getString("account","null")

        val parkingName=intent.getStringExtra(PARKING_LOT_NAME)
        val application_form_mode=intent.getIntExtra(FORM_MODE,0)
        var oldName =""
        var oldSpaceNum = 0
        val parkingInfoViewModel = ParkingLotApplicationViewModel()

        val parkinglotNameEdit = findViewById<EditText>(R.id.application_parkinglot_name)
        val parkinglotLgtEdit = findViewById<EditText>(R.id.application_longitude)
        val parkinglotLatEdit = findViewById<EditText>(R.id.application_latitude)
        val parkinglotLocEdit = findViewById<EditText>(R.id.application_location)
        val parkinglotNumEdit = findViewById<EditText>(R.id.application_space_number)
        val parkinglotDecEdit = findViewById<EditText>(R.id.application_description)
        val parkinglotAdminEdit = findViewById<EditText>(R.id.application_admin)

        if(application_form_mode==UPDATE){
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    val parkingLot = withContext(Dispatchers.IO) {
                        if (parkingName != null) {
                            mysqlHelper.getParkLotInfoByName(parkingName)
                        } else {
                            ParkingLot()
                        }
                    }
                    oldName = parkingLot.parking_name
                    oldSpaceNum=parkingLot.space_number
                    parkingInfoViewModel.parking_name.value = parkingLot.parking_name
                    parkingInfoViewModel.longitude.value = parkingLot.longitude
                    parkingInfoViewModel.latitude.value = parkingLot.latitude
                    parkingInfoViewModel.location.value = parkingLot.location
                    parkingInfoViewModel.description.value = parkingLot.description
                    parkingInfoViewModel.space_number.value = parkingLot.space_number
                    parkingInfoViewModel.admin.value = parkingLot.admin
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            parkingInfoViewModel.parking_name.observe(this, Observer { name ->
                parkinglotNameEdit.setText(name)
            })
            parkingInfoViewModel.longitude.observe(this, Observer { longitude ->
                parkinglotLgtEdit.setText(longitude.toString())
            })
            parkingInfoViewModel.latitude.observe(this, Observer { latitude ->
                parkinglotLatEdit.setText(latitude.toString())
            })
            parkingInfoViewModel.location.observe(this, Observer { location ->
                parkinglotLocEdit.setText(location)
            })
            parkingInfoViewModel.description.observe(this, Observer { description ->
                parkinglotDecEdit.setText(description)
            })
            parkingInfoViewModel.space_number.observe(this, Observer { space_number ->
                parkinglotNumEdit.setText(space_number.toString())
            })
            parkingInfoViewModel.admin.observe(this, Observer { admin ->
                parkinglotAdminEdit.setText(admin.toString())
            })
        }

        findViewById<Button>(R.id.btn_application_finish).setOnClickListener {

            AlertDialog.Builder(this).apply {
                setTitle("提示")
                setMessage("确认提交吗?")
                setCancelable(false)
                setPositiveButton("OK") { dialog, which -> CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        var parkingLot= ParkingLot(parkinglotLgtEdit.text.toString().toDouble(),
                            parkinglotLatEdit.text.toString().toDouble(),
                            parkinglotLocEdit.text.toString(),
                            parkinglotNameEdit.text.toString(),
                            parkinglotDecEdit.text.toString(),
                            parkinglotNumEdit.text.toString().toInt(),
                            "https://x0.ifengimg.com/ucms/2022_02/106D526F5A419C1A6D2B4EDAE2903CF87C102049_size572_w1920_h1280.jpg",
                            parkinglotNumEdit.text.toString().toInt(),
                            parkinglotAdminEdit.text.toString())
                        //停车场更新
                        mysqlHelper.insertOrUpdateParkingLot(parkingLot,application_form_mode,oldName)
                        //停车位更新
                        if(application_form_mode==UPDATE){
                            for(i in oldSpaceNum until parkingLot.space_number){
                                val parkingSpace=ParkingSpace(parkingLot.parking_name,i+1,0)
                                mysqlHelper.insertParkingSpace(parkingSpace)
                            }
                        }else if (application_form_mode== INSERT){
                            for(i in 0 until parkingLot.space_number){
                                val parkingSpace=ParkingSpace(parkingLot.parking_name,i+1,0)
                                mysqlHelper.insertParkingSpace(parkingSpace)
                            }
                        }
                        //权限更新
                        if (account != null) {
                            mysqlHelper.updateUserIdentityToAdmin(account)
                            context?.getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)?.edit()
                                ?.apply {
                                    putInt("identity",1)
                                    apply()
                                }
                        }
                    }
                    finish()
                }
                }
                setNegativeButton("Cancel") { dialog, which ->
                }

                show()
            }


        }



    }
}