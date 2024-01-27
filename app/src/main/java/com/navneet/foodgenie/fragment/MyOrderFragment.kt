package com.navneet.foodgenie.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.navneet.foodgenie.BuildConfig

import com.navneet.foodgenie.activity.LoginActivity
import com.navneet.foodgenie.R
import com.navneet.foodgenie.adapter.OrderHistoryRecyclerAdapter
import com.navneet.foodgenie.model.foodItems
import com.navneet.foodgenie.util.ConnectionManager
import org.json.JSONException

class MyOrderFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var btnLogin: Button
    lateinit var recyclerViewOrderHistory: RecyclerView
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    var foodList = arrayListOf<foodItems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        layoutManager = LinearLayoutManager(activity)
        var view = inflater.inflate(R.layout.fragment_my_order, container, false)
        recyclerViewOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val userId = sharedPreferences.getString("user_id", null)
            if (checkNetwork()) {
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "${BuildConfig.ORDERS_URL}/$userId"
                println(url)
                val jsonObjectRequest =
                    object : JsonObjectRequest(
                        Method.GET, url, null, Response.Listener {
                            try {
                                println(it)
                                val jsonObject = it.getJSONObject("data")
                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    val data = jsonObject.getJSONArray("data")
                                    for (i in 0 until data.length()) {
                                        val ordersDetails = data.getJSONObject(i)
                                        val foodItem = ordersDetails.getJSONArray("food_items")
                                        val orderDetails = foodItems(
                                            ordersDetails.getInt("order_id"),
                                            ordersDetails.getString("restaurant_name"),
                                            ordersDetails.getString("order_placed_at"),
                                            foodItem
                                        )
                                        foodList.add(orderDetails)
                                        recyclerAdapter = OrderHistoryRecyclerAdapter(
                                            activity as Context,
                                            foodList
                                        )
                                        recyclerViewOrderHistory.adapter = recyclerAdapter
                                        recyclerViewOrderHistory.itemAnimator =
                                            DefaultItemAnimator()
                                        recyclerViewOrderHistory.layoutManager = layoutManager
                                    }
                                } else {
                                    Toast.makeText(
                                        activity as Context,
                                        "Some Error has occured",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    activity as Context,
                                    "Some unknown error has occurred!!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            if (activity != null) {
                                Toast.makeText(
                                    activity as Context,
                                    "Some error has been occurred, Please try again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["content-type"] = "application/json"
                            headers["token"] =BuildConfig.API_TOKEN
                                return headers
                        }
                    }
                queue.add(jsonObjectRequest)
            } else {
                Toast.makeText(context, "Internet connection not found", Toast.LENGTH_LONG).show()
            }
        } else {
            view = inflater.inflate(R.layout.login_to_continue, container, false)
            btnLogin = view.findViewById(R.id.loginBtn)
            btnLogin.setOnClickListener {
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("mobile_number", "")
                intent.putExtra("forced_login", "true")
                startActivity(intent)
            }
        }
        return view
    }

    fun checkNetwork(): Boolean {
        return ConnectionManager().checkConnectivity(context as Activity)
    }
}