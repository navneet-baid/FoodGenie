package com.navneet.foodgenie.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {
    @Insert
    fun inserItem(cartEntity: CartEntity)

    @Delete
    fun deleteItem(cartEntity: CartEntity)

    @Query("SELECT * FROM Cart")
    fun getAllItems(): List<CartEntity>

    @Query("SELECT * FROM Cart WHERE item_id = :itemId AND res_id=:res_id")
    fun getDishById(itemId: String, res_id: String): CartEntity

    @Query("SELECT COUNT(item_id) FROM Cart")
    fun count(): Int

    @Query("SELECT * FROM Cart")
    fun selectAll(): List<CartEntity>

    @Query("DELETE FROM Cart")
    fun clear(): Void

    @Query("SELECT DISTINCT(res_id) FROM Cart")
    fun uniqueRestaurant(): String
}