package com.example.splitbill.ui.screens.bill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbill.data.model.Bill
import com.example.splitbill.data.model.RequestBody
import com.example.splitbill.data.model.Response
import com.example.splitbill.data.model.SplitType
import com.example.splitbill.data.repository.BillRepository
import kotlinx.coroutines.launch


class BillViewModel(private val repository: BillRepository = BillRepository()) : ViewModel() {

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _billList = MutableLiveData<List<Bill>>()
    val billList: LiveData<List<Bill>> = _billList

    val splitResults = MutableLiveData<List<Pair<String, Double>>>()

    fun calculateSplit(bill: Bill) {
        val result = when (bill.splitType) {
            SplitType.EVEN -> {
                val each = bill.totalAmount / bill.people.size
                bill.people.map { it.name to each }
            }

            SplitType.BY_ITEM -> {
                bill.people.map { person ->
                    val total = bill.items.filter { person.items?.contains(it.name) == true }
                        .sumOf { it.price * it.quantity }
                    person.name to total
                }
            }

            SplitType.BY_AMOUNT -> {
                bill.people.map { it.name to (it.amount ?: 0.0) }
            }

            SplitType.BY_SHARE -> {
                val totalShares = bill.people.sumOf { it.share ?: 0 }
                bill.people.map { person ->
                    val share = person.share ?: 0
                    val amount =
                        if (totalShares > 0) bill.totalAmount * share / totalShares else 0.0
                    person.name to amount
                }
            }

            SplitType.BY_PERCENTAGE -> {
                bill.people.map { person ->
                    val pct = person.percentage ?: 0.0
                    val amount = bill.totalAmount * pct / 100
                    person.name to amount
                }
            }
        }
        splitResults.value = result
    }


    fun getBills(requestBody: RequestBody) {
        _loading.value = true
        viewModelScope.launch {
            when (val result = repository.getAllBills(requestBody)) {
                is Response.Success -> {
                    _billList.value = result.data.message
                    _error.value = null
                }

                is Response.Error -> {
                    _billList.value = emptyList()
                    _error.value = result.message
                }
            }
            _loading.value = false
        }
    }
}