package com.azalia.submission1_storyapp.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
data class StoryResponse (
    @field:Json(name = "error")
    val error: Boolean,

    @field:Json(name = "message")
    val message: String,

    @field:Json(name = "listStory")
    val listStory: List<ListStory>
)

@Parcelize
data class ListStory (
    @field:Json(name = "id")
    val id: String,

    @field:Json(name = "name")
    val name: String,

    @field:Json(name = "description")
    val description: String,

    @field:Json(name = "photoUrl")
    val photoUrl: String,

    @field:Json(name = "createdAt")
    val createdAt: String,

    @field:Json(name = "lat")
    val lat: Double? = null,

    @field:Json(name = "lon")
    val lon: Double? = null,
) : Parcelable