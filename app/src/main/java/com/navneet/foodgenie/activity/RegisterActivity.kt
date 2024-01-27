package com.navneet.foodgenie.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class RegisterActivity : AppCompatActivity() {
    lateinit var etFullName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etCnfPassword: EditText
    lateinit var btnRegister: Button
    lateinit var errorMsg: TextView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        etFullName = findViewById(R.id.etRegName)
        etEmail = findViewById(R.id.etRegEmail)
        etMobileNumber = findViewById(R.id.etRegMobileNumber)
        etAddress = findViewById(R.id.etRegAddress)
        etPassword = findViewById(R.id.etRegPassword)
        etCnfPassword = findViewById(R.id.etRegCnfPassword)
        btnRegister = findViewById(R.id.btnRegister)
        errorMsg = findViewById(R.id.errorText)

        btnRegister.setOnClickListener {
            var checkCondition: Boolean
            val fullname = etFullName.text.toString()
            val email = etEmail.text.toString()
            val phone = etMobileNumber.text.toString()
            val address = etAddress.text.toString()
            val cnfPassword = etCnfPassword.text.toString()
            val password = etPassword.text.toString()
            if (fullname == "" || email == "" || phone == "" || address == "" || address == "" || cnfPassword == "" || password == "") {
                checkCondition = false
                errorMsg.text = "Please fill out all fields"
            } else {
                if (fullname.length < 3) {
                    checkCondition = false
                    errorMsg.text = "Enter name more than 3 characters"
                } else {
                    if (phone.length != 10) {
                        checkCondition = false
                        errorMsg.text = "Enter Phone number"
                    } else {
                        if (password.length < 6) {
                            checkCondition = false
                            errorMsg.text = "Please enter password more than 6 characters"
                        } else {
                            if (password != cnfPassword) {
                                checkCondition = false
                                errorMsg.text = "Password and confirm password value not matches"
                            } else {
                                checkCondition = true
                            }
                        }
                    }
                }
            }
            if (checkCondition) {
                errorMsg.text = ""
                if (checkNetwork()) {
                    val queue = Volley.newRequestQueue(this@RegisterActivity)
                    val url = BuildConfig.REGISTER_URL
                    val jsonParams = JSONObject()
                    jsonParams.put("name", fullname)
                    jsonParams.put("mobile_number", phone)
                    jsonParams.put("password", password)
                    jsonParams.put("address", address)
                    jsonParams.put("email", email)
                    val jsonObjectRequest =
                        object : JsonObjectRequest(
                            Method.POST, url, jsonParams, Response.Listener {
                                try {
                                    val userJsonObject = it.getJSONObject("data")
                                    val success = userJsonObject.getBoolean("success")
                                    if (success) {
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        val errorMessage = userJsonObject.getString("errorMessage")
                                        if (errorMessage == "Mobile number OR Email Id is already registered") {
                                            Toast.makeText(
                                                this@RegisterActivity,
                                                "Mobile number OR Email Id is already registered",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@RegisterActivity,
                                                "Some issues ahead,Please try later",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Some unknown error has occurred!!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@RegisterActivity,
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
                        this@RegisterActivity,
                        "Internet not connected",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
    }

    fun checkNetwork(): Boolean {
        return ConnectionManager().checkConnectivity(this@RegisterActivity)
    }
}