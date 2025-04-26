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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.splitbill.data.model.Bill

@Composable
fun PercentageSplitContent(
    bill: Bill,
    splitResults: List<Pair<String, Double>>,
    percentageAssignments: Map<String, Double>,
    onPercentageChange: (String, Double) -> Unit
) {
    var editingPerson by remember { mutableStateOf<String?>(null) }
    var tempPercentage by remember { mutableStateOf("") }

    val totalPercentage = percentageAssignments.values.sum()
    val isValidTotal = (99.0..101.0).contains(totalPercentage)

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
                    "Split by Percentage",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Total: ${String.format("%.1f", totalPercentage)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isValidTotal) MaterialTheme.colorScheme.primary else Color.Red,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Assign percentage to each person (total should be 100%)",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            bill.people.forEach { person ->
                val percentage = percentageAssignments[person.name] ?: (100.0 / bill.people.size)
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

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            person.name,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        val amount = splitResults.find { it.first == person.name }?.second ?: 0.0
                        Text(
                            "$${String.format("%.2f", amount)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (isEditing) {
                        TextField(
                            value = tempPercentage,
                            onValueChange = {
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    tempPercentage = it
                                }
                            },
                            modifier = Modifier.width(100.dp),
                            singleLine = true,
                            trailingIcon = {
                                Text(
                                    "%",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FilledTonalButton(
                            onClick = {
                                tempPercentage.toDoubleOrNull()?.let { value ->
                                    onPercentageChange(person.name, value)
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
                                "${String.format("%.1f", percentage)}%",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            FilledTonalButton(
                                onClick = {
                                    editingPerson = person.name
                                    tempPercentage = String.format("%.1f", percentage)
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
                        containerColor = MaterialTheme.colorScheme.errorContainer
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
                            tint = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "Total percentage should be 100%. Current total: ${
                                String.format(
                                    "%.1f",
                                    totalPercentage
                                )
                            }%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}