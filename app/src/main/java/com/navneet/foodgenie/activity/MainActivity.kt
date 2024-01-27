package com.navneet.foodgenie.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.ImageView
import com.navneet.foodgenie.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.light_red)
        }
        setContentView(R.layout.splash_screen)
        val logo: ImageView = findViewById(R.id.splash_logo)
        logo.alpha = 0f
        logo.scaleX = 0f
        logo.scaleY = 0f
        logo.animate().apply {
            duration = 800
            alpha(1f)
            scaleX(1f)
            scaleY(1f)
        }
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("mobile_number", "")
            startActivity(intent)
            finish()
        }, 1500)
    }
}