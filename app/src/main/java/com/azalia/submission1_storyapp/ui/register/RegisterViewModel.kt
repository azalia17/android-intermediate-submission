package com.azalia.submission1_storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.azalia.submission1_storyapp.data.Repository
import com.azalia.submission1_storyapp.data.Resource
import com.azalia.submission1_storyapp.response.LoginResponse
import com.azalia.submission1_storyapp.response.RegisterResponse

class RegisterViewModel(private val repository: Repository) : ViewModel(){
    fun userRegister(name: String, email: String, password: String): LiveData<Resource<RegisterResponse>> = repository.userRegister(name, email, password)
    fun userLogin(email: String, password: String): LiveData<Resource<LoginResponse>> = repository.userLogin(email, password)
}