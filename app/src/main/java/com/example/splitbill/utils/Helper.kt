package com.example.splitbill.utils

import com.example.splitbill.data.model.Bill
import com.example.splitbill.data.model.BillResponse
import com.example.splitbill.data.model.Item
import com.example.splitbill.data.model.Person
import com.example.splitbill.data.model.SplitType

object Helper {

    fun generateMockBillResponse(): BillResponse {
        return BillResponse(
            status = true,
            message = listOf(
                Bill(
                    tableId = "69",
                    totalAmount = 100.0,
                    items = listOf(
                        Item(name = "Burger", price = 10.0, quantity = 1),
                        Item(name = "Drink", price = 5.0, quantity = 6),
                        Item(name = "Pizza", price = 12.0, quantity = 5)
                    ),
                    people = listOf(
                        Person(name = "Person 1", items = listOf("Burger", "Drink"), amount = null, share = null, percentage = null),
                        Person(name = "Person 2", items = listOf("Pizza"), amount = null, share = null, percentage = null)
                    ),
                    splitType = SplitType.BY_ITEM
                )
            ),
            response_code = 200
        )
    }

}