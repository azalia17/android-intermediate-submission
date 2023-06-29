package com.azalia.submission1_storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.azalia.submission1_storyapp.data.Repository
import com.azalia.submission1_storyapp.data.local.StoryEntity

@ExperimentalPagingApi
class MainViewModel(private val repository: Repository) : ViewModel() {
   suspend fun logout() = repository.logout()

   fun getStories(token: String): LiveData<PagingData<StoryEntity>> = repository.getStory(token).cachedIn(viewModelScope)
}