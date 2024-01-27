package com.navneet.foodgenie.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CartEntity::class], version = 1, exportSchema = false)
abstract class CartDatabase : RoomDatabase(){
    abstract fun cartDao():CartDao
}