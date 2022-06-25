package com.example.nearby.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nearby.network.NearbyData
import com.example.nearby.network.TomTomAPI
import kotlinx.coroutines.launch

enum class TomTomStat {LOADING, ERROR, DONE}


class OverviewViewModel : ViewModel(){
    private val _status = MutableLiveData<TomTomStat>()
    val status : LiveData<TomTomStat> = _status

    private val _nearbyPlace = MutableLiveData<NearbyData>()
    val nearbyPlace : LiveData<NearbyData> = _nearbyPlace

    fun onStatChange(stat: TomTomStat){
        _status.value = stat
    }

    fun onListChange(data : NearbyData){
        _nearbyPlace.value = data
    }

    fun onLocationChange(location: Location, key : Key) {
        viewModelScope.launch {
            _status.value = TomTomStat.LOADING
            try{
                _nearbyPlace.value = TomTomAPI.retrofitService.getNearbyData(key = key.key, lat = location.latitude, lon = location.longitude)

                _status.value = TomTomStat.DONE
            }catch(e:Exception){
                println(e.message)
                _status.value = TomTomStat.ERROR
                _nearbyPlace.value = NearbyData(results = listOf())

            }
        }
    }


}