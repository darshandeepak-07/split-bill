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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.splitbill.data.model.Bill
import kotlin.math.roundToInt

@Composable
fun ShareSplitContent(
    bill: Bill,
    splitResults: List<Pair<String, Double>>,
    shareAssignments: Map<String, Int>,
    onShareChange: (String, Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Split by Shares",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Assign shares to each person",
                style = MaterialTheme.typography.bodyMedium
            )

            val totalShares = shareAssignments.values.sum().takeIf { it > 0 } ?: bill.people.size

            Spacer(modifier = Modifier.height(16.dp))

            bill.people.forEach { person ->
                val shares = shareAssignments[person.name] ?: 1
                val amount = (shares * 100.0 / totalShares).roundToInt()

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

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "(${shares} shares)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                "$${amount}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = {
                                if (shares > 1) onShareChange(person.name, shares - 1)
                            },
                            modifier = Modifier.size(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("-")
                        }

                        Text(
                            "$shares",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )

                        FilledTonalButton(
                            onClick = { onShareChange(person.name, shares + 1) },
                            modifier = Modifier.size(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("+")
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Total Shares: $totalShares",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}