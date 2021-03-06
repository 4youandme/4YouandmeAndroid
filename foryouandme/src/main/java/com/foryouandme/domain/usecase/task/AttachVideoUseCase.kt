package com.foryouandme.domain.usecase.task

import com.foryouandme.domain.usecase.user.GetTokenUseCase
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