package com.example.project_vig_la.vigilant

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.core.graphics.createBitmap

/**
 * Draws custom map markers programmatically — no asset files needed.
 * Each crime category gets a distinct color. The marker shape is a
 * rounded diamond with a small dot in the center.
 */
object CrimeMarkerFactory {

    private val markerCache = mutableMapOf<CrimeCategory, BitmapDescriptor>()

    fun getMarker(category: CrimeCategory): BitmapDescriptor {
        return markerCache.getOrPut(category) {
            BitmapDescriptorFactory.fromBitmap(createMarkerBitmap(category))
        }
    }

    private fun createMarkerBitmap(category: CrimeCategory): Bitmap {
        val size = 64
        val bitmap = createBitmap(size, size)
        val canvas = Canvas(bitmap)

        val centerX = size / 2f
        val centerY = size / 2f

        // Outer glow
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = category.color
            alpha = 60
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, 28f, glowPaint)

        // Main circle
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = category.color
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, 16f, fillPaint)

        // Dark border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawCircle(centerX, centerY, 16f, borderPaint)

        // Inner bright dot
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 200
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, 4f, dotPaint)

        return bitmap
    }
}

enum class CrimeCategory(val color: Int) {
    VIOLENT(Color.parseColor("#FF1A1A")),       // bright red
    THEFT(Color.parseColor("#FF6600")),          // orange
    BURGLARY(Color.parseColor("#FFAA00")),       // amber
    VEHICLE(Color.parseColor("#00CCFF")),        // cyan
    VANDALISM(Color.parseColor("#AA00FF")),      // purple
    SEXUAL(Color.parseColor("#FF0066")),         // hot pink
    FRAUD(Color.parseColor("#00FF99")),          // neon green
    DRUGS(Color.parseColor("#FFFF00")),          // yellow
    OTHER(Color.parseColor("#888888"));          // grey

    companion object {
        /**
         * NIBRS descriptions look like:
         * "484(A) - PC - M - Theft Of Motor Vehicle Parts/Accessories - Petty Theft - 23G"
         * "245(A)(1) - PC - F - Assault With Deadly Weapon - Aggravated Assault - 13A"
         *
         * The NIBRS code suffix hints at the category:
         * 13A/13B = Assault, 09A = Murder, 120 = Robbery,
         * 23A-H = Theft/Larceny, 220 = Burglary, 240 = Vehicle Theft,
         * 11A-D = Sex offenses, 35A-B = Drugs, 26A-E = Fraud
         */
        fun fromDescription(description: String?): CrimeCategory {
            if (description == null) return OTHER

            val desc = description.uppercase()
            return when {
                desc.contains("ASSAULT") || desc.contains("ROBBERY") ||
                        desc.contains("HOMICIDE") || desc.contains("MURDER") ||
                        desc.contains("KIDNAP") || desc.contains("WEAPON") ||
                        desc.contains("BATTERY") || desc.contains("- 13A") ||
                        desc.contains("- 13B") || desc.contains("- 09A") ||
                        desc.contains("- 120") -> VIOLENT

                desc.contains("THEFT") || desc.contains("STOLEN") ||
                        desc.contains("SHOPLIFTING") || desc.contains("LARCENY") ||
                        desc.contains("PICKPOCKET") || desc.contains("- 23") -> THEFT

                desc.contains("BURGLARY") || desc.contains("TRESPASS") ||
                        desc.contains("- 220") -> BURGLARY

                desc.contains("VEHICLE") || desc.contains("- 240") -> VEHICLE

                desc.contains("VANDALISM") || desc.contains("ARSON") ||
                        desc.contains("DESTRUCTION") || desc.contains("- 290") -> VANDALISM

                desc.contains("RAPE") || desc.contains("SEXUAL") ||
                        desc.contains("LEWD") || desc.contains("INDECENT") ||
                        desc.contains("- 11A") || desc.contains("- 11B") ||
                        desc.contains("- 11C") || desc.contains("- 11D") -> SEXUAL

                desc.contains("FRAUD") || desc.contains("FORGERY") ||
                        desc.contains("IDENTITY") || desc.contains("COUNTERFEIT") ||
                        desc.contains("EMBEZZLEMENT") || desc.contains("- 26") -> FRAUD

                desc.contains("DRUG") || desc.contains("NARCOTIC") ||
                        desc.contains("- 35A") || desc.contains("- 35B") -> DRUGS

                else -> OTHER
            }
        }
    }
}



//
//enum class CrimeCategory(val color: Int) {
//    VIOLENT(Color.parseColor("#FF1A1A")),      // bright red
//    THEFT(Color.parseColor("#FF6600")),         // orange
//    BURGLARY(Color.parseColor("#FFAA00")),      // amber
//    VEHICLE(Color.parseColor("#00CCFF")),        // cyan
//    VANDALISM(Color.parseColor("#AA00FF")),      // purple
//    SEXUAL(Color.parseColor("#FF0066")),         // hot pink
//    FRAUD(Color.parseColor("#00FF99")),           // neon green
//    OTHER(Color.parseColor("#888888"));          // grey
//
//    companion object {
//        /**
//         * Maps the crime description from the API to a category.
//         * The API returns descriptions like "ASSAULT WITH DEADLY WEAPON",
//         * "VEHICLE - STOLEN", "BURGLARY FROM VEHICLE", etc.
//         */
//        fun fromDescription(description: String?): CrimeCategory {
//            if (description == null) return OTHER
//
//            val desc = description.uppercase()
//            return when {
//                desc.contains("ASSAULT") || desc.contains("ROBBERY") ||
//                        desc.contains("HOMICIDE") || desc.contains("KIDNAP") ||
//                        desc.contains("WEAPON") || desc.contains("SHOTS") ||
//                        desc.contains("BATTERY") -> VIOLENT
//
//                desc.contains("THEFT") || desc.contains("STOLEN") ||
//                        desc.contains("PICKPOCKET") || desc.contains("PURSE") ||
//                        desc.contains("SHOPLIFTING") -> THEFT
//
//                desc.contains("BURGLARY") || desc.contains("TRESPASSING") -> BURGLARY
//
//                desc.contains("VEHICLE") -> VEHICLE
//
//                desc.contains("VANDALISM") || desc.contains("ARSON") -> VANDALISM
//
//                desc.contains("RAPE") || desc.contains("SEXUAL") ||
//                        desc.contains("LEWD") || desc.contains("INDECENT") -> SEXUAL
//
//                desc.contains("FRAUD") || desc.contains("FORGERY") ||
//                        desc.contains("IDENTITY") || desc.contains("COUNTERFEIT") ||
//                        desc.contains("EMBEZZLEMENT") -> FRAUD
//
//                else -> OTHER
//            }
//        }
//    }
//}