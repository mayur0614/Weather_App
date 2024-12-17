package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.Constant
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel :ViewModel(){


    private val weatherApi = RetrofitInstance.weatherApi

    private val _wetherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
     val wetherResult : LiveData<NetworkResponse<WeatherModel>> = _wetherResult

    fun getData(city:String){
        _wetherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try{
                val response =  weatherApi.getWeather(Constant.apiKey,city)
                if(response.isSuccessful){
                    response.body()?.let {
                        _wetherResult.value = NetworkResponse.Success(it)
                    }
                }else{
                }
            }catch (e:Exception){
                _wetherResult.value = NetworkResponse.Error("Failed to load data ")

            }
        }

    }
}