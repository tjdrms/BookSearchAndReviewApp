package com.example.bookreviewapp.model

import com.google.gson.annotations.SerializedName

data class BookDto(
    @SerializedName("items") val books: List<Book>
)
