package com.example.project_vig_la.vigilant

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CrimeFilterBar(
    activeFilters: Set<CrimeCategory>,
    onToggle: (CrimeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = CrimeCategory.entries
    val midpoint = (categories.size + 1) / 2
    val topRow = categories.take(midpoint)
    val bottomRow = categories.drop(midpoint)

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            topRow.forEach { category ->
                CrimeFilterChip(
                    category = category,
                    isActive = category in activeFilters,
                    onClick = { onToggle(category) }
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            bottomRow.forEach { category ->
                CrimeFilterChip(
                    category = category,
                    isActive = category in activeFilters,
                    onClick = { onToggle(category) }
                )
            }
        }
    }
}

@Composable
private fun CrimeFilterChip(
    category: CrimeCategory,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val categoryColor = Color(category.color)

    val textColor by animateColorAsState(
        targetValue = if (isActive) categoryColor else Color(0xFF555555),
        label = "chipText"
    )
    val dotColor by animateColorAsState(
        targetValue = if (isActive) categoryColor else Color(0xFF444444),
        label = "chipDot"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isActive) Color(0xFF2A2A2A) else Color(0xFF1E1E1E),
        label = "chipBorder"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0D0D0D))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = dotColor, shape = CircleShape)
        )

        Text(
            text = category.displayName,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

val CrimeCategory.displayName: String
    get() = when (this) {
        CrimeCategory.VIOLENT -> "Violent"
        CrimeCategory.THEFT -> "Theft"
        CrimeCategory.BURGLARY -> "Burglary"
        CrimeCategory.VEHICLE -> "Vehicle"
        CrimeCategory.VANDALISM -> "Vandalism"
        CrimeCategory.SEXUAL -> "Sexual"
        CrimeCategory.FRAUD -> "Fraud"
        CrimeCategory.DRUGS -> "Drugs"
        CrimeCategory.OTHER -> "Other"
    }