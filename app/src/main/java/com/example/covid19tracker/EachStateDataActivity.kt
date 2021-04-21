package com.example.covid19tracker

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.covid19tracker.Constants.STATE_ACTIVE
import com.example.covid19tracker.Constants.STATE_CONFIRMED
import com.example.covid19tracker.Constants.STATE_CONFIRMED_NEW
import com.example.covid19tracker.Constants.STATE_DEATH
import com.example.covid19tracker.Constants.STATE_DEATH_NEW
import com.example.covid19tracker.Constants.STATE_LAST_UPDATE
import com.example.covid19tracker.Constants.STATE_NAME
import com.example.covid19tracker.Constants.STATE_RECOVERED
import com.example.covid19tracker.Constants.STATE_RECOVERED_NEW
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class EachStateDataActivity : AppCompatActivity() {

    //using to store the data fetch by findViewByid
    private lateinit var tv_confirmed: TextView;
    private lateinit var tv_confirmed_new: TextView;
    private lateinit var tv_active: TextView;
    private lateinit var tv_active_new: TextView;
    private lateinit var tv_recovered: TextView;
    private lateinit var tv_recovered_new: TextView;
    private lateinit var tv_death: TextView;
    private lateinit var tv_death_new: TextView;
    private lateinit var tv_tests: TextView;
    private lateinit var tv_tests_new: TextView;
    private lateinit var tv_lastupdatedate: TextView;
    private lateinit var tv_dist: TextView;

    //store the swipeRefreshLayout and pie chart
    private lateinit var lin_district: LinearLayout;
    private lateinit var pieChart: PieChart;

    //stores information fetch from api in string format
    private lateinit var str_confirmed: String;
    private lateinit var str_confirmed_new:kotlin.String;
    private lateinit var str_active:kotlin.String;
    private lateinit var str_stateName:kotlin.String;
    private lateinit var str_recovered:kotlin.String;
    private lateinit var str_recovered_new:kotlin.String;
    private lateinit var str_death: String;
    private lateinit var str_death_new:kotlin.String;
    private lateinit var str_tests:kotlin.String;
    private lateinit var str_tests_new:kotlin.String;
    private lateinit var str_lastupdatedate:kotlin.String;
    //progress dialog
    private lateinit var progressDialog: ProgressDialog;





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.covid19tracker.R.layout.activity_each_state_data)
        //get the data passed to this activity

        GetIntent();
//        println("each state str_confirmed=")

        //set tile for the page
        var actionBar = getSupportActionBar()
        setTitle(str_stateName);

        //back menu icon on toolbar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        };
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
        };

        //Initialise all textviews
        Init();
        //load the corresponding province data into the each state data activity
        LoadStateData();
//set the on click event listener to watch the click
        //once the button got click it will show the province name of the current page
        lin_district.setOnClickListener{
//            val intent = Intent(this, StateWiseDataActivity::class.java);
//            startActivity(intent);
            Toast.makeText(applicationContext,"$str_stateName",Toast.LENGTH_SHORT).show()
        }



    }

    //get all the text view into the variable
    private fun Init() {
        tv_confirmed = findViewById(com.example.covid19tracker.R.id.activity_each_state_confirmed_textView)
        tv_confirmed_new = findViewById(com.example.covid19tracker.R.id.activity_each_state_confirmed_new_textView)
        tv_active = findViewById(com.example.covid19tracker.R.id.activity_each_state_active_textView)
        tv_active_new = findViewById(com.example.covid19tracker.R.id.activity_each_state_active_new_textView)
        tv_recovered = findViewById(com.example.covid19tracker.R.id.activity_each_state_recovered_textView)
        tv_recovered_new = findViewById(com.example.covid19tracker.R.id.activity_each_state_recovered_new_textView)
        tv_death = findViewById(com.example.covid19tracker.R.id.activity_each_state_death_textView)
        tv_death_new = findViewById(com.example.covid19tracker.R.id.activity_each_state_death_new_textView)
        tv_lastupdatedate = findViewById(com.example.covid19tracker.R.id.activity_each_state_lastupdate_textView)
        tv_dist = findViewById(com.example.covid19tracker.R.id.activity_each_state_district_data_title)
        pieChart = findViewById(com.example.covid19tracker.R.id.activity_each_state_piechart)
        lin_district = findViewById(com.example.covid19tracker.R.id.activity_each_state_lin)
    }

    //load the correct  data to the right textview
    private fun LoadStateData() {
        //Show dialog
        ShowDialog(this)
        val postDelayToshowProgress = Handler()
        postDelayToshowProgress.postDelayed(Runnable {

            //load strings to the corrsponding text view position
            tv_confirmed.setText(NumberFormat.getInstance().format(str_confirmed.toInt()))
            tv_confirmed_new.text =
                "+" + NumberFormat.getInstance().format(str_confirmed_new.toInt())
            tv_active.setText(NumberFormat.getInstance().format(str_active.toInt()))
            val int_active_new =
                str_confirmed_new.toInt() - (str_recovered_new.toInt() + str_death_new.toInt())
            tv_active_new.text = "+" + NumberFormat.getInstance()
                .format(if (int_active_new < 0) 0 else int_active_new)
            tv_death.setText(NumberFormat.getInstance().format(str_death.toInt()))
            tv_death_new.text = "+" + NumberFormat.getInstance().format(str_death_new.toInt())
            tv_recovered.setText(NumberFormat.getInstance().format(str_recovered.toInt()))
            tv_recovered_new.text =
                "+" + NumberFormat.getInstance().format(str_recovered_new.toInt())

            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val calendar = Calendar.getInstance()
            // Move calendar to yesterday
            calendar.add(Calendar.DATE, -1)
            val yesterday = calendar.time
            val currentDate = sdf.format(yesterday).toString()
            tv_lastupdatedate.text = currentDate
            tv_dist.text = "$str_stateName"

            //setting piechart
            pieChart.addPieSlice(
                PieModel(
                    "Active",
                    str_active.toInt().toFloat(),
                    Color.parseColor("#007afe")
                )
            )
            pieChart.addPieSlice(
                PieModel(
                    "Recovered",
                    str_recovered.toInt().toFloat(),
                    Color.parseColor("#08a045")
                )
            )
            pieChart.addPieSlice(
                PieModel(
                    "Deceased",
                    str_death.toInt().toFloat(),
                    Color.parseColor("#F6404F")
                )
            )
            pieChart.startAnimation()
            DismissDialog()
        }, 1000)
    }

    //get the data passing from the stateWiseAdapter and load into variables
    private fun GetIntent() {
        val intent = intent
        str_stateName = intent.getStringExtra(STATE_NAME).toString()
        str_confirmed = intent.getStringExtra(STATE_CONFIRMED).toString()
        str_confirmed_new = intent.getStringExtra(STATE_CONFIRMED_NEW).toString()
        str_active = intent.getStringExtra(STATE_ACTIVE).toString()
        str_death = intent.getStringExtra(STATE_DEATH).toString()
        str_death_new = intent.getStringExtra(STATE_DEATH_NEW).toString()
        str_recovered = intent.getStringExtra(STATE_RECOVERED).toString()
        str_recovered_new = intent.getStringExtra(STATE_RECOVERED_NEW).toString()
        str_lastupdatedate = intent.getStringExtra(STATE_LAST_UPDATE).toString()
    }

    //set the loading processs
    public fun ShowDialog(context: Context) {
        progressDialog = ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);


    }
    //close the loading page
    public fun DismissDialog() {
        progressDialog.dismiss()
    }

    //once the back button being pressed close current page and reutrn to the parent page
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

}