package com.example.covid19tracker

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import org.json.JSONArray
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    //using to store the data fetch by findViewByid
    private lateinit var tv_confirmed: TextView;
    private lateinit var tv_confirmed_new:TextView;
    private lateinit var tv_active:TextView;
    private lateinit var tv_active_new:TextView;
    private lateinit var tv_recovered:TextView;
    private lateinit var tv_recovered_new:TextView;
    private lateinit var tv_death:TextView;
    private lateinit var tv_death_new: TextView;
    private lateinit var tv_tests:TextView;
    private lateinit var tv_tests_new:TextView;
    private lateinit var tv_date:TextView;
    private lateinit var tv_time:TextView;
    private var int_active_new = 0;
    private var int_active = 0;
    private var int_death_new = 0;
    private var int_death =1;
    //store the button obj
    private lateinit var lin_state_data: LinearLayout;
    private lateinit var lin_world_data:LinearLayout;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //initialize all text view in this function by findViewById
        Init();
        //Fetch data from API
        FetchSummaryData();
        //use the swipe fresh feature to reload the page and re-featch the data
        swipeRefreshLayout.setOnRefreshListener {
            FetchSummaryData()
            swipeRefreshLayout.isRefreshing = false
//            Toast.makeText(MainActivity.this, "Data refreshed!", Toast.LENGTH_SHORT).show();
        }
        //set the click listener to the state data button
        lin_state_data.setOnClickListener{
            val intent = Intent(this, StateWiseDataActivity::class.java);
            startActivity(intent);
        }
        //set the click listener to the worlld data button
        lin_world_data.setOnClickListener{
            val intent = Intent(this, WorldDataActivity::class.java);
            startActivity(intent);
        }


    }

    //in this function we will implment the request from covid 19 api
    //covid 19 api will provide the json array of the covid-19 data
    private fun FetchSummaryData() {

        ShowDialog(this);
        // covid-19 statistic  api
        //this api provides the covid-19 data in json format
        val url = "https://api.opencovid.ca"

        pieChart.clearChart();

        // Instantiate the cache
        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())
        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                //create the JSONArray var to store the array
                var summary: JSONArray;
//                    var testData_jsonArray: JSONArray;

                summary = response.getJSONArray("summary");

//                    testData_jsonArray = response.getJSONArray("tested");
                Log.d("summary", "what is wrong");
                //get the first object of the json array
                val data_canada = summary.getJSONObject(0);
