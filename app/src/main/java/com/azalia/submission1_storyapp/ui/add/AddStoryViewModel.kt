package com.azalia.submission1_storyapp.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.azalia.submission1_storyapp.data.Repository
import com.azalia.submission1_storyapp.data.Resource
import com.azalia.submission1_storyapp.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: Repository): ViewModel() {
    fun getUser(): LiveData<String?> = repository.getUser()

    fun addStory(token: String, image: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null) : LiveData<Resource<UploadResponse>> = repository.uploadStory(token, description, image, lat, lon)
}