package com.foryouandme.domain.usecase.task

import com.foryouandme.domain.usecase.auth.GetTokenUseCase
import com.foryouandme.entity.survey.SurveyAnswerUpdate
import java.io.File
import javax.inject.Inject

class AttachVideoUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val getTokenUseCase: GetTokenUseCase
) {

    suspend operator fun invoke(taskId: String, file: File) {

        repository.attachVideo(
            getTokenUseCase(),
            taskId,
            file
        )

    }

}