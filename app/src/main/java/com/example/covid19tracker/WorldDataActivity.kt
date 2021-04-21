package com.example.covid19tracker

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.text.NumberFormat


class WorldDataActivity : AppCompatActivity() {
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
    private lateinit var tv_date: TextView;
    private lateinit var tv_time: TextView;
    private var int_active_new = 0;
    private var int_active = 0;
    private var int_death_new = 0;
    private var int_death =1;
    //store the button obj
    private lateinit var lin_countrywise: LinearLayout;
//    private lateinit var lin_world_data: LinearLayout;

    //store the swipeRefreshLayout and pie chart
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout;
    private lateinit var pieChart: PieChart;

    //stores information fetch from api in string format
    private lateinit var str_confirmed: String;
    private lateinit var str_confirmed_new:kotlin.String;
    private lateinit var str_active:kotlin.String;
    private lateinit var str_active_new:kotlin.String;
    private lateinit var str_recovered:kotlin.String;
    private lateinit var str_recovered_new:kotlin.String;
    private lateinit var str_death: String;
    private lateinit var str_death_new:kotlin.String;
    private lateinit var str_tests:kotlin.String;
    private lateinit var str_tests_new:kotlin.String;
    private lateinit var str_last_update_time:kotlin.String;
    //progress dialog
    private lateinit var progressDialog: ProgressDialog;
    private var doubleBackToExitPressedOnce = false;




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_data)

        //create the action bar varibale
        var actionBar = getSupportActionBar()
        setTitle("Covid19(World Data)")
        //set the back button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        };
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
        };
        //initalize all the variable
        Init();
        //featch the data from the statewise model
        FetchWorldData();

        swipeRefreshLayout.setOnRefreshListener {
            FetchWorldData()
            swipeRefreshLayout.isRefreshing = false
            //Toast.makeText(MainActivity.this, "Data refreshed!", Toast.LENGTH_SHORT).show();
        }

        lin_countrywise.setOnClickListener{
            Toast.makeText(applicationContext, "World Data", Toast.LENGTH_SHORT).show();
        }

    }
    //fetch the data from api
    private fun FetchWorldData() {
        ShowDialog(this);
        // covid-19 statistic  api
        //this api provides the covid-19 data in json format
        val url = "https://corona.lmao.ninja/v2/all"

        pieChart.clearChart();

        // Instantiate the cache
        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())
        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }

        //create the request
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                str_confirmed = response.getString("cases");
                str_confirmed_new = response.getString("todayCases");
                str_active = response.getString("active");
                str_recovered = response.getString("recovered");
                str_recovered_new = response.getString("todayRecovered");
                str_death = response.getString("deaths");
                str_death_new = response.getString("todayDeaths");
                str_tests = response.getString("tests");

                //create a 1 second delay here to deal with the data
                val delay = Handler()
                delay.postDelayed({
                    // setting up texted in the text view
                    // setting up texted in the text view
                    tv_confirmed.text =
                        NumberFormat.getInstance().format(str_confirmed.toInt().toLong())
                    tv_confirmed_new.text =
                        "+" + NumberFormat.getInstance().format(str_confirmed_new.toInt().toLong())

                    tv_active.text = NumberFormat.getInstance().format(str_active.toInt().toLong())

                    int_active_new =
                        str_confirmed_new.toInt() - (str_recovered_new.toInt() + str_death_new.toInt())
                    tv_active_new.text = "+" + NumberFormat.getInstance().format(int_active_new)

                    tv_recovered.text =
                        NumberFormat.getInstance().format(str_recovered.toInt().toLong())
                    tv_recovered_new.text =
                        "+" + NumberFormat.getInstance().format(str_recovered_new.toInt().toLong())

                    tv_death.text = NumberFormat.getInstance().format(str_death.toInt().toLong())
                    tv_death_new.text =
                        "+" + NumberFormat.getInstance().format(str_death_new.toInt().toLong())

                    tv_tests.text = NumberFormat.getInstance().format(str_tests.toInt().toLong())

                    pieChart.addPieSlice(
                        PieModel(
                            "Active",
                            str_active.toInt().toFloat(), Color.parseColor("#007afe")
                        )
                    )
                    pieChart.addPieSlice(
                        PieModel(
                            "Recovered",
                            str_recovered.toInt().toFloat(), Color.parseColor("#08a045")
                        )
                    )
                    pieChart.addPieSlice(
                        PieModel(
                            "Deceased",
                            str_death.toInt().toFloat(), Color.parseColor("#F6404F")
                        )
                    )

                    pieChart.startAnimation()

                    DismissDialog()
                }, 1000);
            },
            Response.ErrorListener { error ->
                // TODO: Handle error
            }
        )

        requestQueue.add(jsonObjectRequest);





    }

    private fun Init() {
        tv_confirmed = findViewById(R.id.activity_world_data_confirmed_textView);
        tv_confirmed_new = findViewById(R.id.activity_world_data_confirmed_new_textView);
        tv_active = findViewById(R.id.activity_world_data_active_textView);
        tv_active_new = findViewById(R.id.activity_world_data_active_new_textView);
        tv_recovered = findViewById(R.id.activity_world_data_recovered_textView);
        tv_recovered_new = findViewById(R.id.activity_world_data_recovered_new_textView);
        tv_death = findViewById(R.id.activity_world_data_death_textView);
        tv_death_new = findViewById(R.id.activity_world_data_death_new_textView);
        tv_tests = findViewById(R.id.activity_world_data_tests_textView);
        swipeRefreshLayout = findViewById(R.id.activity_world_data_swipe_refresh_layout);
        pieChart = findViewById(R.id.activity_world_data_piechart);
        lin_countrywise = findViewById(R.id.activity_world_data_countrywise_lin);



    }

    private fun ShowDialog(context: Context) {
        progressDialog = ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);


    }
    fun DismissDialog() {
        progressDialog.dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }



}