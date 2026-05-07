package com.example.project_vig_la.domain

data class CrimeEntity(
    val id: String,
    val dateOccurred: String?,
    val timeOccurred: String?,
    val areaName: String?,
    val crimeDescription: String?,
    val premiseDescription: String?,
    val weaponDescription: String?,
    val statusDescription: String?,
    val location: String?,
    val lat: Double?,
    val lon: Double?
)