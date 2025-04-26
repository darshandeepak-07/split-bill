package com.example.splitbill.data.model

data class BillResponse(
    val status: Boolean,
    val message: List<Bill>,
    val response_code: Int
)

data class Bill(
    val tableId: String,
    val totalAmount: Double,
    val items: List<Item>,
    val people: List<Person>,
    val splitType: SplitType
)

data class Item(
    val name: String,
    val price: Double,
    val quantity: Int
)

data class Person(
    val name: String,
    val items: List<String>?,
    val amount: Double?,
    val share: Int?,
    val percentage: Double?
)

enum class SplitType {
    EVEN, BY_ITEM, BY_AMOUNT, BY_SHARE, BY_PERCENTAGE
}
