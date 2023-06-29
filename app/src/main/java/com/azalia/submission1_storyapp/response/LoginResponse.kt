package com.azalia.submission1_storyapp.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @field:Json(name = "error")
    val error: Boolean,

    @field:Json(name = "message")
    val message: String,

    @field:Json(name = "loginResult")
    val loginResult: LoginResult
)

data class LoginResult (
    @field:Json(name = "userId")
    val userId: String,

    @field:Json(name = "name")
    val name: String,

    @field:Json(name = "token")
    val token: String,
)
