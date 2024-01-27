package com.navneet.foodgenie.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestaurantEntity::class], version = 1, exportSchema = false)
abstract class RestaurantDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
}