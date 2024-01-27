package com.navneet.foodgenie.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnSendOtp: Button
    lateinit var txtErrorMsg: TextView
    var clickCount=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        etMobileNumber = findViewById(R.id.txtFpMobile)
        etEmail = findViewById(R.id.txtFpEmail)
        btnSendOtp = findViewById(R.id.btnSendOtp)
        txtErrorMsg = findViewById(R.id.fpErrorText)
        txtErrorMsg.text = ""
        btnSendOtp.setOnClickListener {
            var checkCondition: Boolean
            val mobile = etMobileNumber.text.toString()
            val email = etEmail.text.toString()
            if (mobile == "" || email == "") {
                checkCondition = false
                txtErrorMsg.text = "Please fill all input fields"
            } else {
                if (mobile.length != 10) {
                    checkCondition = false
                    txtErrorMsg.text = "Please enter valid mobile number"
                } else {
                    if (!email.contains('@') || !email.contains('.')) {
                        checkCondition = false
                        txtErrorMsg.text = "Enter Valid Email ID"
                    } else {
                        checkCondition = true
                    }
                }
            }
            if (checkNetwork()) {
                if (checkCondition) {
                    txtErrorMsg.text = ""
                    val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                    val url = BuildConfig.FORGOT_PASSWORD
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", mobile)
                    jsonParams.put("email", email)
                    val jsonObjectRequest =
                        object : JsonObjectRequest(
                            Method.POST, url, jsonParams, Response.Listener {
                                try {
                                    val userJsonObject = it.getJSONObject("data")
                                    val success = userJsonObject.getBoolean("success")
                                    if (success) {
                                        println(it)
                                            val intent =
                                                Intent(this, ResetPasswordActivity::class.java)
                                            intent.putExtra("mobile_number", mobile)
                                            startActivity(intent)
                                            ActivityCompat.finishAffinity(this)
                                    } else {
                                        val errorMessage = userJsonObject.getString("errorMessage")
                                        if (errorMessage == "No user found!") {
                                            Toast.makeText(
                                                this@ForgotPasswordActivity,
                                                "User not found",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@ForgotPasswordActivity,
                                                "Some issues ongoing, Please try later",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Some unknown error has occurred!!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
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
                }
            } else {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Internet not connected",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        clickCount++
        if(clickCount==1){
        }
        if(clickCount==2){
            super.onBackPressed()
        }
    }
    fun checkNetwork(): Boolean {
        return ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)
    }
}