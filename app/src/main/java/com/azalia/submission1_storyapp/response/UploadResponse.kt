package com.azalia.submission1_storyapp.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadResponse (
    @field:Json(name = "error")
    val error: Boolean,

    @field:Json(name = "message")
    val message: String,
)