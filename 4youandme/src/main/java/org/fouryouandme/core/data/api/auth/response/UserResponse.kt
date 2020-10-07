package org.fouryouandme.core.data.api.auth.response

import arrow.core.Either
import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import org.fouryouandme.core.entity.user.User

@JsonApi(type = "user")
data class UserResponse(
    @field:Json(name = "email") val email: String? = null,
    @field:Json(name = "phone_number") val phoneNumber: String? = null,
    @field:Json(name = "days_in_study") val daysInStudy: Int? = null,
    @field:Json(name = "identities") val identities: List<String>? = null,
    @field:Json(name = "on_boarding_completed") val onBoardingCompleted: Boolean? = null,
) : Resource() {

    suspend fun toUser(token: String): User? =
        Either.catch {

            User(
                id = id!!,
                email = email,
                phoneNumber = phoneNumber,
                daysInStudy = daysInStudy ?: 0,
                identities = identities ?: emptyList(),
                onBoardingCompleted = onBoardingCompleted!!,
                token = token
            )

        }.orNull()

}
