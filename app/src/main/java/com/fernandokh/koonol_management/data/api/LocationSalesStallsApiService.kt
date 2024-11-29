package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.LocationSalesStallsCreateEditModel
import retrofit2.http.Body
import retrofit2.http.POST

interface LocationSalesStallsApiService {
    @POST ("location-sales-stalls")
    suspend fun createLocationSalesStalls(@Body locationSalesStalls: LocationSalesStallsCreateEditModel): ApiResponse<LocationSalesStallsCreateEditModel>

}