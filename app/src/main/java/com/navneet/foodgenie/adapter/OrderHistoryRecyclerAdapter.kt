package com.navneet.foodgenie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.navneet.foodgenie.R
import com.navneet.foodgenie.model.OrderFoodList
import com.navneet.foodgenie.model.foodItems
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class OrderHistoryRecyclerAdapter(val context: Context, val orderHistoryList: List<foodItems>) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {
    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantName: TextView = view.findViewById(R.id.txtRestName)
        val date: TextView = view.findViewById(R.id.txtDate)
        val orderId: TextView = view.findViewById(R.id.txtOrderId)
        val total: TextView = view.findViewById(R.id.txtTotal)
        val orderHistoryItemsRecycler: RecyclerView = view.findViewById(R.id.recyclerOrderItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history_single_row, parent, false)
        return OrderHistoryRecyclerAdapter.OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val orders = orderHistoryList[position]
        var cost = 0
        for (i in 0 until orders.foodDetails.length()) {
            val foodJson = orders.foodDetails.getJSONObject(i)
            cost += foodJson.getString("cost").toInt()
        }
        holder.restaurantName.text = "ORDER FROM: " + orders.restaurant_name
        holder.date.text = "ORDER DATE: " + formatDate(orders.placed_at)
        holder.orderId.text = "ORDER ID: #" + orders.order_id
        holder.total.text = "TOTAL AMOUNT: Rs. $cost"
        holder.orderHistoryItemsRecycler.visibility = View.GONE
        setUpRecycler(holder.orderHistoryItemsRecycler, orders)
        holder.restaurantName.setOnClickListener {
            if (holder.orderHistoryItemsRecycler.visibility == View.GONE) {
                holder.orderHistoryItemsRecycler.visibility = View.VISIBLE
            } else {
                holder.orderHistoryItemsRecycler.visibility = View.GONE
            }

        }
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    private fun setUpRecycler(recyclerResHistory: RecyclerView, orderHistoryList: foodItems) {
        val foodItemsList = ArrayList<OrderFoodList>()
        for (i in 0 until orderHistoryList.foodDetails.length()) {
            val foodJson = orderHistoryList.foodDetails.getJSONObject(i)
            foodItemsList.add(
                OrderFoodList(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost")
                )
            )
        }
        val orderHistoryItemsAdapter = OrderHistoryItemsAdapter(context, foodItemsList)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerResHistory.layoutManager = mLayoutManager
        recyclerResHistory.itemAnimator = DefaultItemAnimator()
        recyclerResHistory.adapter = orderHistoryItemsAdapter
    }

    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }
}