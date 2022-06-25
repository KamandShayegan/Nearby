package com.example.nearby.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nearby.network.NearbyData
import com.example.nearby.network.Results
import com.example.nearby.network.TomTomAPI
import kotlinx.coroutines.launch

enum class TomTomStat {LOADING, ERROR, DONE}


class OverviewViewModel : ViewModel(){

    init{
        getNearbyPlaces()
    }


    private val _status = MutableLiveData<TomTomStat>()
    val status : LiveData<TomTomStat> = _status

    private val _nearbyPlace = MutableLiveData<List<NearbyData>>()
    val nearbyPlace : LiveData<List<NearbyData>> = _nearbyPlace


    private fun getNearbyPlaces(){
        viewModelScope.launch {
            _status.value = TomTomStat.LOADING
            try{
                _nearbyPlace.value = TomTomAPI.retrofitService.getNearbyData()
                _status.value = TomTomStat.DONE
            }catch(e:Exception){
                _status.value = TomTomStat.ERROR
                _nearbyPlace.value = listOf()
            }
        }
    }


}