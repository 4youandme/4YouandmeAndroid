package org.fouryouandme.core.data.api.yourdata

import org.fouryouandme.core.data.api.Headers
import org.fouryouandme.core.data.api.yourdata.response.YourDataResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface YourDataApi {

    @GET("api/v1/studies/{study_id}/your_data")
    suspend fun getYourData(
        @Header(Headers.AUTH) token: String,
        @Path("study_id") studyId: String
    ): YourDataResponse

}