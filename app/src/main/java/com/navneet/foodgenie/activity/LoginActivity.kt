package com.navneet.foodgenie.activity


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.navneet.foodgenie.BuildConfig
import com.navneet.foodgenie.R
import com.navneet.foodgenie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var txtskip: TextView
    lateinit var etPhone: EditText
    lateinit var etPassword: EditText
    lateinit var txtforgotPassword: TextView
    lateinit var btnLogin: Button
    lateinit var txtRegister: TextView
    lateinit var txtErrorMsg: TextView
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val isSkipped = sharedPreferences.getBoolean("isSkipped", false)
        val forcedLogin = intent.getStringExtra("forced_login").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        txtskip = findViewById(R.id.txtSkip)
        etPhone = findViewById(R.id.etLoginPhone)
        etPassword = findViewById(R.id.etLoginPassword)
        txtforgotPassword = findViewById(R.id.txtForgotPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtRegister = findViewById(R.id.txtLoginRegister)
        txtErrorMsg = findViewById(R.id.txtLoginError)
        txtErrorMsg.text = ""

        if (forcedLogin == "true") {
            txtskip.visibility = View.GONE
        } else {
            if (isLoggedIn) {
                val intent = Intent(this@LoginActivity, HomePage::class.java)
                startActivity(intent)
                finish()
            } else if (isSkipped) {
                val intent = Intent(this@LoginActivity, HomePage::class.java)
                startActivity(intent)
                finish()
            }
        }


        if (intent != null) {
            etPhone.setText(intent.getStringExtra("mobile_number").toString())
            etPhone.setSelectAllOnFocus(true)
        }

        txtskip.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            savePreference("isSkip")
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            val phone = etPhone.text.toString()
            val password = etPassword.text.toString()
            var checkCondition = true

            if (phone == "") {
                checkCondition = false
                txtErrorMsg.text = "Please enter phone number"
            } else {
                if (password == "") {
                    checkCondition = false
                    txtErrorMsg.text = "Please enter password"
                } else {
                    if (phone.length < 10) {
                        checkCondition = false
                        txtErrorMsg.text = "Please enter 10 digit mobile number"
                    } else {
                        if (!phone.startsWith("6", 0) && !phone.startsWith(
                                "7",
                                0
                            ) && !phone.startsWith("8", 0) && !phone.startsWith("9", 0)
                        ) {
                            checkCondition = false
                            txtErrorMsg.text = "Invalid mobile number"
                        }
                    }
                }
            }
            if (checkCondition) {
                txtErrorMsg.text = ""
                val queue = Volley.newRequestQueue(this@LoginActivity)
                val url = BuildConfig.LOGIN_URL
                if (checkNetwork()) {
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", phone)
                    jsonParams.put("password", password)
                    val jsonObjectRequest =
                        object : JsonObjectRequest(
                            Method.POST, url, jsonParams, Response.Listener {
                                try {
                                    println(it)
                                    val userJsonObject = it.getJSONObject("data")
                                    val success = userJsonObject.getBoolean("success")

                                    if (success) {
                                        val userData = userJsonObject.getJSONObject("data")
                                        val userid = userData.getString("user_id")
                                        val username = userData.getString("name")
                                        val useremail = userData.getString("email")
                                        val usermobilenumber =
                                            userData.getString("mobile_number")
                                        val useraddress = userData.getString("address")
                                        saveUserDetails(
                                            userid,
                                            username,
                                            useremail,
                                            usermobilenumber,
                                            useraddress
                                        )
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Login Successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, HomePage::class.java)
                                        savePreference("login")
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        when (userJsonObject.getString("errorMessage")) {
                                            "Mobile Number not registered" -> {
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "Mobile Number not registered",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            "Incorrect password" -> {
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "Incorrect Password",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            "Input field invalid data" -> {
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "Please enter correct details",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }

                                } catch (e: JSONException) {
                                    println(e)
                                    Toast.makeText(
                                        this@LoginActivity,

                                        "Some unknown error has occurred!!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Some error has been occurred, Please try again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["content-type"] = "application/json"
                                headers["token"] = BuildConfig.API_TOKEN
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(settingsIntent)
                        this@LoginActivity?.finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@LoginActivity as Activity)
                    }
                    dialog.setCancelable(false)
                    dialog.create()
                    dialog.show()
                }
            }

        }

        txtRegister.setOnClickListener {
            if (checkNetwork()) {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@LoginActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        txtforgotPassword.setOnClickListener {
            if (checkNetwork()) {
                val intent = Intent(this, ForgotPasswordActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@LoginActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun savePreference(type: String) {
        if (type == "isSkip") {
            sharedPreferences.edit().putBoolean("isSkipped", true).apply()
        } else if (type == "login") {
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        }


    }

    fun saveUserDetails(
        user_id: String,
        user_name: String,
        user_email: String,
        user_mobile_number: String,
        user_address: String
    ) {
        sharedPreferences.edit().putString("user_id", user_id).apply()
        sharedPreferences.edit().putString("user_name", user_name).apply()
        sharedPreferences.edit().putString("user_email", user_email).apply()
        sharedPreferences.edit().putString("user_mobile_number", user_mobile_number).apply()
        sharedPreferences.edit().putString("user_address", user_address).apply()
    }

    fun checkNetwork(): Boolean {
        return ConnectionManager().checkConnectivity(this@LoginActivity)
    }
}