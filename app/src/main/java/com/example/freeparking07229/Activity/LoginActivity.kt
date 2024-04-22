package com.example.freeparking07229.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freeparking07229.MainActivity
import com.example.freeparking07229.R
import com.example.freeparking07229.Util.MysqlHelper
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private val mySQLHelper = MysqlHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.registerButton)
        val progressBar: ProgressBar = findViewById(R.id.progressbar)
        var flag=false;//未实名为false，已经实名为true
        var name = ""//真正名字

        val account = getSharedPreferences("data", MODE_PRIVATE).getString("account","null")

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                progressBar.visibility= View.VISIBLE
                var isLoginSuccessful = false
                var identity = 0
                withContext(Dispatchers.IO) {
                    isLoginSuccessful = mySQLHelper.checkLogin(username, password)
                    identity = mySQLHelper.LoginIdentity(username, password)
                    name = mySQLHelper.getNameInfoByAccount(username)
                    if(name!="")
                        flag=true
                    else
                        flag=false

                }
//                val identity = withContext(Dispatchers.IO) {
//                    mySQLHelper.LoginIdentity(username, password)
//                }
                progressBar.visibility= View.GONE
                if (isLoginSuccessful) {
                    // Successful login, navigate to another activity
                    val editor = getSharedPreferences("data", MODE_PRIVATE).edit().apply{
                        clear()
                        putString("account",username)//将账户名存入SP
                        putInt("identity",identity)//将用户身份存入SP
                        putBoolean("isValid",flag)
                        putString("name",name)
                        apply()
                    }
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)




                } else {
                    // Failed login, show error message
                    Toast.makeText(
                        this@LoginActivity,
                        "账号密码错误！请重新输入!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        registerButton.setOnClickListener {
            // Navigate to registration activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}