package com.wibisa.dicodingstoryapp.util

import com.wibisa.dicodingstoryapp.core.data.remote.response.AllStoriesNetwork
import com.wibisa.dicodingstoryapp.core.data.remote.response.LoginResult
import com.wibisa.dicodingstoryapp.core.data.remote.response.Story

object DataDummy {

    const val dummyRegisterSuccessResponse = "User Created."

    const val dummyRegisterFailedResponse = "Failed Create User."

    val dummyLoginResult = LoginResult("123", "Jack", "oadjknuh1")

    const val dummyLoginFailedResponse = "Login Failed."

    fun generateDummyStoriesResponse(): AllStoriesNetwork {
        val error = false
        val message = "Stories fetched successfully"
        val listStory = mutableListOf<Story>()

        for (i in 0 until 5) {
            val story = Story(
                id = "story-DDDASASE4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-11-08T06:34:18.598Z",
                name = "Jack",
                description = "Lorem Ipsum",
                lon = -16.0342,
                lat = -10.2124
            )

            listStory.add(story)
        }

        return AllStoriesNetwork(error, message, listStory)
    }

    fun generateDummyListStory(): List<Story> {
        val items = arrayListOf<Story>()

        for (i in 0 until 5) {
            val story = Story(
                id = "story-FvU4u0sdfsaggDD",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-11-08T06:34:18.598Z",
                name = "Dimas",
                description = "Lorem Ipsum",
                lon = -16.0012,
                lat = -10.2132
            )

            items.add(story)
        }

        return items
    }
}