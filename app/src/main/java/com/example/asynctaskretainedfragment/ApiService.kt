package com.example.asynctaskretainedfragment

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    fun getItems(): Call<List<Item>>
}
