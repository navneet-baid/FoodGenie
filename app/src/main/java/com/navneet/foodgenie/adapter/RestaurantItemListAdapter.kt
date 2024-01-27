package com.navneet.foodgenie.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.navneet.foodgenie.activity.CartActivity
import com.navneet.foodgenie.activity.LoginActivity
import com.navneet.foodgenie.R
import com.navneet.foodgenie.database.CartDatabase
import com.navneet.foodgenie.database.CartEntity
import com.navneet.foodgenie.model.Menu

class RestaurantItemListAdapter(
    val context: Context,
    val menuList: ArrayList<Menu>,
    val btnPlaceOrder: Button,
    val restaurant_id: String,
    val cartEntity: ArrayList<CartEntity>,
    val progressLayout: RelativeLayout
) :
    RecyclerView.Adapter<RestaurantItemListAdapter.MenuViewHolder>() {
    var cost = 0
    lateinit var sharedPreferences: SharedPreferences

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtMrp: TextView = view.findViewById(R.id.txtItemMrp)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
        val txtSerialNo: TextView = view.findViewById(R.id.txtSerialNo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_restaurant_item_single_row, parent, false)
        sharedPreferences =
            view.context.getSharedPreferences("FoodGenie Preference file", Context.MODE_PRIVATE)

        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuList[position]
        val cartEntityList: CartEntity = cartEntity[position]
        val serialno = position + 1

        val price = menuItem.price
        holder.txtDishName.text = menuItem.name
        holder.txtMrp.text = "₹ $price"
        holder.txtSerialNo.text = serialno.toString()
        if (CartDbasyncTask(context, cartEntityList, 1).execute().get()) {
            holder.btnAddToCart.setBackgroundColor(
                holder.btnAddToCart.resources.getColor(
                    R.color.remove_from_cart
                )
            )
            holder.btnAddToCart.text = "Remove"
        } else {
            holder.btnAddToCart.setBackgroundColor(
                holder.btnAddToCart.resources.getColor(
                    R.color.add_to_cart
                )
            )
            holder.btnAddToCart.text = "Add  to Cart"
        }
        holder.btnAddToCart.setOnClickListener {
            if (getResId(context).execute().get() != menuItem.resId && (getResId(context).execute()
                    .get() != null)
            ) {
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle("Update Cart")
                dialog.setMessage("Do you want to update your cart? Previous restaurant will be changed")
                dialog.setPositiveButton("Yes") { text, listener ->
                    CartDbasyncTask(context, cartEntityList, 6).execute().get()
                    if (!CartDbasyncTask(context, cartEntityList, 1).execute().get()) {
                        val async =
                            CartDbasyncTask(context, cartEntityList, 2).execute()
                        val result = async.get()
                        if (result) {
                            cost += menuItem.price.toInt()
                            holder.btnAddToCart.setBackgroundColor(
                                holder.btnAddToCart.resources.getColor(
                                    R.color.remove_from_cart
                                )
                            )
                            holder.btnAddToCart.text = "Remove"
                        } else {
                            Toast.makeText(
                                context,
                                "Some error has been occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val async =
                            CartDbasyncTask(context, cartEntityList, 3).execute()
                        val result = async.get()
                        if (result) {
                            cost -= menuItem.price.toInt()
                            holder.btnAddToCart.setBackgroundColor(
                                holder.btnAddToCart.resources.getColor(
                                    R.color.add_to_cart
                                )
                            )
                            holder.btnAddToCart.text = "Add  to Cart"
                        } else {
                            Toast.makeText(
                                context,
                                "Some error has been occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                dialog.setNegativeButton("No") { _, _ ->
                }
                dialog.setCancelable(false)
                dialog.create()
                dialog.show()
                if (CartDbasyncTask(context, cartEntityList, 4).execute().get()) {
                    btnPlaceOrder.text = "Proceed to cart"
                    btnPlaceOrder.visibility = View.VISIBLE
                } else {
                    cost = 0
                    btnPlaceOrder.visibility = View.GONE
                }
            } else {
                if (!CartDbasyncTask(context, cartEntityList, 1).execute().get()) {
                    val async =
                        CartDbasyncTask(context, cartEntityList, 2).execute()
                    val result = async.get()
                    if (result) {
                        cost += menuItem.price.toInt()
                        holder.btnAddToCart.setBackgroundColor(
                            holder.btnAddToCart.resources.getColor(
                                R.color.remove_from_cart
                            )
                        )
                        holder.btnAddToCart.text = "Remove"
                    } else {
                        Toast.makeText(
                            context,
                            "Some error has been occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val async =
                        CartDbasyncTask(context, cartEntityList, 3).execute()
                    val result = async.get()
                    if (result) {
                        cost -= menuItem.price.toInt()
                        holder.btnAddToCart.setBackgroundColor(
                            holder.btnAddToCart.resources.getColor(
                                R.color.add_to_cart
                            )
                        )
                        holder.btnAddToCart.text = "Add  to Cart"
                    } else {
                        Toast.makeText(
                            context,
                            "Some error has been occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                if (CartDbasyncTask(context, cartEntityList, 4).execute().get()) {
                    btnPlaceOrder.text = "Proceed to cart"
                    btnPlaceOrder.visibility = View.VISIBLE
                } else {
                    cost = 0
                    btnPlaceOrder.visibility = View.GONE
                }
            }
        }
        if (CartDbasyncTask(context, cartEntityList, 4).execute().get()) {
            btnPlaceOrder.text = "Proceed to cart"
            btnPlaceOrder.visibility = View.VISIBLE
        } else {
            cost = 0
            btnPlaceOrder.visibility = View.GONE
        }

        btnPlaceOrder.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            context.startActivity(intent)
        }
        progressLayout.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class CartDbasyncTask(val context: Context, val cartEntity: CartEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db =
            Room.databaseBuilder(context, CartDatabase::class.java, "cart-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val item: CartEntity? = db.cartDao()
                        .getDishById(cartEntity.item_id.toString(), cartEntity.resId.toString())
                    db.close()
                    return item != null
                }
                2 -> {
                    db.cartDao().inserItem(cartEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.cartDao().deleteItem(cartEntity)
                    db.close()
                    return true
                }
                4 -> {
                    val count = db.cartDao().count()
                    db.close()
                    println("total=$count")
                    return count > 0
                }
                5 -> {
                    db.cartDao().selectAll()
                    db.close()
                }
                6 -> {
                    db.cartDao().clear()
                    db.close()
                }
            }
            return false
        }
    }

    class getResId(val context: Context) :
        AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String {
            val db =
                Room.databaseBuilder(context, CartDatabase::class.java, "cart-db")
                    .build()
            return db.cartDao().uniqueRestaurant()
        }

    }
}