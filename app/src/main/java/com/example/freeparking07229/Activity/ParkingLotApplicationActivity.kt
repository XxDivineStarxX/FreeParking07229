package com.example.freeparking07229.Activity

import android.Manifest
import android.app.Activity
import android.content.EntityIterator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.freeparking07229.Model.ParkingLot
import com.example.freeparking07229.Model.ParkingSpace
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File

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
        var parking_img = MutableLiveData<String>()

    }
    val fromAlbum = 2
    private val REQUEST_STORAGE_PERMISSION = 100
    lateinit var outputImage: File
    lateinit var imageUri: Uri
    val client = OkHttpClient()
    val mysqlHelper = MysqlHelper()
    val domain="http://10.0.2.2:8080"
    var img_url:String ="http://10.0.2.2:8080/upload"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_lot_application)

        val account=getSharedPreferences("data", MODE_PRIVATE).getString("account","null")

        val parkingName=intent.getStringExtra(PARKING_LOT_NAME)
        val application_form_mode=intent.getIntExtra(FORM_MODE,0)
        var oldName =""
        var oldSpaceNum = 0
        val parkingInfoViewModel = ParkingLotApplicationViewModel()

        requestStoragePermission()
        val parkinglotNameEdit = findViewById<EditText>(R.id.application_parkinglot_name)
        val parkinglotLgtEdit = findViewById<EditText>(R.id.application_longitude)
        val parkinglotLatEdit = findViewById<EditText>(R.id.application_latitude)
        val parkinglotLocEdit = findViewById<EditText>(R.id.application_location)
        val parkinglotNumEdit = findViewById<EditText>(R.id.application_space_number)
        val parkinglotDecEdit = findViewById<EditText>(R.id.application_description)
        val parkinglotAdminEdit = findViewById<EditText>(R.id.application_admin)
        val parkinglotImageView = findViewById<ImageView>(R.id.application_parkinglot_img)

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
                    parkingInfoViewModel.parking_img.value = parkingLot.parking_picture
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
            parkingInfoViewModel.parking_img.observe(this, Observer { img ->
                Glide.with(this).load(img).into(parkinglotImageView)
            })
        }

        parkinglotImageView.setOnClickListener{
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent, 2)

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
                            img_url,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val account = getSharedPreferences("data", MODE_PRIVATE).getString("account","null")
        when (requestCode) {
            2 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的图片显示
                        val bitmap = getBitmapFromUri(uri)
                        findViewById<ImageView>(R.id.application_parkinglot_img).setImageBitmap(bitmap)
                        val imgPath = getPathFromUri(uri)
                        val myurl ="http://10.0.2.2:8080/upload"
                        val imgName = "image"+account+".jpg"
                        val requestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file",imgName,
                                File(imgPath).asRequestBody("image/jpeg".toMediaTypeOrNull())
                            )
                        val request= Request.Builder().url(myurl)
                            .post(requestBody.build())
                            .build()
                        var responseData = ""
                        try {
                            CoroutineScope(Dispatchers.Main).launch {
                                findViewById<ProgressBar>(R.id.application_parkinglot_photo_upload_progressbar).visibility =
                                    View.VISIBLE
                                withContext(Dispatchers.IO){
                                    val response:Response = client.newCall(request).execute()
                                    responseData=response.body!!.string()

                                    val msg = parseJSON(responseData)
                                    Log.d("ParkingLotApplicationActivity","读取到的信息是"+msg)
                                }
                                findViewById<ProgressBar>(R.id.application_parkinglot_photo_upload_progressbar).visibility =
                                    View.GONE
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }

        }
    }
    private fun parseJSON(jsonData: String):String {
        val jsonObject = JSONObject(jsonData)
        img_url = domain+jsonObject.getString("message")
        return img_url
    }
    private fun getPathFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        var path:String =""
        if(cursor!=null){
            val column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            path = cursor.getString(column_index)
            cursor.close()
            Log.d("ReservationActivity","uri-》路径获取成功")

        }else{
            Log.d("ReservationActivity","uri-》路径获取失败")
        }
        return path

    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }
    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height,
            matrix, true)
        bitmap.recycle() // 将不再需要的Bitmap对象回收
        return rotatedBitmap
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }


    // 在Activity的onCreate方法或其他适当的地方调用
    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 如果没有权限，则请求权限
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
        }
    }

    // 处理权限请求的回调
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了存储权限，您可以执行相应的操作
                Log.d("ReservationActivity","同意授权")
            } else {
                // 用户拒绝了权限请求，您可能需要提醒用户并解释为什么需要这个权限
            }
        }
    }
}