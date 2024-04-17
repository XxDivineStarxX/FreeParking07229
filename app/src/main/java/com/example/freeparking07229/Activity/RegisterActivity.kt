package com.example.freeparking07229.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import kotlinx.coroutines.*


class RegisterActivity : AppCompatActivity() {
    private val mysqlHelper = MysqlHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerUsernameEditText:EditText= findViewById(R.id.registerUsernameEditText)
        val registerPasswordEditText:EditText= findViewById(R.id.registerPasswordEditText)
        val registerEmailEditText:EditText= findViewById(R.id.registerEmailEditText)
        val registerIdentityEditText:EditText= findViewById(R.id.registerIdentityEditText)

        val confirmRegisterButton:Button=findViewById(R.id.confirmRegisterButton)
        confirmRegisterButton.setOnClickListener {
            val username = registerUsernameEditText.text.toString()
            val password = registerPasswordEditText.text.toString()
            val email = registerEmailEditText.text.toString()
            val identity = registerIdentityEditText.text.toString().toInt()

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    mysqlHelper.insertUser(username, password, email, identity)
                }

                // Show a Toast message indicating successful registration
                Toast.makeText(this@RegisterActivity, "注册成功!", Toast.LENGTH_SHORT).show()

                // Finish the registration activity
                finish()
            }
        }



    }

}