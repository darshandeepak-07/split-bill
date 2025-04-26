package com.example.splitbill.ui.screens.bill.splitUI

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun ItemSplitContent(
    bill: Bill,
    splitResults: List<Pair<String, Double>>,
    personAssignments: Map<String, Map<String, Boolean>>
) {
    var expandedPerson by remember { mutableStateOf<String?>(null) }

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
                "Split by Item",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Select items that each person ordered",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            bill.people.forEach { person ->
                val isExpanded = expandedPerson == person.name

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isExpanded)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedPerson = if (isExpanded) null else person.name
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val amount =
                                    splitResults.find { it.first == person.name }?.second ?: 0.0
                                Text(
                                    "$${String.format("%.2f", amount)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    "Select items ordered by ${person.name}:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                bill.items.forEach { item ->
                                    val personItems = personAssignments[person.name] ?: emptyMap()
                                    val isChecked = personItems[item.name] ?: false

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "${item.name} - $${
                                                String.format(
                                                    "%.2f",
                                                    item.price
                                                )
                                            } (x${item.quantity})",
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isChecked)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.outline,
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .background(
                                                    if (isChecked)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        Color.Transparent
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isChecked) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = MaterialTheme.colorScheme.onPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}