package com.example.project_vig_la.data

import com.google.gson.annotations.SerializedName

data class CrimeIncident(
    @SerializedName("caseno")
    val id: String,

    @SerializedName("date_occ")
    val dateOccurred: String?,

    @SerializedName("time_occ")
    val timeOccurred: String?,

    @SerializedName("area_name")
    val areaName: String?,

    @SerializedName("nibr_description")
    val crimeDescription: String?,

    @SerializedName("crime_against")
    val crimeAgainst: String?,

    @SerializedName("premis_desc")
    val premiseDescription: String?,

    @SerializedName("status_desc")
    val statusDescription: String?,

    @SerializedName("weapon_desc")
    val weaponDescription: String?,

    @SerializedName("victim_shot")
    val victimShot: String?,

    @SerializedName("domestic_violence_crime")
    val domesticViolence: String?,

    @SerializedName("gang_related_crime")
    val gangRelated: String?,

    @SerializedName("hndrdth_loc_chk")
    val location: String?,

    @SerializedName("hndrdth_lat")
    val latString: String?,

    @SerializedName("hndrdth_lon")
    val lonString: String?
) {
    // Lat/lon are strings in the new API — convert safely
    val lat: Double? get() = latString?.toDoubleOrNull()
    val lon: Double? get() = lonString?.toDoubleOrNull()
}
