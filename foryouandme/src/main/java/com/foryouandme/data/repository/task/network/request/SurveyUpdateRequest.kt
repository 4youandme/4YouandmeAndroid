package com.foryouandme.data.repository.task.network.request

import com.squareup.moshi.Json

data class SurveyUpdateRequest(
    @Json(name = "answers") val answers: List<AnswerUpdateRequest>
)

data class AnswerUpdateRequest(
    @Json(name = "question_id") val questionId: String,
    @Json(name = "answer") val answer: Any,
)

data class AnswerRequest(
    @Json(name = "answer_id") val answerId: String,
    @Json(name = "answer_text") val answerText: String?,
)


