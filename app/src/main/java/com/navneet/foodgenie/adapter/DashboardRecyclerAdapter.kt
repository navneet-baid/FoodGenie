@file:Suppress("DEPRECATION")

package com.navneet.foodgenie.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.room.Room
import com.navneet.foodgenie.R
import com.navneet.foodgenie.activity.Restaurant_menu_activity
import com.navneet.foodgenie.database.RestaurantDatabase
import com.navneet.foodgenie.database.RestaurantEntity
import com.navneet.foodgenie.model.Restaurants
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(
    val context: Context,
    val restaurantList: ArrayList<Restaurants>,
    val restaurantEntityList: ArrayList<RestaurantEntity>
) :
    Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtCostForOne: TextView = view.findViewById(R.id.txtCostForOne)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val fav_icon: ImageView = view.findViewById(R.id.ic_fav)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val restaurantEntity: RestaurantEntity = restaurantEntityList[position]
        val restaurant = restaurantList[position]
        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val is_fav = checkFav.get()

        if (is_fav) {
            holder.fav_icon.setImageResource(R.drawable.ic_fav_tab)
        } else {
            holder.fav_icon.setImageResource(R.drawable.ic_fav)
        }
        val price = restaurant.cost_for_one
        holder.txtRestaurantName.text = restaurant.resaturant_name
        holder.txtCostForOne.text = "₹ $price/person"
        holder.txtRestaurantRating.text = restaurant.rating
        Picasso.get().load(restaurant.image).error(R.drawable.food_genie_logo)
            .into(holder.imgRestaurantImage)
        holder.llContent.setOnClickListener {
            val intent = Intent(context, Restaurant_menu_activity::class.java)
            intent.putExtra("restaurant_id", restaurant.resaturant_id)
            intent.putExtra("restaurant_name", restaurant.resaturant_name)
            intent.putExtra("restaurant_cost_for_one", restaurant.cost_for_one)
            intent.putExtra("rating", restaurant.rating)
            intent.putExtra("image_url", restaurant.image)
            context.startActivity(intent)
        }



        holder.fav_icon.setOnClickListener {
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Some error has been occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                holder.fav_icon.setImageResource(R.drawable.ic_fav_tab)
            } else {
                val async =
                    DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Some error has been occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                holder.fav_icon.setImageResource(R.drawable.ic_fav)
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }


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