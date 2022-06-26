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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.compose.rememberNavController
import com.example.nearby.network.NearbyData
import com.example.nearby.network.Result
import com.example.nearby.viewmodel.Key
import com.example.nearby.viewmodel.OverviewViewModel
import com.example.nearby.viewmodel.TomTomStat

class MainActivity : ComponentActivity() {
    val viewModel = OverviewViewModel()
    val key : Key = Key("DWHwqxBRthz7COQVzQNGxJSfQ4gU7Quk")

    private lateinit var fusedLocProviderClient : FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurLocation(viewModel, key)

        setContent {
            MyApp {
                    ScreenContent(viewModel)
            }
        }





    }
    private fun getCurLocation(viewModel : OverviewViewModel, key: Key){
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
                }

                fusedLocProviderClient.lastLocation.addOnCompleteListener(this){ task ->
                    val location = task.result
                    if(location == null){
                        Toast.makeText(this, "Failed to fetch location!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Location Received", Toast.LENGTH_SHORT).show()
                        viewModel.onLocationChange(location, key = key)
                        println("LONG: ${location.longitude} \n LAT: ${location.latitude}")
                    }


                }

            }else{
                //open here
                Toast.makeText(this, "Please turn on your location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        }else{
            //request permission here
            requestPermission()
        }
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
                Toast.makeText(applicationContext, "Acess Granted", Toast.LENGTH_SHORT).show()
                getCurLocation(viewModel = viewModel, key)
            }else{
                Toast.makeText(applicationContext, "Access Denied!", Toast.LENGTH_SHORT).show()

            }

        }
    }

}

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
fun ScreenContent(viewModel: OverviewViewModel){
    Column {
        TopAppBar{
            Text(text = "Nearby", style = TextStyle(fontWeight = FontWeight.Bold), fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        ModelHolder(viewModel = viewModel)
    }
}

@Composable
fun ModelHolder(viewModel: OverviewViewModel){
    val places : NearbyData? by viewModel.nearbyPlace.observeAsState()
    val stat : TomTomStat? by viewModel.status.observeAsState()
    NearbyDataHolder(data = places, stat)
}

@Composable
fun NearbyDataHolder(data : NearbyData?, status: TomTomStat?){
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
fun EachItem(place : Result){
    val mContext = LocalContext.current
    Box(
        Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp, 4.dp, 4.dp, 4.dp))
            .border(BorderStroke(2.dp, MaterialTheme.colors.primary))
            .fillMaxWidth()
            .clickable {
                val navigate = Intent(mContext, MainActivity2::class.java)
                navigate.putExtra("score", place.score.toString())
                navigate.putExtra("name", place.poi.name)
                mContext.startActivity(navigate)

            }
            .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp))) {
        Column(modifier = Modifier
            .height(56.dp)
            .padding(8.dp)) {
            Text(place.poi.name, overflow = TextOverflow.Ellipsis)
        }

    }



}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp{
        TopAppBar{
            Text(text = "Places Near You", style = TextStyle(fontWeight = FontWeight.Bold), fontSize = 32.sp)
        }
    }
}