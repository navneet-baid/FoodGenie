package com.navneet.foodgenie.model

import org.json.JSONArray

data class foodItems(
    val order_id: Int,
    val restaurant_name: String,
    val placed_at: String,
    val foodDetails: JSONArray
)