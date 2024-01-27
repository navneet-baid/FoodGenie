package com.navneet.foodgenie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cart")
data class CartEntity(
    @PrimaryKey val item_id: Int,
    @ColumnInfo(name = "name") val dishName: String,
    @ColumnInfo(name = "price") val dishPrice: String,
    @ColumnInfo(name = "res_id") val resId: String
)