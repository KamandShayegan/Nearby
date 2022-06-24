package com.example.nearby

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.nearby.ui.theme.NearbyTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {

private lateinit var fusedLocProviderClient : FusedLocationProviderClient




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val lonLat: LongLat = getCurLocation()
        println("LONG: ${lonLat.long} \n LAT: ${lonLat.lat} ")

        setContent {
            MyApp {
                ScreenContent(modifier = Modifier.padding(8.dp))
            }
        }





    }
    private fun getCurLocation() : LongLat {
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
                getCurLocation()
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
fun EachItem(loc: LongLat){
    Column() {
        Text("LONG: ${loc.long}", color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(8.dp))
        Text("LAT: ${loc.lat}", color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier){
//    val longlat : LongLat by remember {
//        mutableStateOf(LongLat(0.0,0.0))
//    }
    Column(modifier = modifier){
//        EachItem(loc = loc)
    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp{
        ScreenContent(modifier = Modifier.padding(8.dp))
    }
}