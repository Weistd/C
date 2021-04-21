package com.example.covid19tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        //setting up the title to actionbar
        var actionBar = getSupportActionBar()
        //set the tile of this page
        setTitle("About")
        //create the back button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        };
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
        };
    }

//when the back button was touch close current page and return to the parent page
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

}