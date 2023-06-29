package com.azalia.submission1_storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.azalia.submission1_storyapp.UserPreference
import com.azalia.submission1_storyapp.data.local.StoryDatabase
import com.azalia.submission1_storyapp.data.local.StoryEntity
import com.azalia.submission1_storyapp.data.remote.ApiService
import com.azalia.submission1_storyapp.data.remote.RetrofitService
import com.azalia.submission1_storyapp.response.LoginResponse
import com.azalia.submission1_storyapp.response.RegisterResponse
import com.azalia.submission1_storyapp.response.StoryResponse
import com.azalia.submission1_storyapp.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository constructor(private val pref: UserPreference, private val database: StoryDatabase) {
    private val retrofit: ApiService = RetrofitService.create()


    fun userLogin(email: String, password: String): LiveData<Resource<LoginResponse>> = liveData{
        try {
            emit(Resource.Loading())
            val response = retrofit.loginUser(email, password)
            val token = response.loginResult.token
            pref.saveUser(token)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Failure(e.toString()))
        }
    }

    fun userRegister(name: String, email: String, password: String): LiveData<Resource<RegisterResponse>> = liveData {
        try {
            emit(Resource.Loading())
            val response = retrofit.registerUser(name = name, email = email, password = password)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Failure(e.toString()))
        }
    }

    fun getStory(token: String): LiveData<PagingData<StoryEntity>> {
        val token = getToken(token)
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(
                database,
                retrofit,
                token
            ),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoriesLocation(token: String): LiveData<Resource<StoryResponse>> = liveData {
        try {
            emit(Resource.Loading())
            val getToken = getToken(token)
            val response = retrofit.getAllStories(token = getToken, location = 1)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Failure(e.toString()))
        }
    }

    fun uploadStory(token: String, description: RequestBody, image: MultipartBody.Part, lat: RequestBody? = null, lon: RequestBody? = null): LiveData<Resource<UploadResponse>> = liveData {
        val token = getToken(token)
        try {
            emit(Resource.Loading())
            val response = retrofit.addStory(token, image, description, lat, lon)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("UPLOAD", e.toString())
            emit(Resource.Failure(e.toString()))
        }
    }

    fun getUser(): LiveData<String?> = pref.getUser().asLiveData()

    suspend fun logout() = pref.logout()

    private fun getToken(token: String): String = "Bearer $token"
}