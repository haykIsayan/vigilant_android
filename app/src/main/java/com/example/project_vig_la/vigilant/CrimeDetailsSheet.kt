package com.example.project_vig_la.vigilant

import android.os.Build
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_vig_la.domain.CrimeEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrimeDetailSheet(
    crime: CrimeEntity,
    onDismiss: () -> Unit
) {
    val category = CrimeCategory.fromDescription(crime.crimeDescription)
    val categoryColor = Color(category.color)
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF111111),
        contentColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .background(
                        color = Color(0xFF333333),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Category badge + crime type
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color = categoryColor, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category.displayName.uppercase(),
                    color = categoryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Crime description
            Text(
                text = formatDescription(crime.crimeDescription),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFF222222))

            Spacer(modifier = Modifier.height(16.dp))

            // Details grid
            crime.areaName?.let {
                DetailRow(label = "Area", value = it.trim())
            }
            crime.location?.let {
                DetailRow(label = "Location", value = it.trim())
            }
            crime.dateOccurred?.let {
                DetailRow(label = "Date", value = formatDate(it))
            }
            crime.timeOccurred?.let {
                DetailRow(label = "Time", value = formatTime(it))
            }
            crime.premiseDescription?.let {
                DetailRow(label = "Premise", value = it.trim())
            }
            crime.statusDescription?.let {
                DetailRow(label = "Status", value = it.trim())
            }
//            crime.crimeAgainst?.let {
//                DetailRow(label = "Crime Against", value = it.trim())
//            }

            // Flags
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF222222))
            Spacer(modifier = Modifier.height(16.dp))

//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                crime.victimShot?.let {
//                    if (it == "Yes") FlagBadge("Victim Shot", Color(0xFFFF1A1A))
//                }
//                crime.domesticViolence?.let {
//                    if (it == "Yes") FlagBadge("Domestic Violence", Color(0xFFFF6600))
//                }
//                crime.gangRelated?.let {
//                    if (it == "Yes") FlagBadge("Gang Related", Color(0xFFFFAA00))
//                }
//            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF666666),
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = Color(0xFFCCCCCC),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FlagBadge(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

/**
 * Cleans up NIBRS descriptions like:
 * "484(A) - PC - M - Theft Of Motor Vehicle Parts/Accessories - Petty Theft - 23G"
 * → "Theft Of Motor Vehicle Parts/Accessories"
 *
 * Extracts the readable part between the code prefix and the NIBRS suffix.
 */
private fun formatDescription(description: String?): String {
    if (description == null) return "Unknown Crime"
    val parts = description.split(" - ")
    return when {
        parts.size >= 4 -> parts[3].trim()
        parts.size >= 2 -> parts.last().trim()
        else -> description
    }
}

/**
 * Formats ISO date "2026-03-08T17:00:00.000" → "Mar 8, 2026"
 */
private fun formatDate(isoDate: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            val localDate = java.time.LocalDate.parse(isoDate.substring(0, 10))
            localDate.format(
                java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy")
            )
        } catch (e: Exception) {
            isoDate.substring(0, 10)
        }
    } else {
        ""
    }
}

/**
 * Formats time "1700" → "5:00 PM"
 */
private fun formatTime(time: String): String {
    return try {
        val hour = time.substring(0, 2).toInt()
        val minute = time.substring(2, 4)
        val amPm = if (hour >= 12) "PM" else "AM"
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        "$hour12:$minute $amPm"
    } catch (e: Exception) {
        time
    }
}