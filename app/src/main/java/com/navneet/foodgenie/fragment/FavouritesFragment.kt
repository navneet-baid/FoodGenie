package com.navneet.foodgenie.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.navneet.foodgenie.R
import com.navneet.foodgenie.adapter.FavouritesRecyclerAdapter
import com.navneet.foodgenie.database.RestaurantDatabase
import com.navneet.foodgenie.database.RestaurantEntity

class FavouritesFragment : Fragment() {
    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouritesRecyclerAdapter
    var dbRestaurantList = listOf<RestaurantEntity>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        recyclerFavourite = view.findViewById(R.id.recyclerViewFavourites)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        layoutManager = GridLayoutManager(activity as Context, 2)
        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()
        if (dbRestaurantList.isNotEmpty()) {
            if (activity != null) {
                progressLayout.visibility = View.GONE
                recyclerAdapter = FavouritesRecyclerAdapter(activity as Context, dbRestaurantList)
                recyclerFavourite.adapter = recyclerAdapter
                recyclerFavourite.layoutManager = layoutManager
            }
        } else {
            progressLayout.visibility = View.GONE
            var toast: Toast =
                Toast.makeText(activity as Context, "Nothing in favourites", Toast.LENGTH_LONG)
            toast.show()

        }
        return view
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
            val db =
                Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db")
                    .build()
            return db.restaurantDao().getAllRestaurants()
        }

    }
}