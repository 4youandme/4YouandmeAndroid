package com.foryouandme.domain.usecase.location

import com.foryouandme.entity.location.LocationCoordinates
import javax.inject.Inject

class GetHomeLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {

    suspend operator fun invoke(): LocationCoordinates? = repository.getHomeLocation()

}