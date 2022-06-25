package com.example.nearby.network

data class NearbyData(
    val results : List<Results>,
)


data class Results(
    val id : String,
    val poi : POI
)

data class POI(
    val name : String,
    val phone : String,
    val url : String
)