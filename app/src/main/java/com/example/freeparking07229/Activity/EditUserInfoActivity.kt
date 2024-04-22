package com.example.freeparking07229.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.freeparking07229.Model.ParkingCard
import com.example.freeparking07229.Model.ParkingLot
import com.example.freeparking07229.Model.ParkingSpace
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditUserInfoActivity : AppCompatActivity() {

    inner class EditUserViewModel:ViewModel(){
        var user_name=MutableLiveData<String>()
        var user_sex=MutableLiveData<String>()
        var user_idnumber=MutableLiveData<String>()
        var user_carnumber=MutableLiveData<String>()
        var user_phonenumber=MutableLiveData<String>()
    }


    val mysqlHelper = MysqlHelper()
    val editUserViewModel = EditUserViewModel()
    var card_id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_info)
        val account = getSharedPreferences("data", MODE_PRIVATE).getString("account","null")
        val nameET = findViewById<EditText>(R.id.edit_user_name)
        val sexET = findViewById<EditText>(R.id.edit_user_sex)
        val idET = findViewById<EditText>(R.id.edit_user_idnumber)
        val carET = findViewById<EditText>(R.id.edit_user_carnumber)
        val phoneET = findViewById<EditText>(R.id.edit_user_phonenumber)

        try {
            CoroutineScope(Dispatchers.Main).launch {
                val parkingCard = withContext(Dispatchers.IO) {
                    if (account != null) {
                        mysqlHelper.getParkCardInfoByAccount(account)
                    } else
                        ParkingCard()
                }
                card_id = parkingCard.parking_id
                editUserViewModel.user_name.value = parkingCard.name
                editUserViewModel.user_sex.value = parkingCard.sex
                editUserViewModel.user_idnumber.value = parkingCard.id_number
                editUserViewModel.user_carnumber.value = parkingCard.car_number
                editUserViewModel.user_phonenumber.value = parkingCard.phone_number

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        editUserViewModel.user_name.observe(this, Observer { name ->
            nameET.setText(name)
        })
        editUserViewModel.user_sex.observe(this, Observer { sex ->
            sexET.setText(sex)
        })
        editUserViewModel.user_idnumber.observe(this, Observer { id ->
            idET.setText(id)
        })
        editUserViewModel.user_carnumber.observe(this, Observer { car ->
            carET.setText(car)
        })
        editUserViewModel.user_phonenumber.observe(this, Observer { phone ->
            phoneET.setText(phone)
        })


        findViewById<Button>(R.id.btn_edit_user_finish).setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("提示")
                setMessage("确认提交吗?")
                setCancelable(false)
                setPositiveButton("OK") { dialog, which ->
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            if(card_id=="")
                                card_id=(10000..99999).random().toString()
                            var _parkingCard = account?.let { it1 ->
                                ParkingCard(
                                    card_id,
                                    it1,
                                    nameET.text.toString(),
                                    sexET.text.toString(),
                                    idET.text.toString(),
                                    carET.text.toString(),
                                    "https://preview.qiantucdn.com/58pic/20220306/00258PICPZadbdwsah7Jd_PIC2018_PIC2018.jpg%21qt324_nowater_jpg",
                                    phoneET.text.toString()
                                )
                            }
                            Log.d("EditUserInfoActivity",_parkingCard.toString())
                            if (_parkingCard != null) {
                                mysqlHelper.updateParkingCard(_parkingCard)
                            }
                        }
                        setNegativeButton("Cancel") { dialog, which ->
                        }
                    }

                    getSharedPreferences("data", MODE_PRIVATE).edit().apply {
                        putBoolean("isValid",true)
                        putString("name",nameET.text.toString())
                        apply()
                    }

                    finish()
                }
                setNegativeButton("Cancel") { dialog, which ->
                }
                show()

            }

        }
    }

}