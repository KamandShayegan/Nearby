package com.example.nearby

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.service.autofill.OnClickAction
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.nearby.ui.theme.NearbyTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.compose.rememberNavController
import com.example.nearby.network.NearbyData
import com.example.nearby.network.Results
import com.example.nearby.viewmodel.Key
import com.example.nearby.viewmodel.OverviewViewModel
import com.example.nearby.viewmodel.TomTomStat

class MainActivity : ComponentActivity() {
    val viewModel = OverviewViewModel()
    val key : Key = Key("AIzaSyDdPjIKXzWueeY-_ZT9Y86IgHoQ5lau-LY")

    private lateinit var fusedLocProviderClient : FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val lonLat: LongLat = getCurLocation(viewModel, key)
        println("LONG: ${lonLat.long} \n LAT: ${lonLat.lat} ")

        setContent {
            MyApp {
                    ModelHolder(viewModel)
            }
        }





    }
    private fun getCurLocation(viewModel : OverviewViewModel, key: Key) : LongLat {
         var lon = 0.0
         var lat = 0.0

        if(checkPermissions()){
            if(isLocationEnabled()){
                //final long and lat
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return LongLat(0.0, 0.0)
                }

                fusedLocProviderClient.lastLocation.addOnCompleteListener(this){ task ->
                    val location = task.result
                    if(location == null){
                        Toast.makeText(this, "Received NULL!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "RECEIVED!", Toast.LENGTH_SHORT).show()
                        viewModel.onLocationChange(location, key = key)
                        println("LONG: ${location.longitude} \n LAT: ${location.latitude}")
                    }


                }

            }else{
                //setting open here
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        }else{
            //request permission here
            requestPermission()
        }
        return LongLat(lon, lat)
    }

    private fun isLocationEnabled() : Boolean {
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_REQ_ACCESS_LOC)
    }

    companion object {
        private const val PERMISSION_REQ_ACCESS_LOC = 10

    }

    private fun checkPermissions(): Boolean{
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQ_ACCESS_LOC){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurLocation(viewModel = viewModel, key)
            }else{
                Toast.makeText(applicationContext, "Denied!", Toast.LENGTH_SHORT).show()

            }

        }
    }

}




//Holds value of location details
data class Location(val title: String)
data class LongLat(val long: Double, val lat: Double)

@Composable
fun MyApp(content: @Composable ()-> Unit){
    NearbyTheme{
        Surface(
            color = MaterialTheme.colors.background
        ){
            content()
        }

    }
}

@Composable
fun ModelHolder(viewModel: OverviewViewModel){
    val places : NearbyData? by viewModel.nearbyPlace.observeAsState()
    val stat : TomTomStat? by viewModel.status.observeAsState()
    NearbyDataHolder(modifier = Modifier.padding(8.dp), data = places, stat)
}

@Composable
fun NearbyDataHolder(modifier: Modifier = Modifier, data : NearbyData?, status: TomTomStat?){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        when(status) {
            TomTomStat.DONE -> ShowResponse(places= data)
            TomTomStat.ERROR -> ShowError()
            TomTomStat.LOADING -> ShowLoading()
            else -> Surface{}
        }
    }

}

@Composable
fun ShowResponse(places: NearbyData?){
    LazyColumn{
        items(items = places?.results?:listOf()){
            EachItem(place = it)
        }
    }
}

@Composable
fun ShowError(){
        Text(text = "ERROR!")
}

@Composable
fun ShowLoading(){
    CircularProgressIndicator()
}

@Composable
fun EachItem(place : Results){
    val mContext = LocalContext.current

    Box(Modifier.clickable {
        val navigate = Intent(mContext, MainActivity2::class.java )
        mContext.startActivity(navigate)

    }) {

        Column(modifier = Modifier
            .height(80.dp)
            .padding(8.dp)) {
            Text(place.poi.name)
        }

    }



}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp{
        ModelHolder(viewModel = OverviewViewModel())
    }
}