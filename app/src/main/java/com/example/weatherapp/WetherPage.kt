package com.example.weatherapp


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest

import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherModel
import kotlinx.coroutines.delay


@Composable
fun WetherPage(viewModel: WeatherViewModel) {
    var city by remember { mutableStateOf("") }
    val keyBoardController = LocalSoftwareKeyboardController.current
    val weatherResult = viewModel.wetherResult.observeAsState()

    val backgroundColor = colorResource(id = R.color.Light_Light_Blue)
    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = city,
                onValueChange = {
                    city = it
                },
                label = {
                    Text("Search for any location")
                }
            )
            IconButton(

                onClick = {
                    viewModel.getData(city)
                    keyBoardController?.hide()
                    city = ""
                }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        }
        when (val result = weatherResult.value) {
            is NetworkResponse.Error -> {

            }


            NetworkResponse.Loading -> {
               LoadingScreenWithTimeout()
            }

            is NetworkResponse.Success -> {
                WeatherDetails(result.data)
            }

            null -> {

            }
        }
    }
}
}

@Composable
fun WeatherDetails(data:WeatherModel){
    val cardBackgroundColor = colorResource(id=R.color.Light_Blue)
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

    ) {

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ){
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription ="Location icon ",
                modifier = Modifier.size(40.dp)
            )
            Text(text=data.location.name, fontSize = 30.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text=data.location.country,
                fontSize = 18.sp,
                color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text="${data.current.temp_c}°C",
            fontSize = 56.sp     ,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https:${data.current.condition.icon}".replace("64*64","256*256"))
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.size(160.dp)
        )
        Text(
            text= data.current.condition.text,
            fontSize = 20.sp     ,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(

            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardBackgroundColor
            )
        ) {
            Column (
                modifier = Modifier.fillMaxWidth()
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ){
                    WeatherKeyVal("Humidity",data.current.humidity)
                    WeatherKeyVal("Wind Speed",data.current.wind_kph+"km/hr")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ){
                    WeatherKeyVal("Uv",data.current.uv)
                    WeatherKeyVal("precipitation",data.current.precip_mm+"mm")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ){
                    WeatherKeyVal("Local time",data.location.localtime.split(" ")[1])
                    WeatherKeyVal("Local date ",data.location.localtime.split(" ")[0])
                }
            }
        }
    }
}
@Composable
fun WeatherKeyVal(key:String,value:String){
    Column (
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text=value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text=key, fontWeight = FontWeight.SemiBold, color = Color.Gray)

    }
}


@Composable
fun LoadingScreenWithTimeout() {

    var isLoading by remember { mutableStateOf(true) }
    var timeoutMessage by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {

        delay(5000L)
        if (isLoading) {

            timeoutMessage = "Cannot Fetch! ,Try another location"
            isLoading = false
        }
    }

    // UI layout
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator() // Show loader
        } else {
            Text(text = timeoutMessage, color = Color.Black) // Show timeout message
        }
    }
}



















