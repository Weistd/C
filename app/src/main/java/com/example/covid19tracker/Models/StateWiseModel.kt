package com.example.covid19tracker.Models

data class StateWiseModel(var stateP: String,var confirmedP: String,var confirmed_newP: String,var activeP: String,var deathP: String,var death_newP: String,var recoveredP: String,var recovered_newP: String,var lastupdateP: String) {
    private var state:String = stateP;
    private  var confirmed: String=confirmedP;
    private  var confirmed_new: String=confirmed_newP;
    private  var active: String=activeP;
    private  var death: String=deathP;
    private  var death_new: String=death_newP;
    private  var recovered: String=recoveredP;
    private  var recovered_new: String=recovered_newP;
    private  var lastupdate: String=lastupdateP;

}