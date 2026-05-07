package com.example.project_vig_la.domain

data class CrimeQueryEntity(
    val southLat: Double,
    val northLat: Double,
    val westLng: Double,
    val eastLng: Double,
    val limit: Int = 50
)