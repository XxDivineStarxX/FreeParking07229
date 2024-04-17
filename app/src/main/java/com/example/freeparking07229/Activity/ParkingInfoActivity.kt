package com.example.freeparking07229.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.freeparking07229.Model.ParkingLot
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParkingInfoActivity : AppCompatActivity() {

    companion object{
        var PARKING_LOT_NAME:String = "PARKING_LOT_NAME"
    }

    inner class ParkingInfoViewModel(parkingLot:ParkingLot):ViewModel(){

        var parking_lot_name = MutableLiveData<String>()
        var longitude = MutableLiveData<Double>()
        var latitude = MutableLiveData<Double>()
        var location = MutableLiveData<String>()
        var description = MutableLiveData<String>()
        var space_number = MutableLiveData<Int>()
        var space_available = MutableLiveData<Int>()
        var parking_picture = MutableLiveData<String>()

        init{
            parking_lot_name.value=parkingLot.parking_name
            longitude.value=parkingLot.longitude
            latitude.value=parkingLot.latitude
            location.value=parkingLot.location
            description.value=parkingLot.description
            space_number.value=parkingLot.space_number
            space_available.value=parkingLot.space_available
            parking_picture.value=parkingLot.parking_picture
        }

    }

    val mysqlHelper = MysqlHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_info)

        val parkingName=intent.getStringExtra(PARKING_LOT_NAME)
        val parkingInfoViewModel = ParkingInfoViewModel(ParkingLot())

        val parkingImgV = findViewById<ImageView>(R.id.parking_image_view)
        val nameTxtV = findViewById<TextView>(R.id.parking_lot_name)
        val longTxtV = findViewById<TextView>(R.id.longitude_text)
        val latTxtV = findViewById<TextView>(R.id.latitude_text)
        val locTxtV = findViewById<TextView>(R.id.location_text)
        val descTxtV = findViewById<TextView>(R.id.parking_description)
        val spaceNumTxtV = findViewById<TextView>(R.id.space_text)
        val spaceAvailTxtV = findViewById<TextView>(R.id.space_available_text)

        val btn_fav = findViewById<Button>(R.id.btn_favorite)
        val btn_res = findViewById<Button>(R.id.btn_reserve)
        val btn_pay = findViewById<Button>(R.id.pay_parkingTicket)

        try {
            CoroutineScope(Dispatchers.Main).launch {
                val parkingLot= withContext(Dispatchers.IO) {
                    if (parkingName != null) {
                       mysqlHelper.getParkLotInfoByName(parkingName)
                    }else{
                        ParkingLot()
                    }
                }

                parkingInfoViewModel.parking_lot_name.value=parkingLot.parking_name
                parkingInfoViewModel.longitude.value = parkingLot.longitude
                parkingInfoViewModel.latitude.value = parkingLot.latitude
                parkingInfoViewModel.location.value = parkingLot.location
                parkingInfoViewModel.description.value = parkingLot.description
                parkingInfoViewModel.space_number.value = parkingLot.space_number
                parkingInfoViewModel.space_available.value = parkingLot.space_available
                parkingInfoViewModel.parking_picture.value = parkingLot.parking_picture
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

        parkingInfoViewModel.parking_lot_name.observe(this, Observer {name->
            nameTxtV.text=name.toString()
        })
        parkingInfoViewModel.longitude.observe(this, Observer {longitude->
            longTxtV.text=longitude.toString()
        })
        parkingInfoViewModel.latitude.observe(this, Observer {latitude->
            latTxtV.text=latitude.toString()
        })
        parkingInfoViewModel.location.observe(this, Observer {location->
            locTxtV.text=location
        })
        parkingInfoViewModel.description.observe(this, Observer {description->
            descTxtV.text=description
        })
        parkingInfoViewModel.space_number.observe(this, Observer {space_number->
            spaceNumTxtV.text=space_number.toString()
        })
        parkingInfoViewModel.space_available.observe(this, Observer {space_available->
            spaceAvailTxtV.text=space_available.toString()
        })
        parkingInfoViewModel.parking_picture.observe(this, Observer {parking_picture->
            Glide.with(this).load(parking_picture).into(parkingImgV)
        })

        btn_fav.setOnClickListener {
            Toast.makeText(this,"收藏该停车场",Toast.LENGTH_LONG).show()
        }
        btn_res.setOnClickListener {
           val intent =  Intent(this,SpaceActivity::class.java).apply {
               putExtra(SpaceActivity.PARKINGLOT_NAME,parkingName)
           }
            startActivity(intent)
        }
        btn_pay.setOnClickListener {
            Toast.makeText(this,"选择支付",Toast.LENGTH_LONG).show()
        }
    }


}