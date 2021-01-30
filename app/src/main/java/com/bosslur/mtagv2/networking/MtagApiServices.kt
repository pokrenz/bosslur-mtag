package com.bosslur.mtagv2.networking

import com.bosslur.mtagv2.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MtagApiServices {

    @Headers("Accept: application/json","Content-Type: application/json")
    @POST("auth/login")
    fun loginRequest(@Body body: String): Call<LoginResponse>
}