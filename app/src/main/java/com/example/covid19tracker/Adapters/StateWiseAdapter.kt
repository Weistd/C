package com.example.covid19tracker.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.covid19tracker.Constants.STATE_ACTIVE
import com.example.covid19tracker.Constants.STATE_CONFIRMED
import com.example.covid19tracker.Constants.STATE_CONFIRMED_NEW
import com.example.covid19tracker.Constants.STATE_DEATH
import com.example.covid19tracker.Constants.STATE_DEATH_NEW
import com.example.covid19tracker.Constants.STATE_LAST_UPDATE
import com.example.covid19tracker.Constants.STATE_NAME
import com.example.covid19tracker.Constants.STATE_RECOVERED
import com.example.covid19tracker.Constants.STATE_RECOVERED_NEW
import com.example.covid19tracker.EachStateDataActivity
import com.example.covid19tracker.Models.StateWiseModel
import com.example.covid19tracker.R
import java.text.NumberFormat
import java.util.regex.Matcher
import java.util.regex.Pattern


class StateWiseAdapter(mContext: Context, stateWiseModelArrayList: ArrayList<StateWiseModel>) : RecyclerView.Adapter<StateWiseAdapter.ViewHolder>() {

    private var mContext: Context=mContext;
    private var stateWiseModelArrayList=stateWiseModelArrayList;
    private var searchText = "";

    private lateinit var sb: SpannableStringBuilder;

//    constructor(mContext: Context, stateWiseModelArrayList: ArrayList<StateWiseModel>) ;


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateWiseAdapter.ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.layout_state_wise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: StateWiseAdapter.ViewHolder, position: Int) {
        val currentItem =
            stateWiseModelArrayList[position]
        val stateName: String = currentItem.stateP;
        val stateTotal: String = currentItem.confirmedP
        //set the totalcases fetch from the arraylist and place it on the textview
        holder.tv_stateTotalCases.setText(
            NumberFormat.getInstance().format(
                Integer.parseInt(
                    stateTotal
                )
            )
        );

        //highlight the color of the searched letter
        if (searchText.length > 0) {
            //color your text here
            sb = SpannableStringBuilder(stateName)
            val word: Pattern = Pattern.compile(searchText.toLowerCase())
            val match: Matcher = word.matcher(stateName.toLowerCase())
            while (match.find()) {
                val fcs = ForegroundColorSpan(Color.rgb(55, 200, 235)) //specify color here
                sb.setSpan(fcs, match.start(), match.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            holder.tv_stateName.setText(sb)
        } else {
            holder.tv_stateName.text = stateName
        }


        holder.tv_stateName.setText(stateName);
        holder.lin_state_wise.setOnClickListener {
            val clickedItem = stateWiseModelArrayList[position]
            val perStateIntent = Intent(mContext, EachStateDataActivity::class.java)
            //passing the value from this activity;s statewiseModel to the new activity
            perStateIntent.putExtra(STATE_NAME, clickedItem.stateP)
            perStateIntent.putExtra(STATE_CONFIRMED, clickedItem.confirmedP)
            perStateIntent.putExtra(STATE_CONFIRMED_NEW, clickedItem.confirmed_newP)
            perStateIntent.putExtra(STATE_ACTIVE, clickedItem.activeP)
            perStateIntent.putExtra(STATE_DEATH, clickedItem.deathP)
            perStateIntent.putExtra(STATE_DEATH_NEW, clickedItem.death_newP)
            perStateIntent.putExtra(STATE_RECOVERED, clickedItem.recoveredP)
            perStateIntent.putExtra(STATE_RECOVERED_NEW, clickedItem.recovered_newP)
            perStateIntent.putExtra(STATE_LAST_UPDATE, clickedItem.lastupdateP)
            //start the new activity
            mContext.startActivity(perStateIntent)
        }


    }

    override fun getItemCount(): Int {
        return if (stateWiseModelArrayList == null) 0 else stateWiseModelArrayList.size
    }


    class ViewHolder : RecyclerView.ViewHolder {
        lateinit var tv_stateName: TextView;
        lateinit var tv_stateTotalCases:TextView;
        lateinit var lin_state_wise: LinearLayout;
        constructor(itemView: View): super(itemView) {
            tv_stateName = itemView.findViewById(R.id.statewise_layout_name_textview);
            tv_stateTotalCases = itemView.findViewById(R.id.statewise_layout_confirmed_textview);
            lin_state_wise = itemView.findViewById(R.id.layout_state_wise_lin);

        }

    }

    fun filterList(filteredList: ArrayList<StateWiseModel>, text: String) {
        stateWiseModelArrayList = filteredList
        searchText = text

        notifyDataSetChanged()
    }


}