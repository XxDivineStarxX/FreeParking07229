package com.example.freeparking07229.Activity

import android.Manifest
import android.app.Activity
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freeparking07229.Model.ParkingSpace
import com.example.freeparking07229.Model.UsingSpace
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.CarPlateHelper
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
import java.sql.Timestamp

class CreateNewUsingSpaceActivity : AppCompatActivity() {

    companion object{
        //Extra
        val SPACE_SELECTED = "0"
        val PARKING_LOT_SELECTED = "1"
    }

    val fromAlbum = 2
    private val REQUEST_STORAGE_PERMISSION = 100
    lateinit var outputImage: File
    lateinit var imageUri: Uri
    val client = OkHttpClient()
    val mysqlHelper = MysqlHelper()
    var my_car_number:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_using_space)

        requestStoragePermission()
        val space_id_selected=intent.getIntExtra(SPACE_SELECTED,-1)
        val parking_lot_selected=intent.getStringExtra(PARKING_LOT_SELECTED)


        findViewById<ImageView>(R.id.admin_usingSpace_photo_upload).setOnClickListener {

            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent, fromAlbum)
        }

        findViewById<Button>(R.id.btn_admin_usingSpace_add_finish).setOnClickListener {
            val account = getSharedPreferences("data", MODE_PRIVATE).getString("account","111")
            val identity = getSharedPreferences("data", MODE_PRIVATE).getInt("identity",0)
            var card_id = getSharedPreferences("data", MODE_PRIVATE).getString("card_id","null")

            if(identity>=0){
                if(card_id.equals("null")){
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO){
                            card_id = if(account!=null){
                                mysqlHelper.getCardIDInfoByAccount(account)
                            } else
                                "11111"
                            getSharedPreferences("data", MODE_PRIVATE).edit().putString("card_id",card_id).apply()
                        }
                    }
                }
                //创建添加的UsingSpace对象
                val usingSpace= UsingSpace()
                if(space_id_selected<0){
                    Log.d("CreateNewUsingSpaceActivity","未正确获取选中车位space_id")
                }
                else usingSpace.space_id = space_id_selected

                usingSpace.is_reserved = 0

                val card_id_1 =getSharedPreferences("data", MODE_PRIVATE).getString("card_id","null")
                if(card_id_1==null){
                    Log.d("CreateNewUsingSpaceActivity","未正确获取选card_id")
                }
                else usingSpace.parking_id= findViewById<EditText>(R.id.admin_usingSpace_parking_cardId).text.toString()

                usingSpace.car_number= CarPlateHelper.carPlateNormlize(my_car_number)

                if(parking_lot_selected==null){
                    Log.d("CreateNewUsingSpaceActivity","未正确获取选中停车场parkinglot")
                }
                else usingSpace.parking_lot = parking_lot_selected

                usingSpace.lock_time= Timestamp(System.currentTimeMillis())
                usingSpace.time= "1min"
                usingSpace.cost= 0.10

                //创建添加的ParkingSpace对象
                val parkingSpace = ParkingSpace()
                parkingSpace.space_id = usingSpace.space_id
                parkingSpace.parking_lot = usingSpace.parking_lot
                parkingSpace.state=2

                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        val spaceAvail = mysqlHelper.getParkLotInfoByName(parkingSpace.parking_lot).space_available
                        mysqlHelper.InsertUsingSpace(usingSpace)
                        mysqlHelper.UpdateParkingSpace(parkingSpace,parkingSpace.state,spaceAvail)
                    }
                    finish()
                    Log.d("CreateNewUsingSpaceActivity","插入成功")
                }


            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的图片显示
                        val bitmap = getBitmapFromUri(uri)
                        findViewById<ImageView>(R.id.admin_usingSpace_photo_upload).setImageBitmap(bitmap)
                        val imgPath = getPathFromUri(uri)
                        val myurl ="http://10.0.2.2:5000/upload_image"
                        val requestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image","image.jpg",
                                File(imgPath).asRequestBody("image/jpeg".toMediaTypeOrNull())
                            )
                        val request= Request.Builder().url(myurl)
                            .post(requestBody.build())
                            .build()

                        var responseData = ""
                        try {
                            CoroutineScope(Dispatchers.Main).launch {
                                findViewById<ProgressBar>(R.id.admin_usingSpace_photo_upload_progressbar).visibility =View.VISIBLE
                                withContext(Dispatchers.IO){
                                    val response: Response = client.newCall(request).execute()
                                    responseData=response.body!!.string()

                                    val msg = parseJSON(responseData)
                                    Log.d("CreateNewUsingSpaceActivity","读取到的信息是"+msg)

                                }
                                findViewById<ProgressBar>(R.id.admin_usingSpace_photo_upload_progressbar).visibility =View.GONE

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
        my_car_number = jsonObject.getString("message")
        return  my_car_number
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
            Log.d("CreateNewUsingSpaceActivity","uri-》路径获取成功")

        }else{
            Log.d("CreateNewUsingSpaceActivity","uri-》路径获取失败")
        }
        return path

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
                Log.d("CreateNewUsingSpaceActivity","同意授权")
            } else {
                // 用户拒绝了权限请求，您可能需要提醒用户并解释为什么需要这个权限
            }
        }
    }
}