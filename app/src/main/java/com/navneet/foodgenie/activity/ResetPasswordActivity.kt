package com.navneet.foodgenie.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.navneet.foodgenie.BuildConfig
import com.navneet.foodgenie.R
import com.navneet.foodgenie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var etOtp: EditText
    lateinit var etPasswword: EditText
    lateinit var etCnfPassword: EditText
    lateinit var txtError: TextView
    lateinit var btnResetPassword: Button
    lateinit var sharedPreferences: SharedPreferences
    var clickCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        etOtp = findViewById(R.id.txtResOtp)
        etPasswword = findViewById(R.id.txtResPassword)
        etCnfPassword = findViewById(R.id.txtResCnfPassword)
        txtError = findViewById(R.id.resErrorText)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        txtError.text = ""
        btnResetPassword.setOnClickListener {
            val otp = etOtp.text.toString()
            val password = etPasswword.text.toString()
            val cnfPassword = etCnfPassword.text.toString()
            if (otp.length < 4) {
                txtError.text = "Please enter valid OTP"
            } else {
                if (password == "" || cnfPassword == "") {
                    txtError.text = "Please enter new password in both fields"
                } else {
                    if (password != cnfPassword) {
                        txtError.text = "New password and confirm password not match"
                    } else {
                        if (password.length < 6) {
                            txtError.text = "Password should be more than 6 characters"
                        } else
                            txtError.text = ""
                    }
                }
            }
            if (txtError.text == "") {
                if (checkNetwork()) {
                    val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
                    val url = BuildConfig.RESET_PASSWORD
                    val jsonParams = JSONObject()
                    val mobile = intent.getStringExtra("mobile_number")
                    jsonParams.put("mobile_number", mobile)
                    jsonParams.put("password", password)
                    jsonParams.put("otp", otp)
                    val jsonObjectRequest =
                        object : JsonObjectRequest(
                            Method.POST, url, jsonParams, Response.Listener {
                                try {
                                    val userJsonObject = it.getJSONObject("data")
                                    val success = userJsonObject.getBoolean("success")
                                    if (success) {
                                        val successMessage =
                                            userJsonObject.getString("successMessage")
                                        if (successMessage == "Password has successfully changed.") {
                                            Toast.makeText(
                                                this@ResetPasswordActivity,
                                                "Password reset successfully",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            sharedPreferences =
                                                getSharedPreferences(
                                                    getString(R.string.preference_file_name),
                                                    Context.MODE_PRIVATE
                                                )
                                            sharedPreferences.edit().clear()
                                            val intent = Intent(
                                                this@ResetPasswordActivity,
                                                LoginActivity::class.java
                                            )
                                            intent.putExtra("mobile_number", mobile)
                                            startActivity(intent)
                                            finish()
                                        }
                                    } else {
                                        println(it)
                                        val errorMessage = userJsonObject.getString("errorMessage")
                                        if (errorMessage == "Invalid OTP") {
                                            Toast.makeText(
                                                this@ResetPasswordActivity,
                                                "Please enter correct OTP",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@ResetPasswordActivity,
                                                "Some issues ahead,Please try later",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@ResetPasswordActivity,
                                        "Some unknown error has occurred!!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
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
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Please check your internet connection.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    fun checkNetwork(): Boolean {
        return ConnectionManager().checkConnectivity(this@ResetPasswordActivity)
    }
}