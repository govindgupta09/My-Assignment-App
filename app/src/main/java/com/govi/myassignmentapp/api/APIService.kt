package com.govi.myassignmentapp.api

import com.govi.myassignmentapp.models.MyData
import retrofit2.Call
import retrofit2.http.GET

interface APIService {
    @GET("/products")
    fun fetchData(): Call<MyData>
}