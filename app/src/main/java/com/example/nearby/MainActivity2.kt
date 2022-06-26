package com.example.nearby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nearby.ui.theme.NearbyTheme

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val bundle :Bundle ?=intent.extras
        val name : String? = bundle?.getString("name")
        val score : String? = bundle?.getString("score")

        super.onCreate(savedInstanceState)
        setContent {
            MoreDetails(name = name, score = score )
        }
    }
}

@Composable
fun MoreDetails(name: String?, score: String?) {
    NearbyTheme{
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Column {
                Text(text = "Name: $name",
                    fontWeight = FontWeight.W700,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp))
                Divider(thickness = 4.dp, modifier = Modifier.padding(8.dp))
                Text(text = "Score: $score",
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    NearbyTheme {
        Column{
            Text(text = "More Details",
                fontWeight = FontWeight.W700,
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp))
            Divider(thickness = 4.dp, modifier = Modifier.padding(8.dp))
            Text(text = "Hello test1!",
                fontWeight = FontWeight.W400,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp))
            Text(text = "Hello test2!",
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                modifier = Modifier.padding(16.dp))
        }
    }
}