//                    val test_data_india = testData_jsonArray.getJSONObject(testData_jsonArray.length() - 1)

                //Fetching data for India and storing it in String
                //comfirmed cases and the new comfirmed cases
                str_confirmed = data_canada.getString("cumulative_cases");
                str_confirmed_new = data_canada.getString("active_cases_change");

                //current active cases and the active cases change
                str_active = data_canada.getString("active_cases");
                str_active_new = data_canada.getString("active_cases_change");

                //total recovered cases and the recovered cases change
                str_recovered = data_canada.getString("cumulative_recovered");
                str_recovered_new = data_canada.getString("recovered");

                str_death = data_canada.getString("cumulative_deaths");
                str_death_new = data_canada.getString("deaths");

                //last update time
                str_last_update_time = data_canada.getString("date");
                //total test number and the test number change
                str_tests = data_canada.getString("cumulative_testing");
                str_tests_new = data_canada.getString("testing");

                str_last_update_time = data_canada.getString("date"); //Last update date and time

                //create a 1 second delay here to deal with the data
                val delay = Handler()
                delay.postDelayed({

                    //pass the strings to corresponding text view position
                    tv_confirmed.setText(
                        NumberFormat.getInstance().format(
                            Integer.parseInt(
                                str_confirmed
                            )
                        )
                    );
                    tv_confirmed_new.setText(
                        "+" + NumberFormat.getInstance().format(
                            Integer.parseInt(
                                str_confirmed_new
                            )
                        )
                    );

                    //set the total active data and the active case change  from api to the right position
                    tv_active.setText(
                        NumberFormat.getInstance().format(
                            Integer.parseInt(
                                str_active
                            )
                        )
                    );
                    tv_active_new.setText(
                        "+" + NumberFormat.getInstance().format(
                            Integer.parseInt(
                                str_active_new
                            )
                        )
                    );

                    //set the total recovered data and the recovered case change  from api to the right position
                    tv_recovered.setText(
                        NumberFormat.getInstance().format(str_recovered.toFloat())
                    );
                    tv_recovered_new.setText(
                        "+" + NumberFormat.getInstance().format(
                            str_recovered_new.toFloat()
                        )
                    );
                    //set the total death data and the death case change  from api to the right position
                    tv_death.setText(NumberFormat.getInstance().format(str_death.toFloat()));
                    tv_death_new.setText(
                        "+" + NumberFormat.getInstance().format(str_death_new.toFloat())
                    );
//                        //set the last update time data
//                        val pattern = "yyyy-MM-dd";
//                        var simpleDateFormat = SimpleDateFormat(pattern);
//                        var date: String = simpleDateFormat.format(str_last_update_time);
                    tv_time.setText(str_last_update_time);
//
//                        val pattern2 = "HH:mm"
//                        simpleDateFormat = SimpleDateFormat(pattern)
//                        date = simpleDateFormat.format(str_last_update_time)
//                        tv_time.setText(date);


                    tv_tests.setText(NumberFormat.getInstance().format(str_tests.toFloat()));
                    tv_tests_new.setText(
                        "+" + NumberFormat.getInstance().format(str_tests_new.toFloat())
                    );

                    //set the active slice in the pie chart to blue color and pasing in the number
                    pieChart.addPieSlice(
                        PieModel(
                            "Active", str_active.toFloat(), Color.parseColor(
                                "#007afe"
                            )
                        )
                    )
                    //set the active slice in the pie chart to green color and pasing in the number
                    pieChart.addPieSlice(
                        PieModel(
                            "Recovered", str_recovered.toFloat(), Color.parseColor(
                                "#08a045"
                            )
                        )
                    )
                    //set the active slice in the pie chart to red color and pasing in the number
                    pieChart.addPieSlice(
                        PieModel(
                            "Deceased", str_death.toFloat(), Color.parseColor(
                                "#F6404F"
                            )
                        )
                    )
                    //start the pie char animation
                    pieChart.startAnimation();

                    DismissDialog();


                }, 1000);
            },
            Response.ErrorListener { error ->
                // TODO: Handle error
            }
        )

        requestQueue.add(jsonObjectRequest);

    }

    //initialize all t he view variable by corresponding findViewById
    private fun Init() {
        tv_confirmed = findViewById(R.id.activity_main_confirmed_textview);
        tv_confirmed_new = findViewById(R.id.activity_main_confirmed_new_textview);
        tv_active = findViewById(R.id.activity_main_active_textview);
        tv_active_new = findViewById(R.id.activity_main_active_new_textview);
        tv_recovered = findViewById(R.id.activity_main_recovered_textview);
        tv_recovered_new = findViewById(R.id.activity_main_recovered_new_textview);
        tv_death = findViewById(R.id.activity_main_death_textview);
        tv_death_new = findViewById(R.id.activity_main_death_new_textview);
        tv_tests = findViewById(R.id.activity_main_samples_textview);
        tv_tests_new = findViewById(R.id.activity_main_samples_new_textview);
        tv_date = findViewById(R.id.activity_main_date_textview);
        tv_time = findViewById(R.id.activity_main_time_textview);

        pieChart=findViewById(R.id.activity_main_piechart);
        swipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        lin_state_data = findViewById(R.id.activity_main_statewise_lin);
        lin_world_data = findViewById(R.id.activity_main_world_data_lin);



    }

    //adding the loading dialog
    public fun ShowDialog(context: Context) {
        progressDialog = ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);


    }
    //close the loading dialog
    public fun DismissDialog() {
        progressDialog.dismiss()
    }



    //overide will run once the activity started
    //load the menu icon to the main page
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater;
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
if(item.itemId==R.id.menu){
//    Toast.makeText(applicationContext, "Menu icon clicked", Toast.LENGTH_SHORT).show();
    startActivity(Intent(this@MainActivity, AboutActivity::class.java))
}

        return super.onOptionsItemSelected(item);

    }

    //using this method to prevent the accident touch to the back button
    //only two quick touch will make the back button work
    //prevent accident touch on the back button
    lateinit var backToast:Toast;
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            backToast.cancel()
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        backToast = Toast.makeText(this, "double click to exit program", Toast.LENGTH_SHORT)
        backToast.show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 3000)
    }

}