package com.example.splitbill.ui.screens.bill

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitbill.data.model.RequestBody
import com.example.splitbill.data.model.SplitType
import com.example.splitbill.ui.screens.bill.splitUI.AmountSplitContent
import com.example.splitbill.ui.screens.bill.splitUI.EvenSplitContent
import com.example.splitbill.ui.screens.bill.splitUI.ItemSplitContent
import com.example.splitbill.ui.screens.bill.splitUI.PercentageSplitContent
import com.example.splitbill.ui.screens.bill.splitUI.ShareSplitContent
import com.example.splitbill.ui.screens.components.SplitMethodDialog
import com.example.splitbill.ui.screens.tab.TabButton
import com.example.splitbill.utils.enums.Tab

@Composable
fun BillDetailsScreen(
    requestBody: RequestBody,
    viewModel: BillViewModel = viewModel(),
    onPaymentComplete: () -> Unit = {}
) {
    val loading by viewModel.loading.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val billList by viewModel.billList.observeAsState(emptyList())
    val splitResults by viewModel.splitResults.observeAsState(emptyList())
    var selectedTab by remember { mutableStateOf(Tab.BILL) }
    var showSplitMethodDialog by remember { mutableStateOf(false) }
    var currentSplitType by remember { mutableStateOf<SplitType?>(null) }
    var personAssignments by remember { mutableStateOf<Map<String, Map<String, Boolean>>>(emptyMap()) }
    var shareAssignments by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var percentageAssignments by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var amountAssignments by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }

    LaunchedEffect(Unit) {
        viewModel.getBills(requestBody)
    }

    when {
        loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading bill details...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Error: $error", color = Color.Red, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.getBills(requestBody) }) {
                        Text("Retry")
                    }
                }
            }
        }

        billList.isNotEmpty() -> {
            val bill = billList.first()

            LaunchedEffect(bill) {
                currentSplitType = bill.splitType
                viewModel.calculateSplit(bill)

                when (bill.splitType) {
                    SplitType.BY_ITEM -> {
                        val assignments = mutableMapOf<String, Map<String, Boolean>>()
                        bill.people.forEach { person ->
                            val items = bill.items.associate { item ->
                                item.name to (person.items?.contains(item.name) == true)
                            }
                            assignments[person.name] = items
                        }
                        personAssignments = assignments
                    }

                    SplitType.BY_SHARE -> {
                        shareAssignments = bill.people.associate { it.name to (it.share ?: 1) }
                    }

                    SplitType.BY_PERCENTAGE -> {
                        percentageAssignments = bill.people.associate {
                            it.name to (it.percentage ?: (100.0 / bill.people.size))
                        }
                    }

                    SplitType.BY_AMOUNT -> {
                        amountAssignments = bill.people.associate {
                            it.name to (it.amount ?: (bill.totalAmount / bill.people.size))
                        }
                    }

                    else -> {}
                }
            }

            Column(Modifier.fillMaxSize()) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Table",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Table #${bill.tableId}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Total: $${String.format("%.2f", bill.totalAmount)}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))

                        OutlinedButton(
                            onClick = { showSplitMethodDialog = true },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = when (currentSplitType) {
                                    SplitType.EVEN -> "Split Evenly"
                                    SplitType.BY_ITEM -> "Split by Item"
                                    SplitType.BY_AMOUNT -> "Split by Amount"
                                    SplitType.BY_SHARE -> "Split by Shares"
                                    SplitType.BY_PERCENTAGE -> "Split by Percentage"
                                    null -> "Choose Split Method"
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(Icons.Default.ArrowDropDown, "Change split method")
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    TabButton(
                        text = "Bill",
                        icon = Icons.Default.ShoppingCart,
                        selected = selectedTab == Tab.BILL,
                        onClick = { selectedTab = Tab.BILL },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Split",
                        icon = Icons.Default.Person,
                        selected = selectedTab == Tab.SPLIT,
                        onClick = { selectedTab = Tab.SPLIT },
                        modifier = Modifier.weight(1f)
                    )
                }

                when (selectedTab) {
                    Tab.BILL -> {
                        BillContent(bill)
                    }

                    Tab.SPLIT -> {
                        when (currentSplitType) {
                            SplitType.EVEN -> {
                                EvenSplitContent(
                                    splitResults
                                )
                            }

                            SplitType.BY_ITEM -> {
                                ItemSplitContent(
                                    bill = bill,
                                    splitResults = splitResults,
                                    personAssignments = personAssignments,
                                )
                            }

                            SplitType.BY_SHARE -> {
                                ShareSplitContent(
                                    bill = bill,
                                    splitResults = splitResults,
                                    shareAssignments = shareAssignments,
                                    onShareChange = { person, shares ->
                                        shareAssignments = shareAssignments.toMutableMap().apply {
                                            this[person] = shares
                                        }

                                        val updatedPeople = bill.people.map { p ->
                                            if (p.name == person) {
                                                p.copy(
                                                    share = shares,
                                                    percentage = null,
                                                    amount = null,
                                                    items = null
                                                )
                                            } else p
                                        }
                                        val updatedBill = bill.copy(people = updatedPeople)
                                        viewModel.calculateSplit(updatedBill.copy(splitType = SplitType.BY_SHARE))
                                    }
                                )
                            }

                            SplitType.BY_PERCENTAGE -> {

                                PercentageSplitContent(
                                    bill = bill,
                                    splitResults = splitResults,
                                    percentageAssignments = percentageAssignments,
                                    onPercentageChange = { person, percentage ->
                                        percentageAssignments =
                                            percentageAssignments.toMutableMap().apply {
                                                this[person] = percentage
                                            }

                                        val updatedPeople = bill.people.map { p ->
                                            if (p.name == person) {
                                                p.copy(
                                                    percentage = percentage,
                                                    share = null,
                                                    amount = null,
                                                    items = null
                                                )
                                            } else p
                                        }

                                        val updatedBill = bill.copy(people = updatedPeople)
                                        viewModel.calculateSplit(updatedBill.copy(splitType = SplitType.BY_PERCENTAGE))
                                    }
                                )
                            }

                            SplitType.BY_AMOUNT -> {
                                AmountSplitContent(
                                    bill = bill,
                                    amountAssignments = amountAssignments,
                                    onAmountChange = { person, amount ->
                                        amountAssignments = amountAssignments.toMutableMap().apply {
                                            this[person] = amount
                                        }

                                        val updatedPeople = bill.people.map { p ->
                                            if (p.name == person) {
                                                p.copy(
                                                    amount = amount,
                                                    share = null,
                                                    percentage = null,
                                                    items = null
                                                )
                                            } else p
                                        }

                                        val updatedBill = bill.copy(people = updatedPeople)
                                        viewModel.calculateSplit(updatedBill.copy(splitType = SplitType.BY_AMOUNT))
                                    }
                                )
                            }

                            null -> {
                                Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Please select a split method")
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onPaymentComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            "PROCEED TO PAYMENT",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (showSplitMethodDialog) {
                SplitMethodDialog(
                    currentMethod = currentSplitType ?: bill.splitType,
                    onDismiss = { showSplitMethodDialog = false },
                    onMethodSelected = { splitType ->
                        currentSplitType = splitType
                        showSplitMethodDialog = false

                        when (splitType) {
                            SplitType.BY_SHARE -> {
                                shareAssignments = bill.people.associate { person ->
                                    person.name to (person.share ?: 1)
                                }
                            }

                            SplitType.BY_PERCENTAGE -> {
                                val defaultPercentage = 100.0 / bill.people.size
                                percentageAssignments = bill.people.associate { person ->
                                    person.name to (person.percentage ?: defaultPercentage)
                                }
                            }

                            SplitType.BY_ITEM -> {
                                val assignments = mutableMapOf<String, Map<String, Boolean>>()
                                bill.people.forEach { person ->
                                    val items = bill.items.associate { item ->
                                        item.name to (person.items?.contains(item.name) == true)
                                    }
                                    assignments[person.name] = items
                                }
                                personAssignments = assignments
                            }

                            SplitType.BY_AMOUNT -> {
                                val defaultAmount = bill.totalAmount / bill.people.size
                                amountAssignments = bill.people.associate { person ->
                                    person.name to (person.amount ?: defaultAmount)
                                }
                            }

                            else -> {}
                        }

                        val updatedPeople = bill.people.map { person ->
                            when (splitType) {
                                SplitType.BY_SHARE -> person.copy(
                                    share = shareAssignments[person.name] ?: 1,
                                    percentage = null,
                                    amount = null,
                                    items = null
                                )

                                SplitType.BY_PERCENTAGE -> person.copy(
                                    percentage = percentageAssignments[person.name]
                                        ?: (100.0 / bill.people.size),
                                    share = null,
                                    amount = null,
                                    items = null
                                )

                                SplitType.BY_AMOUNT -> person.copy(
                                    amount = amountAssignments[person.name]
                                        ?: (bill.totalAmount / bill.people.size),
                                    share = null,
                                    percentage = null,
                                    items = null
                                )

                                SplitType.BY_ITEM -> {
                                    val personItems = personAssignments[person.name]
                                    val selectedItems =
                                        personItems?.filter { it.value }?.keys?.toList()
                                            ?: emptyList()
                                    person.copy(
                                        items = selectedItems,
                                        share = null,
                                        percentage = null,
                                        amount = null
                                    )
                                }

                                else -> person.copy(
                                    share = null,
                                    percentage = null,
                                    amount = null,
                                    items = null
                                )
                            }
                        }

                        val updatedBill = bill.copy(
                            splitType = splitType,
                            people = updatedPeople
                        )

                        viewModel.calculateSplit(updatedBill)
                    }
                )
            }
        }
    }
}
