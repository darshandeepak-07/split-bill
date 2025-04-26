package com.example.splitbill.data.repository

import com.example.splitbill.data.api.ApiClient
import com.example.splitbill.data.model.BillResponse
import com.example.splitbill.data.model.RequestBody
import com.example.splitbill.data.model.Response
import com.example.splitbill.utils.Helper

class BillRepository {
    private val apiService = ApiClient.retrofitService

    suspend fun getAllBills(requestBody: RequestBody): Response<BillResponse> {
        return try {
            val response = apiService.getBill(requestBody)
            val mockResponse = Helper.generateMockBillResponse()
            if (response.isSuccessful && response.body() != null) {
//                Response.Success(response.body()!!)
                Response.Success(mockResponse)
            } else {
                Response.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Response.Error("Error: ${e.message}")
        }
    }
}
