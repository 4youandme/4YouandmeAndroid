package com.foryouandme.data.repository.consent.user.network.response

import com.foryouandme.core.data.api.common.response.PageResponse
import com.foryouandme.entity.consent.user.ConsentUser
import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.ObjectDocument
import moe.banana.jsonapi2.Resource

@JsonApi(type = "signature")
data class ConsentUserResponse(
    @field:Json(name = "success_page") val successPage: HasOne<PageResponse>? = null
) : Resource() {

    suspend fun toConsentUser(document: ObjectDocument<ConsentUserResponse>): ConsentUser? =
        successPage?.get(document)?.toPage(document)?.let { ConsentUser(it) }

}