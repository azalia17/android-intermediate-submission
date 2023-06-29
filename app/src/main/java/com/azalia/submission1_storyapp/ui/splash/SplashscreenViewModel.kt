package com.azalia.submission1_storyapp.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.azalia.submission1_storyapp.data.Repository

class SplashscreenViewModel(private val repository: Repository) : ViewModel() {
    fun getUser(): LiveData<String?> = repository.getUser()
}