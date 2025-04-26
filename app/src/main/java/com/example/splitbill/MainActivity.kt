package com.example.splitbill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.splitbill.data.model.RequestBody
import com.example.splitbill.ui.screens.bill.BillDetailsScreen
import com.example.splitbill.ui.theme.SplitBillTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mockRequest = RequestBody(
            apiKey = "3444CED48713B3DA2EC075CDDE7D562F",
            tableId = 69
        )
        setContent {
            SplitBillTheme {
                BillDetailsScreen(
                    requestBody = mockRequest
                )
            }
        }
    }
}
