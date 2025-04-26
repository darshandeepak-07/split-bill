package com.example.splitbill.data.api

import com.example.splitbill.data.model.BillResponse
import com.example.splitbill.data.model.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("getOrderByTableId")
    suspend fun getBill(@Body requestBody: RequestBody): Response<BillResponse>
}
