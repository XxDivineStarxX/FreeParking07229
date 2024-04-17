package com.example.freeparking07229.Activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.freeparking07229.Model.ParkingCard
import com.example.freeparking07229.Model.ParkingLot
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class userInfoActivity : AppCompatActivity() {

    inner class UserInfoViewModel: ViewModel(){
        var card_name = MutableLiveData<String>()
        var card_sex = MutableLiveData<String>()
        var card_phone = MutableLiveData<String>()
        var card_car = MutableLiveData<String>()
        var card_idcard = MutableLiveData<String>()
        var card_id = MutableLiveData<String>()
        var card_img = MutableLiveData<String>()
    }

    val mysqlHelper= MysqlHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val userInfoViewModel = UserInfoViewModel()
        val account = getSharedPreferences("data", MODE_PRIVATE).getString("account","111")

        CoroutineScope(Dispatchers.Main).launch {
            val parkingCard = withContext(Dispatchers.IO){
                if (account!=null)
                    mysqlHelper.getParkCardInfoByAccount(account)
                else{
                    Log.d("userInfoActivity","account为空")
                    ParkingCard()
                }

            }
            userInfoViewModel.card_name.value = parkingCard.name
            userInfoViewModel.card_sex.value = parkingCard.sex
            userInfoViewModel.card_phone.value = parkingCard.phone_number
            userInfoViewModel.card_idcard.value = parkingCard.id_number
            userInfoViewModel.card_car.value = parkingCard.car_number
            userInfoViewModel.card_id.value = parkingCard.parking_id
            userInfoViewModel.card_img.value = parkingCard.person_picture

        }

        userInfoViewModel.card_name.observe(this, Observer {
            name -> findViewById<TextView>(R.id.card_name) .text = name
        })
        userInfoViewModel.card_sex.observe(this, Observer {
                sex -> findViewById<TextView>(R.id.card_sex).text = sex
        })
        userInfoViewModel.card_phone.observe(this, Observer {
                phone -> findViewById<TextView>(R.id.card_phone).text = phone
        })
        userInfoViewModel.card_idcard.observe(this, Observer {
                idcard -> findViewById<TextView>(R.id.card_idcard).text = idcard
        })
        userInfoViewModel.card_car.observe(this, Observer {
                car -> findViewById<TextView>(R.id.card_carnumber).text = car
        })
        userInfoViewModel.card_id.observe(this, Observer {
                id -> findViewById<TextView>(R.id.card_id).text = id
        })
        userInfoViewModel.card_img.observe(this, Observer {
            img -> Glide.with(this).load(img).placeholder(R.mipmap.ic_launcher).into(findViewById<ImageView>(R.id.card_img))

        })


    }
}