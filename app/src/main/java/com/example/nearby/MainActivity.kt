package com.example.nearby

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.nearby.ui.theme.NearbyTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MainActivity : ComponentActivity() {

    var lng = 0.0
    var lat = 0.0

    //unique number
    var PERMISSION_ID = 1000

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest : LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
             MyApp{
                 ScreenContent(locations = listOf(Location(title= "hi"), Location(title = "no")), modifier = Modifier.padding(8.dp))
             }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun getLastLocation(){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task ->
                    var location = task.result
                    if(location == null){


                    }else{
                        lng = location.longitude
                        lat = location.latitude
                        println("Location is lat: $lat , and long: $lng")
                    }

                }

            }else{
                Toast.makeText(this, "Please enable location service", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getNewLocation(){
//        locationRequest = LocationRequest()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.intervalMillis = 0
//



    }

    //Check users permission
//    private fun CheckPermission(): Boolean {
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            return true
//        }
//        return false
//    }

    //Get users permission
    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID
        )
    }

    //Check location service
    private fun isLocationEnabled():Boolean{
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug:", "You have the permissions")
            }
        }
    }


}


//Holds value of location details
data class Location(val title: String)

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
fun EachItem(location: Location){
    Text(location.title, color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(8.dp))
}

@Composable
fun ScreenContent(locations: List<Location>, modifier: Modifier = Modifier){

    LazyColumn(modifier = modifier){
        items(items = locations){ item ->
            EachItem(location = item)

        }

    }


}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp{
        ScreenContent(locations = listOf(Location(title= "hi"), Location(title = "no")), modifier = Modifier.padding(8.dp))
    }
}