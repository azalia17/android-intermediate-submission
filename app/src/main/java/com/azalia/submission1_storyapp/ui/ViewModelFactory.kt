package com.azalia.submission1_storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.azalia.submission1_storyapp.Injection
import com.azalia.submission1_storyapp.data.Repository
import com.azalia.submission1_storyapp.ui.add.AddStoryViewModel
import com.azalia.submission1_storyapp.ui.login.LoginViewModel
import com.azalia.submission1_storyapp.ui.main.MainViewModel
import com.azalia.submission1_storyapp.ui.map.MapsViewModel
import com.azalia.submission1_storyapp.ui.register.RegisterViewModel
import com.azalia.submission1_storyapp.ui.splash.SplashscreenViewModel

class ViewModelFactory constructor(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    @OptIn(ExperimentalPagingApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SplashscreenViewModel::class.java) -> {
                SplashscreenViewModel(this.repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}