package com.example.splitbill.ui.screens.bill.splitUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.splitbill.data.model.Bill
import kotlin.math.absoluteValue

@Composable
fun AmountSplitContent(
    bill: Bill,
    amountAssignments: Map<String, Double>,
    onAmountChange: (String, Double) -> Unit
) {
    var editingPerson by remember { mutableStateOf<String?>(null) }
    var tempAmount by remember { mutableStateOf("") }

    val totalAssigned = amountAssignments.values.sum()
    val remaining = bill.totalAmount - totalAssigned
    val isValidTotal = remaining.absoluteValue < 0.01

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Split by Amount",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Total: $${String.format("%.2f", totalAssigned)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        if (remaining >= 0) "Remaining: $${String.format("%.2f", remaining)}"
                        else "Over by: $${String.format("%.2f", -remaining)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isValidTotal) MaterialTheme.colorScheme.primary
                        else if (remaining > 0) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Assign specific amount to each person",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            bill.people.forEach { person ->
                val amount = amountAssignments[person.name] ?: (bill.totalAmount / bill.people.size)
                val isEditing = editingPerson == person.name

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            person.name.first().toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        person.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    if (isEditing) {
                        TextField(
                            value = tempAmount,
                            onValueChange = {
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    tempAmount = it
                                }
                            },
                            modifier = Modifier.width(120.dp),
                            singleLine = true,
                            leadingIcon = {
                                Text(
                                    "$",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FilledTonalButton(
                            onClick = {
                                tempAmount.toDoubleOrNull()?.let { value ->
                                    onAmountChange(person.name, value)
                                }
                                editingPerson = null
                            }
                        ) {
                            Text("OK")
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "$${String.format("%.2f", amount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            FilledTonalButton(
                                onClick = {
                                    editingPerson = person.name
                                    tempAmount = String.format("%.2f", amount)
                                }
                            ) {
                                Text("Edit")
                            }
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            if (!isValidTotal) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (remaining > 0)
                            MaterialTheme.colorScheme.tertiaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Warning",
                            tint = if (remaining > 0)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            if (remaining > 0)
                                "There's still $${
                                    String.format(
                                        "%.2f",
                                        remaining
                                    )
                                } remaining to be assigned."
                            else
                                "The assigned amount exceeds the total by $${
                                    String.format(
                                        "%.2f",
                                        -remaining
                                    )
                                }.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (remaining > 0)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (remaining > 0) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            // Distribute remaining amount equally
                            val equalShare = remaining / bill.people.size
                            bill.people.forEach { person ->
                                val currentAmount = amountAssignments[person.name] ?: 0.0
                                onAmountChange(person.name, currentAmount + equalShare)
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Distribute Remaining Equally")
                    }
                }
            }
        }
    }
}
