package com.navneet.foodgenie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavouriteRestaurants")
data class RestaurantEntity(
    @PrimaryKey val restaurant_id: Int,
    @ColumnInfo(name = "name") val Name: String,
    @ColumnInfo(name = "cost_for_one") val Price: String,
    @ColumnInfo(name = "rating") val Rating: String,
    @ColumnInfo(name = "image") val Image: String
)