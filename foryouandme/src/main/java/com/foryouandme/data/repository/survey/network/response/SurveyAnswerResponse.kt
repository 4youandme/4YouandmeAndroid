package com.foryouandme.data.repository.survey.network.response

import arrow.core.Either
import com.foryouandme.entity.survey.SurveyAnswer
import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "possible_answer")
data class SurveyAnswerResponse(
    @field:Json(name = "text") val text: String? = null,
    @field:Json(name = "correct") val correct: Boolean? = null,
) : Resource() {

    suspend fun toSurveyAnswer(): SurveyAnswer? =
        Either.catch {

            SurveyAnswer(
                id,
                text!!,
                correct!!
            )

        }.orNull()

}