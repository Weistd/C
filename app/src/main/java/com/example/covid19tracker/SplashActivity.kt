package com.example.covid19tracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed


class SplashActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //hide the actionbar to make the splash page constant with colors
        var actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.hide()
        };

        //set the delay to make the splashActivity page will last for 2 second
        Handler().postDelayed(Runnable {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

    }
}