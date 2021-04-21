package com.example.covid19tracker

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.example.covid19tracker.Adapters.StateWiseAdapter
import com.example.covid19tracker.Models.StateWiseModel
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StateWiseDataActivity : AppCompatActivity() {

    //create the variable for the later use
    private lateinit var rv_state_wise: RecyclerView;
    private lateinit var stateWiseAdapter: StateWiseAdapter;
    private lateinit var stateWiseModelArrayList: ArrayList<StateWiseModel>;
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout;
    private lateinit var et_search: EditText;

    //stores information fetch from api in string format
    private lateinit var str_province: String;
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
    //main activity object
    private val activity = MainActivity()
    //progress dialog
    private lateinit var progressDialog: ProgressDialog;
    private var doubleBackToExitPressedOnce = false;
    //
//    create arraylist to store the data fetched from the covid 19 api
    private val cases: MutableList<String> =ArrayList();
    private  val death: MutableList<String> = ArrayList();
    private  val recovered: MutableList<String> = ArrayList();
    private  var testing: MutableList<String> = ArrayList();
    private  var active: MutableList<String> = ArrayList();
    private  var ProvinceAndDate: MutableList<String> = ArrayList<String>();
    private  var temp: MutableList<String> = ArrayList<String>();
    //create the fetched data type for the api
    private val dataType = arrayOf("cases", "mortality", "recovered", "testing", "active");
    //create the arraylist store all data type needed from api
    private val featchDataItemList = arrayOf(
            "cases",
            "cumulative_cases",
            "deaths",
            "cumulative_deaths",
            "recovered",
            "cumulative_recovered",
            "testing",
            "cumulative_testing",
            "active_cases_change",
            "active_cases"
    );

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.covid19tracker.R.layout.activity_state_wise_data);

        //setting up the title to actionbar
        var actionBar = getSupportActionBar()
        //set the page tile to the Select Province
        setTitle("Select Province")
        // create the back button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        };
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
        };

        //inital all variables
        Init();
        //fetch the data from api and store it in order
       FetchStateData();
        //Setting swipe refresh layout
        swipeRefreshLayout.setOnRefreshListener {
            FetchStateData()
            swipeRefreshLayout.isRefreshing = false
            //Toast.makeText(MainActivity.this, "Data refreshed!", Toast.LENGTH_SHORT).show();
        }
        val search=findViewById<EditText>(com.example.covid19tracker.R.id.activity_state_wise_search_editText);
        //add text change listener to watch the change of the text in the search bar
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

    }
    //filter the word on the editbox and add the correponding item to the filteredList list and pass it to the stateWiseAdapter
    private fun Filter(text: String) {
        println("the search text="+text);
        println("SOMETHING");
        val filteredList: ArrayList<StateWiseModel> = ArrayList()
        for (item in stateWiseModelArrayList) {
            if (item.stateP.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        stateWiseAdapter.filterList(filteredList, text)
    }

    //initialize all the variables
    private fun Init() {
        swipeRefreshLayout = findViewById(com.example.covid19tracker.R.id.activity_state_wise_swipe_refresh_layout)
        et_search = findViewById(com.example.covid19tracker.R.id.activity_state_wise_search_editText)
        rv_state_wise = findViewById(com.example.covid19tracker.R.id.activity_state_wise_recyclerview)
        rv_state_wise.setHasFixedSize(true)
        rv_state_wise.layoutManager = LinearLayoutManager(this)
        stateWiseModelArrayList = ArrayList()
        stateWiseAdapter = StateWiseAdapter(this@StateWiseDataActivity, stateWiseModelArrayList)
        rv_state_wise.adapter = stateWiseAdapter
    }

    //fetch the data from api
    private fun FetchStateData() {

        //once the fetching process start show the loading page
        ShowDialog(this);
        //clear all the arraylist for the next api request
        cases.clear();
        death.clear();
        recovered.clear()
        testing.clear()
        active.clear()
        ProvinceAndDate.clear();
        // covid-19 statistic  api
        //this api provides the covid-19 data in json format
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        // Create a calendar object with today date. Calendar is in java.util pakage.
        val calendar = Calendar.getInstance()
        // Move calendar to yesterday
        calendar.add(Calendar.DATE, -1)
        val yesterday = calendar.time
        val currentDate = sdf.format(yesterday).toString()
        System.out.println(" C DATE is  " + currentDate)

        // Instantiate the cache
        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())
        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        //for loop used to generate different api request url
        for(i in 0..dataType.size-1) {
            val url1 = "https://api.opencovid.ca/timeseries?stat=";
            val dataType = dataType[i];
            val url2 = "&loc=prov&after="
            val url = url1 + dataType + url2 + currentDate;
            System.out.println(" url " + url)

            //create the request to the api

            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                    Response.Listener { response ->
                        //if the specified data type is the cases

                        var jsonArray: JSONArray;
                        var DataType: String = "cases";
                        DataType = dataType;
                        System.out.println(" DataType " + DataType)
                        jsonArray = response.getJSONArray(DataType);
                        var seatchItem1: String = "";
                        var seatchItem2: String = "";
                        if (i == 0) {
                            seatchItem1 = featchDataItemList[0]
                            seatchItem2 = featchDataItemList[1]
                        } else if (i == 1) {
                            seatchItem1 = featchDataItemList[2]
                            seatchItem2 = featchDataItemList[3]
                        } else if (i == 2) {
                            seatchItem1 = featchDataItemList[4]
                            seatchItem2 = featchDataItemList[5]
                        } else if (i == 3) {
                            seatchItem1 = featchDataItemList[6]
                            seatchItem2 = featchDataItemList[7]
                        } else if (i == 4) {
                            seatchItem1 = featchDataItemList[8]
                            seatchItem2 = featchDataItemList[9]
                        }

                        for (j in 0..jsonArray.length() - 1) {

                            val statewise = jsonArray.getJSONObject(j)
                            if (i == 0) {

                                str_confirmed = statewise.getString(seatchItem1);
                                str_confirmed_new = statewise.getString(seatchItem2);

                                System.out.println(" str_confirmed " + str_confirmed + "str_confirmed_new" + str_confirmed_new)
                                cases.add(str_confirmed);
                                cases.add(str_confirmed_new);
                            }
                            if (i == 1) {
                                str_confirmed = statewise.getString(seatchItem1);
                                str_confirmed_new = statewise.getString(seatchItem2);
                                System.out.println(" death_total " + str_confirmed + "death_new" + str_confirmed_new)
                                death.add(str_confirmed);
                                death.add(str_confirmed_new);
                            }
                            if (i == 2) {
                                str_confirmed = statewise.getString(seatchItem1);
                                str_confirmed_new = statewise.getString(seatchItem2);
                                System.out.println(" recovered_total " + str_confirmed + "recovered_new" + str_confirmed_new)
                                recovered.add(str_confirmed);
                                recovered.add(str_confirmed_new);
                            }
                            if (i == 3) {
                                str_confirmed = statewise.getString(seatchItem1);
                                str_confirmed_new = statewise.getString(seatchItem2);
                                System.out.println(" tested_total " + str_confirmed + "tested_new" + str_confirmed_new)
                                testing.add(str_confirmed);
                                testing.add(str_confirmed_new);
                            }

                            if (i == 4) {
                                str_confirmed = statewise.getString(seatchItem1);
                                str_confirmed_new = statewise.getString(seatchItem2);
                                System.out.println(" active_total " + str_confirmed + "active_new" + str_confirmed_new)
                                active.add(str_confirmed);
                                active.add(str_confirmed_new);
                                str_province = statewise.getString("province");
                                str_last_update_time = statewise.getString("date_active");
                                ProvinceAndDate.add(str_province);
                                ProvinceAndDate.add(str_last_update_time);
//                            println("str_province =" )
                                temp = ProvinceAndDate;
                            }

                        }


                        //create a 1 second delay here to deal with the data
                        val delay = Handler()
                        delay.postDelayed({
                            stateWiseAdapter.notifyDataSetChanged();
                            DismissDialog();
                            if (i == 4) {
                                println("cases =" + cases)
                                println("death =" + death)
                                println("recovered =" + recovered)
                                println("testing =" + testing)
                                println("active =" + active)
                                println("province =" + ProvinceAndDate)
//                                    val stateWiseModel1 = StateWiseModel(
//            str_province, str_confirmed, str_confirmed_new, str_active,
//            str_death, str_death_new, str_recovered, str_recovered_new, str_last_update_time
//        )
                                for (k in 0..27 step 2) {
                                    val stateWiseModel = StateWiseModel(
                                            ProvinceAndDate.get(k),
                                            cases.get(k + 1),
                                            cases.get(k),
                                            active.get(k + 1),
                                            death.get(k + 1),
                                            death.get(k),
                                            recovered.get(k + 1),
                                            recovered.get(k),
                                            ProvinceAndDate.get(k + 1)
                                    );
                                    //adding data to our arraylist
                                    stateWiseModelArrayList.add(stateWiseModel);

                                }

                            }
//                        println("province data="+ProvinceAndDate)
                        }, 1000);

                    },
                    Response.ErrorListener { error ->
                        // TODO: Handle error
                    }
            )

            requestQueue.add(jsonObjectRequest);

        }



    }

    private fun loadDateIntoModle(){

        for (i in cases) {
            println(i)
        }
        println("province size=" + stateWiseModelArrayList.size)
        for (i in ProvinceAndDate) {
            println("?")
            println(i.toString())
        }

        //Creating an object of our statewise model class and passing the values in the constructor
        //Creating an object of our statewise model class and passing the values in the constructor
//        val stateWiseModel1 = StateWiseModel(
//            str_province, str_confirmed, str_confirmed_new, str_active,
//            str_death, str_death_new, str_recovered, str_recovered_new, str_last_update_time
//        )
//        for
//        val stateWiseModel = StateWiseModel()

        //adding data to our arraylist
        //adding data to our arraylist
//        stateWiseModelArrayList.add(stateWiseModel)
    }

    //adding the loading dialog
    public fun ShowDialog(context: Context) {
        progressDialog = ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(com.example.covid19tracker.R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);


    }
    //close the loading dialog
    public fun DismissDialog() {
        progressDialog.dismiss()
    }
    //using this method to prevent the accident touch to the back button
    //only two quick touch will make the back button work
    //prevent accident touch on the back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }



}