package com.navneet.foodgenie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.navneet.foodgenie.BuildConfig
import com.navneet.foodgenie.R
import com.navneet.foodgenie.adapter.DashboardRecyclerAdapter
import com.navneet.foodgenie.database.RestaurantDatabase
import com.navneet.foodgenie.database.RestaurantEntity
import com.navneet.foodgenie.model.Restaurants
import com.navneet.foodgenie.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class AllrestaurantFragment : Fragment() {
    lateinit var recylerViewDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerAdapter: DashboardRecyclerAdapter
    var restaurantList = arrayListOf<Restaurants>()
    var restaurantEntityList = arrayListOf<RestaurantEntity>()
    var checkedOption = -1

    var ratingComparator = Comparator<Restaurants> { restaurant1, restaurant2 ->
        if (restaurant1.rating.compareTo(restaurant2.rating, true) == 0) {
            restaurant1.resaturant_name.compareTo(restaurant2.resaturant_name, true)
        } else {
            restaurant1.rating.compareTo(restaurant2.rating, true)
        }
    }
    var nameComparator = Comparator<Restaurants> { restaurant1, restaurant2 ->
        restaurant1.resaturant_name.compareTo(restaurant2.resaturant_name, true)
    }
    var costComparator = Comparator<Restaurants> { restaurant1, restaurant2 ->
        if (restaurant1.cost_for_one.compareTo(restaurant2.cost_for_one, true) == 0) {
            restaurant1.resaturant_name.compareTo(restaurant2.resaturant_name, true)
        } else {
            restaurant1.cost_for_one.compareTo(restaurant2.cost_for_one, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_allrestaurant, container, false)
        setHasOptionsMenu(true)
        recylerViewDashboard = view.findViewById(R.id.recyclerViewDashboard)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = BuildConfig.RESTAURANTS_FETCH
        if (checkNetwork()) {
            progressLayout.visibility = View.VISIBLE
            val jsonObjectRequest =
                object : JsonObjectRequest(
                    Method.GET, url, null, Response.Listener {
                        try {
                            val jsonObject = it.getJSONObject("data")
                            val success = jsonObject.getBoolean("success")
                            progressLayout.visibility = View.GONE
                            if (success) {
                                val data = jsonObject.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val restaurantJsonObject = data.getJSONObject(i)
                                    val restaurantObject = Restaurants(
                                        restaurantJsonObject.getString("id"),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("rating"),
                                        restaurantJsonObject.getString("cost_for_one"),
                                        restaurantJsonObject.getString("image_url")
                                    )
                                    val restaurantEntity = RestaurantEntity(
                                        restaurantJsonObject.getString("id").toInt(),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("cost_for_one"),
                                        restaurantJsonObject.getString("rating"),
                                        restaurantJsonObject.getString("image_url")
                                    )
                                    restaurantEntityList.add(restaurantEntity)
                                    restaurantList.add(restaurantObject)
                                    if (activity != null) {
                                        recyclerAdapter =
                                            DashboardRecyclerAdapter(
                                                activity as Context,
                                                restaurantList,
                                                restaurantEntityList
                                            )
                                        recylerViewDashboard.adapter = recyclerAdapter
                                        recylerViewDashboard.layoutManager = layoutManager
                                    }
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
                        headers["token"] = BuildConfig.API_TOKEN
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()

        }
        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.titlebar_nav_icon, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.iconTopSort) {
            val options = arrayOf("Name", "Rating", "Cost (Low-High)", "Cost (High-Low)")

            val dialogBox = AlertDialog.Builder(activity as Context)
            dialogBox.setTitle("Sort by")
            dialogBox.setSingleChoiceItems(
                options, checkedOption
            ) { dialog, which ->
                checkedOption = which
                when (which) {
                    0 -> {
                        Collections.sort(restaurantList, nameComparator)
                        dialog.dismiss()
                        recyclerAdapter.notifyDataSetChanged()
                    }
                    1 -> {
                        Collections.sort(restaurantList, ratingComparator)
                        restaurantList.reverse()
                        dialog.dismiss()
                        recyclerAdapter.notifyDataSetChanged()
                    }
                    2 -> {
                        Collections.sort(restaurantList, costComparator)
                        dialog.dismiss()
                        recyclerAdapter.notifyDataSetChanged()
                    }
                    3 -> {
                        Collections.sort(restaurantList, costComparator)
                        restaurantList.reverse()
                        dialog.dismiss()
                        recyclerAdapter.notifyDataSetChanged()
                    }
                }
            }
            dialogBox.create()
            dialogBox.show()

        }

        return super.onOptionsItemSelected(item)
    }


    fun checkNetwork(): Boolean {
        return ConnectionManager().checkConnectivity(activity as Context)
    }


}