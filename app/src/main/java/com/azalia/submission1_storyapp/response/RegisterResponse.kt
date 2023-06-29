package com.azalia.submission1_storyapp.response

import com.squareup.moshi.Json

data class RegisterResponse (
    @field:Json(name = "error")
    val error: Boolean,

    @field:Json(name = "message")
    val message: String,
)