package com.wibisa.dicodingstoryapp.core.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class AllStoriesNetwork(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)

@Parcelize
data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double,
    val lon: Double
) : Parcelable