package com.example.project_vig_la.data

import com.example.project_vig_la.domain.CrimeEntity

class CrimeDtoToEntityMapper {
    fun map(dto: CrimeIncident): CrimeEntity {
        return CrimeEntity(
            id = dto.id,
            dateOccurred = dto.dateOccurred,
            timeOccurred = dto.timeOccurred,
            areaName = dto.areaName,
            crimeDescription = dto.crimeDescription,
            premiseDescription = dto.premiseDescription,
            weaponDescription = dto.weaponDescription,
            statusDescription = dto.statusDescription,
            location = dto.location,
            lat = dto.lat,
            lon = dto.lon
        )
    }
}