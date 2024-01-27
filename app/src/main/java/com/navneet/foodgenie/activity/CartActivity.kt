package com.navneet.foodgenie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.navneet.foodgenie.BuildConfig
import com.navneet.foodgenie.R
import com.navneet.foodgenie.adapter.CartRecyclerAdapter
import com.navneet.foodgenie.database.CartDatabase
import com.navneet.foodgenie.database.CartEntity
import com.navneet.foodgenie.model.foodItemId
import com.navneet.foodgenie.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerView: RecyclerView
    lateinit var cnfrmOrder: Button
    lateinit var toolbar: Toolbar
    lateinit var progressLayout: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences
    var itemIdList = arrayListOf<foodItemId>()
    var dbCartList = listOf<CartEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val user_id = sharedPreferences.getString("user_id", null)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        var cost = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#FF2F2F")
        }
        recyclerView = findViewById(R.id.recyclerViewCartList)
        cnfrmOrder = findViewById(R.id.cnfrmOrder)
        toolbar = findViewById(R.id.CartToolBar)
        setUpActionBar()
        layoutManager = LinearLayoutManager(applicationContext)
        progressLayout = findViewById(R.id.progressLayout)
        dbCartList = RetrieveItems(this@CartActivity).execute().get()
        var res_id = ""
        val food = JSONArray()
        for (i in dbCartList) {
            val foodId = JSONObject()
            foodId.put("food_item_id", i.item_id.toString())
            food.put(foodId)
            println(food)
            val itemList = foodItemId(i.item_id.toString())
            itemIdList.add(itemList)
            res_id = i.resId
            cost += i.dishPrice.toInt()
        }
        recyclerAdapter =
            CartRecyclerAdapter(
                this@CartActivity,
                dbCartList,
                progressLayout
            )
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = layoutManager
        if (cost <= 0) {
            progressLayout.visibility = View.GONE
            cnfrmOrder.visibility = View.GONE
            Snackbar.make(
                findViewById(android.R.id.content),
                "Your cart is empty !!",
                Snackbar.LENGTH_LONG
            )
                .show()
        }
        cnfrmOrder.text = "PLACE ORDER(Rs. $cost)"
        cnfrmOrder.setOnClickListener {
            if (checkNetwork()) {
                if (!isLoggedIn) {
                    val dialog = AlertDialog.Builder(this@CartActivity)
                    dialog.setTitle("Login to proceed")
                    dialog.setMessage("Please login to continue proceed with your order")
                    dialog.setPositiveButton("Login") { _, _ ->
                        val intent = Intent(this@CartActivity, LoginActivity::class.java)
                        intent.putExtra("mobile_number", "")
                        intent.putExtra("forced_login", "true")
                        this@CartActivity.startActivity(intent)
                    }
                    dialog.setNegativeButton("Cancel") { text, listener ->

                    }
                    dialog.setCancelable(true)
                    dialog.create()
                    dialog.show()
                } else {
                    val queue = Volley.newRequestQueue(this@CartActivity)
                    val url = BuildConfig.PLACE_ORDERS_URL
                    val jsonParams = JSONObject()
                    jsonParams.put("user_id", user_id)
                    jsonParams.put("restaurant_id", res_id)
                    jsonParams.put("total_cost", cost.toString())
                    jsonParams.put("food", food)
                    val jsonObjectRequest =
                        object : JsonObjectRequest(
                            Method.POST, url, jsonParams, Response.Listener {
                                try {
                                    println(it)
                                    val userJsonObject = it.getJSONObject("data")
                                    val success = userJsonObject.getBoolean("success")
                                    if (success) {
                                        Toast.makeText(
                                            this@CartActivity,
                                            "Order placed successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        ClearDatabase(this@CartActivity).execute().get()
                                        val intent = Intent(this, HomePage::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@CartActivity,
                                            "Some error occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@CartActivity,
                                        "Some unknown error has occurred!!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@CartActivity,
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
                    this@CartActivity,
                    "Internet connection not found",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun setUpActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Cart"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    class RetrieveItems(val context: Context) :
        AsyncTask<Void, Void, List<CartEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<CartEntity> {
            val db =
                Room.databaseBuilder(context, CartDatabase::class.java, "cart-db")
                    .build()
            return db.cartDao().getAllItems()
        }
    }

    class ClearDatabase(val context: Context) :
        AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void {
            val db =
                Room.databaseBuilder(context, CartDatabase::class.java, "cart-db")
                    .build()
            return db.cartDao().clear()
        }
    }

    fun checkNetwork(): Boolean {
        return ConnectionManager().checkConnectivity(this@CartActivity)
    }
}