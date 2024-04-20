package com.example.freeparking07229.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.freeparking07229.Activity.AdminActivity
import com.example.freeparking07229.Activity.HistoryActivity
import com.example.freeparking07229.Activity.IdentityActivity
import com.example.freeparking07229.Activity.ParkingInfoActivity
import com.example.freeparking07229.Activity.ParkingLotApplicationActivity
import com.example.freeparking07229.Activity.ParkingMainActivity
import com.example.freeparking07229.Activity.ReservationActivity
import com.example.freeparking07229.Activity.userInfoActivity
import com.example.freeparking07229.R

import com.example.freeparking07229.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var view =  inflater.inflate(R.layout.fragment_home, container, false)

        binding.F1.setOnClickListener {
            Log.d("HomeFragment","检测到点击")
            val intent = Intent(activity, userInfoActivity::class.java)
            startActivity(intent)
        }

        binding.F2.setOnClickListener {
            val intent = Intent(activity, HistoryActivity::class.java)
            startActivity(intent)
        }


        binding.S1.setOnClickListener {
            val intent = Intent(activity, ParkingMainActivity::class.java)
            startActivity(intent)
        }

        binding.S2.setOnClickListener {
            val intent = Intent(activity, ParkingInfoActivity::class.java).apply {
                putExtra(ParkingInfoActivity.PARKING_LOT_NAME,"北京停车场")
            }
            startActivity(intent)
        }

        binding.S3.setOnClickListener {
            val intent = Intent(activity, ParkingMainActivity::class.java)
            startActivity(intent)
        }

        binding.T1.setOnClickListener {
            val identity = context?.getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)?.getInt("identity",0)
            if(identity==1){
                val intent = Intent(activity, AdminActivity::class.java)
                startActivity(intent)
            }else{
                context?.let { it1 ->
                    AlertDialog.Builder(it1).apply {
                        setTitle("警告")
                        setMessage("您不是停车场管理员！")
                        setCancelable(false)
                        setPositiveButton("OK") { dialog, which ->
                        }
                        show()
                    }

                }
            }
        }



        binding.T2.setOnClickListener {
            val myidentity = activity?.getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)
                ?.getInt("identity",0)
            if(myidentity==1){
                context?.let { it1 ->
                    AlertDialog.Builder(it1).apply {
                        setTitle("警告")
                        setMessage("您已经是一所停车场的管理员，无法重复申请")
                        setCancelable(false)
                        setPositiveButton("OK") { dialog, which ->
                        }
                        show()
                    }
                }
            }
            else{
                val intent = Intent(activity, ParkingLotApplicationActivity::class.java).apply {
                    putExtra(
                        ParkingLotApplicationActivity.FORM_MODE,
                        ParkingLotApplicationActivity.INSERT
                    )
                }
                startActivity(intent)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}