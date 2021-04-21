package com.example.covid19tracker

//create the constants to load the data into the correct position
object Constants {
    //State data Keys
    const val STATE_NAME = "stateName"
    const val STATE_CONFIRMED = "stateConfirmed"
    const val STATE_CONFIRMED_NEW = "stateConfirmedNew"
    const val STATE_ACTIVE = "stateActive"
    const val STATE_DEATH = "stateDeath"
    const val STATE_DEATH_NEW = "stateDeathNew"
    const val STATE_RECOVERED = "stateRecovered"
    const val STATE_RECOVERED_NEW = "stateRecoveredNew"
    const val STATE_LAST_UPDATE = "stateLastUpdate"

    //District Data Keys
    const val DISTRICT_NAME = "districtName"
    const val DISTRICT_CONFIRMED = "districtConfirmed"
    const val DISTRICT_CONFIRMED_NEW = "districtConfirmedNew"
    const val DISTRICT_ACTIVE = "districtActive"
    const val DISTRICT_DEATH = "districtDeath"
    const val DISTRICT_DEATH_NEW = "districtDeathNew"
    const val DISTRICT_RECOVERED = "districtRecovered"
    const val DISTRICT_RECOVERED_NEW = "districtRecoveredNew"

    //Country Data Keys
    const val COUNTRY_NAME = "country"
    const val COUNTRY_CONFIRMED = "cases"
    const val COUNTRY_ACTIVE = "active"
    const val COUNTRY_DECEASED = "deaths"
    const val COUNTRY_NEW_CONFIRMED = "todayCases"
    const val COUNTRY_TESTS = "tests"
    const val COUNTRY_NEW_DECEASED = "todayDeaths"
    const val COUNTRY_FLAGURL = "flag"
    const val COUNTRY_RECOVERED = "recovered"
}