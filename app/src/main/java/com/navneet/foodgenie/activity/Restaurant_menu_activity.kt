package com.navneet.foodgenie.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.navneet.foodgenie.BuildConfig
import com.navneet.foodgenie.R
import com.navneet.foodgenie.adapter.RestaurantItemListAdapter
import com.navneet.foodgenie.database.CartEntity
import com.navneet.foodgenie.database.RestaurantDatabase
import com.navneet.foodgenie.database.RestaurantEntity
import com.navneet.foodgenie.model.Menu
import com.navneet.foodgenie.util.ConnectionManager
import org.json.JSONException

class Restaurant_menu_activity : AppCompatActivity() {
    lateinit var recyclerViewMenuItem: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantItemListAdapter
    lateinit var toolbar: Toolbar
    lateinit var addToFav: ImageView
    lateinit var btnPlaceOrder: Button
    lateinit var progressLayout: RelativeLayout
    var restaurant_name = "Food Genie"
    var restaurant_id = "100"
    var restaurant_cost = ""
    var restaurant_rating = ""
    var restaurant_image = ""
    lateinit var restaurantEntity: RestaurantEntity
    var cartEntityList = arrayListOf<CartEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)
        addToFav = findViewById(R.id.addToFav)
        recyclerViewMenuItem = findViewById(R.id.recyclerViewItemsMenu)
        layoutManager = LinearLayoutManager(applicationContext)
        toolbar = findViewById(R.id.toolbar)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        progressLayout = findViewById(R.id.progressLayout)
        btnPlaceOrder.visibility = View.GONE
        setUpActionBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#FF2F2F")
        }
        val menuList = arrayListOf<Menu>()
        val queue = Volley.newRequestQueue(this@Restaurant_menu_activity)
        if (intent != null) {
            restaurant_name = intent.getStringExtra("restaurant_name").toString()
            restaurant_id = intent.getStringExtra("restaurant_id").toString()
            restaurant_cost = intent.getStringExtra("restaurant_cost_for_one").toString()
            restaurant_image = intent.getStringExtra("image_url").toString()
            restaurant_rating = intent.getStringExtra("rating").toString()
            restaurantEntity = RestaurantEntity(
                restaurant_id.toInt(),
                restaurant_name,
                restaurant_cost,
                restaurant_rating,
                restaurant_image
            )

            supportActionBar?.title = restaurant_name
        } else {
            finish()
            Toast.makeText(
                this@Restaurant_menu_activity,
                "Unable to open: Some unexpected error occurred",
                Toast.LENGTH_LONG
            ).show()
        }
        if (restaurant_id == "100") {
            finish()
            Toast.makeText(
                this@Restaurant_menu_activity,
                "Unable to open: Some unexpected error occurred",
                Toast.LENGTH_LONG
            ).show()
        }
        val is_fav =
            DBAsyncTask(
                this@Restaurant_menu_activity,
                restaurantEntity,
                1
            ).execute().get()

        if (is_fav) {
            addToFav.setImageResource(R.drawable.ic_fav_tab)
        } else {
            addToFav.setImageResource(R.drawable.ic_fav)
        }
        addToFav.setOnClickListener {
            if (!DBAsyncTask(this@Restaurant_menu_activity, restaurantEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(this@Restaurant_menu_activity, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        this@Restaurant_menu_activity,
                        "Added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    addToFav.setImageResource(R.drawable.ic_fav_tab)
                } else {
                    Toast.makeText(
                        this@Restaurant_menu_activity,
                        "Some error has been occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async =
                    DBAsyncTask(this@Restaurant_menu_activity, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        this@Restaurant_menu_activity,
                        "Removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    addToFav.setImageResource(R.drawable.ic_fav)
                } else {
                    Toast.makeText(
                        this@Restaurant_menu_activity,
                        "Some error has been occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        val url = "${BuildConfig.RESTAURANTS_FETCH}/$restaurant_id"
        if (checkNetwork()) {
            val jsonObjectRequest =
                object : JsonObjectRequest(
                    Method.GET, url, null, Response.Listener {
                        try {
                            val jsonObject = it.getJSONObject("data")
                            val success = jsonObject.getBoolean("success")
                            if (success) {
                                val data = jsonObject.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val restaurantJsonObject = data.getJSONObject(i)
                                    val restaurantObject = Menu(
                                        restaurantJsonObject.getString("id"),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("cost_for_one"),
                                        restaurantJsonObject.getString("restaurant_id")
                                    )
                                    val cartEntity = CartEntity(
                                        restaurantJsonObject.getString("id").toInt(),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("cost_for_one"),
                                        restaurantJsonObject.getString("restaurant_id")
                                    )
                                    menuList.add(restaurantObject)
                                    cartEntityList.add(cartEntity)
                                    recyclerAdapter =
                                        RestaurantItemListAdapter(
                                            this@Restaurant_menu_activity,
                                            menuList,
                                            btnPlaceOrder,
                                            restaurant_id,
                                            cartEntityList,
                                            progressLayout
                                        )
                                    recyclerViewMenuItem.adapter = recyclerAdapter
                                    recyclerViewMenuItem.layoutManager = layoutManager

                                }
                            } else {
                                Toast.makeText(
                                    this@Restaurant_menu_activity,
                                    "Some Error has occured",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@Restaurant_menu_activity,
                                "Some unknown error has occurred!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    Response.ErrorListener {

                        Toast.makeText(
                            this@Restaurant_menu_activity,
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
            val dialog = AlertDialog.Builder(this@Restaurant_menu_activity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingsIntent)
                this@Restaurant_menu_activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@Restaurant_menu_activity as Activity)
            }
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()

        }
    }

    fun setUpActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Food Genie"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Food Genie"
    }

    fun checkNetwork(): Boolean {
        return ConnectionManager().checkConnectivity(this@Restaurant_menu_activity)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurant_id.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}