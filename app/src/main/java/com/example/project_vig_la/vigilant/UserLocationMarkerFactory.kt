package com.example.project_vig_la.vigilant

import com.google.android.gms.maps.model.BitmapDescriptor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object UserLocationMarkerFactory {

    private var cached: BitmapDescriptor? = null

    fun get(): BitmapDescriptor {
        return cached ?: BitmapDescriptorFactory.fromBitmap(create()).also {
            cached = it
        }
    }

    private fun create(): Bitmap {
        val size = /*64*/ 264
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val cx = size / 2f
        val cy = size / 2f

        // Outer glow
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4488CCFF")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, 28f, glowPaint)

        // White border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, 18f, borderPaint)

        // Blue center
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4488FF")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, 14f, fillPaint)

        return bitmap
    }
}