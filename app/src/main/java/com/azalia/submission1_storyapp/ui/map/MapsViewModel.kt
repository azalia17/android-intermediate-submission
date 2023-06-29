package com.azalia.submission1_storyapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azalia.submission1_storyapp.data.Repository
import com.azalia.submission1_storyapp.data.Resource
import com.azalia.submission1_storyapp.response.StoryResponse
import com.google.android.gms.maps.model.LatLng

class MapsViewModel(private val repository: Repository): ViewModel() {
    val coordinateTemp = MutableLiveData(LatLng(-6.8957643, 107.6338462))

    fun getStoriesLocation(token: String): LiveData<Resource<StoryResponse>> = repository.getStoriesLocation(token)
}