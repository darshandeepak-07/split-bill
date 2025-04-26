package com.example.splitbill.ui.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.splitbill.data.model.SplitType

@Composable
fun SplitMethodDialog(
    currentMethod: SplitType,
    onDismiss: () -> Unit,
    onMethodSelected: (SplitType) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Choose Split Method",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                SplitMethodOption(
                    title = "Split Evenly",
                    description = "Divide total amount equally among all individuals",
                    isSelected = currentMethod == SplitType.EVEN,
                    onClick = { onMethodSelected(SplitType.EVEN) }
                )

                SplitMethodOption(
                    title = "Split by Item",
                    description = "Each person pays for the items they ordered",
                    isSelected = currentMethod == SplitType.BY_ITEM,
                    onClick = { onMethodSelected(SplitType.BY_ITEM) }
                )

                SplitMethodOption(
                    title = "Split by Amount",
                    description = "Manually assign specific amount to each person",
                    isSelected = currentMethod == SplitType.BY_AMOUNT,
                    onClick = { onMethodSelected(SplitType.BY_AMOUNT) }
                )

                SplitMethodOption(
                    title = "Split by Shares",
                    description = "Assign shares to each person (e.g., 2 shares = paying double)",
                    isSelected = currentMethod == SplitType.BY_SHARE,
                    onClick = { onMethodSelected(SplitType.BY_SHARE) }
                )

                SplitMethodOption(
                    title = "Split by Percentage",
                    description = "Assign percentage of the total bill to each person",
                    isSelected = currentMethod == SplitType.BY_PERCENTAGE,
                    onClick = { onMethodSelected(SplitType.BY_PERCENTAGE) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}


@Composable
fun SplitMethodOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
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