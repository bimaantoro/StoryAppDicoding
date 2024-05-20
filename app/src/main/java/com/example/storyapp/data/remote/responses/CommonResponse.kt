package com.example.storyapp.data.remote.responses

import com.google.gson.annotations.SerializedName

data class CommonResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
