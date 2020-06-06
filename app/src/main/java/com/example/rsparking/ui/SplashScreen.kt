package com.example.rsparking.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.rsparking.R

class SplashScreen: AppCompatActivity() {
    private lateinit var mLogo:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_remote_stand_splash)
        mLogo = findViewById(R.id.splash_logo)
        mLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_in))
        startAnimation()
    }

    private fun startAnimation() {
        Handler().postDelayed({
            mLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_out))
            Handler().postDelayed({
                mLogo.visibility = View.GONE
                startActivity(Intent(this, LoginScreen::class.java))
                finish()
            },500)
        }, 2500)
    }

    override fun onDestroy() {
        super.onDestroy()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}