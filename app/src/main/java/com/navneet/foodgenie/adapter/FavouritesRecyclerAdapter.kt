package com.navneet.foodgenie.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.navneet.foodgenie.R
import com.navneet.foodgenie.activity.Restaurant_menu_activity
import com.navneet.foodgenie.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavouritesRecyclerAdapter(val context: Context, val restaurantList: List<RestaurantEntity>) :
    RecyclerView.Adapter<FavouritesRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestName: TextView = view.findViewById(R.id.txtFavResTitle)
        val txtRestCost: TextView = view.findViewById(R.id.txtFavResCost)
        val txtRestRating: TextView = view.findViewById(R.id.txtFavResRating)
        val imgRestImage: ImageView = view.findViewById(R.id.imgFavRestImage)
        val llContent: LinearLayout = view.findViewById(R.id.llFavContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourite_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.txtRestName.text = restaurant.Name
        holder.txtRestRating.text = restaurant.Rating
        holder.txtRestCost.text = restaurant.Price
        Picasso.get().load(restaurant.Image).error(R.drawable.food_genie_logo)
            .into(holder.imgRestImage)
        holder.llContent.setOnClickListener {
            val intent = Intent(context, Restaurant_menu_activity::class.java)
            val restaurant_id = restaurant.restaurant_id.toString()
            intent.putExtra("restaurant_id", restaurant_id)
            intent.putExtra("restaurant_name", restaurant.Name)
            intent.putExtra("restaurant_cost_for_one", restaurant.Price)
            intent.putExtra("rating", restaurant.Rating)
            intent.putExtra("image_url", restaurant.Image)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

}