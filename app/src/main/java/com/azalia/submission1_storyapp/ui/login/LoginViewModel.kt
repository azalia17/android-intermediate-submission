package com.azalia.submission1_storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.azalia.submission1_storyapp.data.Repository
import com.azalia.submission1_storyapp.data.Resource
import com.azalia.submission1_storyapp.response.LoginResponse

class LoginViewModel(private val repository: Repository) : ViewModel() {
    fun userLogin(email: String, password: String): LiveData<Resource<LoginResponse>> = repository.userLogin(email, password)
}