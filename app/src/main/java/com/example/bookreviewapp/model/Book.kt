package com.example.bookreviewapp.model

import com.google.gson.annotations.SerializedName

data class Book(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String,
)