package com.example.freeparking07229.Activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.freeparking07229.Model.ParkingCard
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

class EditUserInfoActivity : AppCompatActivity() {

    inner class EditUserViewModel:ViewModel(){
        var user_name=MutableLiveData<String>()
        var user_sex=MutableLiveData<String>()
        var user_idnumber=MutableLiveData<String>()
        var user_carnumber=MutableLiveData<String>()
        var user_phonenumber=MutableLiveData<String>()
        var user_profile=MutableLiveData<String>()
    }


    val mysqlHelper = MysqlHelper()
    val editUserViewModel = EditUserViewModel()
    var card_id = ""

    val fromAlbum = 2
    private val REQUEST_STORAGE_PERMISSION = 100
    lateinit var outputImage: File
    lateinit var imageUri: Uri
    val client = OkHttpClient()
    val domain="http://10.0.2.2:8080"
    var img_url:String ="http://10.0.2.2:8080/upload_user"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_info)

        requestStoragePermission()

        val account = getSharedPreferences("data", MODE_PRIVATE).getString("account","null")
        val nameET = findViewById<EditText>(R.id.edit_user_name)
        val sexET = findViewById<EditText>(R.id.edit_user_sex)
        val idET = findViewById<EditText>(R.id.edit_user_idnumber)
        val carET = findViewById<EditText>(R.id.edit_user_carnumber)
        val phoneET = findViewById<EditText>(R.id.edit_user_phonenumber)
        val imgv = findViewById<ImageView>(R.id.edit_user_img)

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
                editUserViewModel.user_profile.value = parkingCard.person_picture

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
        editUserViewModel.user_profile.observe(this, Observer { profile ->
           Glide.with(this).load(profile).placeholder(R.drawable.male_profile).into(imgv)
        })

        imgv.setOnClickListener{
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent, 2)

        }

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
                                    img_url,
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val account = getSharedPreferences("data", MODE_PRIVATE).getString("account","null")
        when (requestCode) {
            2 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的图片显示
                        val bitmap = getBitmapFromUri(uri)
                        findViewById<ImageView>(R.id.edit_user_img).setImageBitmap(bitmap)
                        val imgPath = getPathFromUri(uri)
                        val myurl ="http://10.0.2.2:8080/upload_user"
                        val imgName = "image"+account+"_"+System.currentTimeMillis().toString()+".jpg"
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
                                findViewById<ProgressBar>(R.id.edit_user_photo_upload_progressbar).visibility =
                                    View.VISIBLE
                                withContext(Dispatchers.IO){
                                    val response: Response = client.newCall(request).execute()
                                    responseData=response.body!!.string()

                                    val msg = parseJSON(responseData)
                                    Log.d("ParkingLotApplicationActivity","读取到的信息是"+msg)
                                }
                                findViewById<ProgressBar>(R.id.edit_user_photo_upload_progressbar).visibility =
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