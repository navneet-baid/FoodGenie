package com.navneet.foodgenie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.navneet.foodgenie.R
import com.navneet.foodgenie.model.OrderFoodList

class OrderHistoryItemsAdapter(val context: Context, val orderList: List<OrderFoodList>) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.dash_icon)
        var dishName: TextView = view.findViewById(R.id.dishName)
        var amount: TextView = view.findViewById(R.id.amount)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartRecyclerAdapter.CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_list_single_row, parent, false)
        return CartRecyclerAdapter.CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartRecyclerAdapter.CartViewHolder, position: Int) {
        val item = orderList[position]
        holder.dishName.text = item.name
        holder.amount.text = "Rs." + item.cost
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}