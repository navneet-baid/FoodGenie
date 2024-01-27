package com.navneet.foodgenie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView
import com.navneet.foodgenie.R
import com.navneet.foodgenie.fragment.*


class HomePage : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var sideBarNavigationView: NavigationView
    var previousMenuItem: MenuItem? = null
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#FF2F2F")
        }


        toolbar = findViewById(R.id.toolBar)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        sideBarNavigationView = findViewById(R.id.navigationView)
        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        setUpActionBar()
        openHome()
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@HomePage,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )


        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        sideBarNavigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when (it.itemId) {
                R.id.sideBarHome -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.sideBarFav -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouritesFragment())
                        .commit()
                    supportActionBar?.title = "Favourites"
                    drawerLayout.closeDrawers()
                }
                R.id.sideBarOrderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, MyOrderFragment())
                        .commit()
                    supportActionBar?.title = "My Orders"
                    drawerLayout.closeDrawers()
                }
                R.id.sideBarFaq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FAQFragment())
                        .commit()
                    supportActionBar?.title = "Frequently Asked Question"
                    drawerLayout.closeDrawers()
                }
                R.id.sideBarProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, ProfileFragment())
                        .commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.sideBarLogout -> {
                    val dialog = AlertDialog.Builder(this@HomePage)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to logout")
                    dialog.setPositiveButton("Yes") { text, listener ->
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this@HomePage, LoginActivity::class.java)
                        intent.putExtra("mobile_number", "")
                        startActivity(intent)
                        this@HomePage?.finish()
                    }
                    dialog.setNegativeButton("No") { text, listener ->

                    }
                    dialog.setCancelable(false)
                    dialog.create()
                    dialog.show()
                }
                R.id.sideBarLogIn -> {
                    val intent = Intent(this@HomePage, LoginActivity::class.java)
                    intent.putExtra("mobile_number", "")
                    intent.putExtra("forced_login", "true")
                    startActivity(intent)
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val nav_menu: Menu = sideBarNavigationView.menu
        val nav_header: View = sideBarNavigationView.getHeaderView(0)
        val nav_username: TextView = nav_header.findViewById(R.id.welcome_username)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val username = sharedPreferences.getString("user_name", null)
            nav_username.text = "$username"
            nav_menu.findItem(R.id.sideBarLogout).isVisible = true
            nav_menu.findItem(R.id.sideBarLogIn).isVisible = false

        } else {
            nav_menu.findItem(R.id.sideBarLogout).isVisible = false
            nav_menu.findItem(R.id.sideBarLogIn).isVisible = true
        }
        return true
    }

    fun setUpActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Toolbar Title"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        if (id == R.id.icTopFav) {
            val nav_menu: Menu = sideBarNavigationView.menu
            nav_menu.findItem(R.id.sideBarFav).isChecked = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, FavouritesFragment())
                .commit()
            supportActionBar?.title = "Favourites"
        }
        if (id == R.id.iconTopCart) {
            startActivity(Intent(this@HomePage, CartActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHome() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, AllrestaurantFragment())
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        sideBarNavigationView.setCheckedItem(R.id.sideBarHome)
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        val drawer: DrawerLayout = findViewById(R.id.drawerLayout)
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers()
            return
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers()
            }
        }
        when (frag) {
            !is AllrestaurantFragment -> openHome()
            else -> {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed()
                    return
                }
                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }


}
