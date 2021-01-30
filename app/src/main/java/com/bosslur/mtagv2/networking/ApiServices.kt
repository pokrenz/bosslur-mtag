package com.bosslur.mtagv2.networking

object ApiServices {
    fun getMtagServices(): MtagApiServices {
        return RetrofitClient.getClient().create(MtagApiServices::class.java)
    }
}