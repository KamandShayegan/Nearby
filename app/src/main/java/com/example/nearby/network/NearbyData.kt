package com.example.nearby.network

data class NearbyData(
    val results : List<Result>,
)


data class Result(
    val id : String,
    val score : Float,
    val poi : POI
)

data class POI(
    val name : String,
)