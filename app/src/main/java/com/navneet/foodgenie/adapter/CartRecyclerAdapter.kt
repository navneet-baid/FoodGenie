package com.navneet.foodgenie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.navneet.foodgenie.R
import com.navneet.foodgenie.database.CartEntity
import com.navneet.foodgenie.model.OrderFoodList


class CartRecyclerAdapter(
    val context: Context,
    val foodList: List<CartEntity>,
    val progressLayout: RelativeLayout?
) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.dash_icon)
        var dishName: TextView = view.findViewById(R.id.dishName)
        var amount: TextView = view.findViewById(R.id.amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_list_single_row, parent, false)
        return CartRecyclerAdapter.CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = foodList[position]
        holder.dishName.text = item.dishName
        holder.amount.text = "Rs." + item.dishPrice
        progressLayout?.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}