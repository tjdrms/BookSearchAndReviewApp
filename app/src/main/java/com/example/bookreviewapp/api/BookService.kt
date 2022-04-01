package com.example.bookreviewapp.api

import com.example.bookreviewapp.model.BookDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BookService {
    @GET("/v1/search/book.json")
    fun getBooksByName(
        @Query("query") keyword: String,
        @Query("display") display: Int,
        @Header("X-Naver-Client-Id") ID: String,
        @Header("X-Naver-Client-Secret") secret: String
    ): Call<BookDto>
}