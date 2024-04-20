package com.example.freeparking07229.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freeparking07229.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)

        findViewById<ImageButton>(R.id.F1).setOnClickListener {
            Log.d("HomeFragment","检测到点击")
            val intent = Intent(this, userInfoActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.F2).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }


        findViewById<ImageButton>(R.id.S1).setOnClickListener {
            val intent = Intent(this, ParkingMainActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.S2).setOnClickListener {
            val intent = Intent(this, ParkingInfoActivity::class.java).apply {
                putExtra(ParkingInfoActivity.PARKING_LOT_NAME,"北京停车场")
            }
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.S3).setOnClickListener {
            val intent = Intent(this, ParkingMainActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.T1).setOnClickListener {
            val identity = getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)?.getInt("identity",0)
            if(identity==1){
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
            }else{

                    AlertDialog.Builder(this).apply {
                        setTitle("警告")
                        setMessage("您不是停车场管理员！")
                        setCancelable(false)
                        setPositiveButton("OK") { dialog, which ->
                        }
                        show()
                    }

                }
        }



        findViewById<ImageButton>(R.id.T2).setOnClickListener {
            val myidentity = this.getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)
                ?.getInt("identity",0)
            if(myidentity==1){
                    AlertDialog.Builder(this).apply {
                        setTitle("警告")
                        setMessage("您已经是一所停车场的管理员，无法重复申请")
                        setCancelable(false)
                        setPositiveButton("OK") { dialog, which ->
                        }
                        show()
                    }

            }
            else{
                val intent = Intent(this, ParkingLotApplicationActivity::class.java).apply {
                    putExtra(
                        ParkingLotApplicationActivity.FORM_MODE,
                        ParkingLotApplicationActivity.INSERT
                    )
                }
                startActivity(intent)
            }
        }


    }
}