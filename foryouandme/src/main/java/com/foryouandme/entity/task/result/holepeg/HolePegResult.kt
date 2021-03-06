package com.foryouandme.entity.task.result.holepeg

import com.foryouandme.entity.task.holepeg.HolePegAttempt
import com.foryouandme.researchkit.result.StepResult
import com.foryouandme.researchkit.result.TaskResult
import com.foryouandme.researchkit.task.holepeg.HolePegTask
import com.foryouandme.researchkit.task.trailmaking.TrailMakingTask
import org.threeten.bp.ZonedDateTime

class HolePegResult(
    identifier: String,
    startDate: ZonedDateTime,
    endDate: ZonedDateTime,
    val steps: List<HolePegAttempt>
) : StepResult(identifier, startDate, endDate)

fun TaskResult.toHolePegResult(): HolePegResult? =
    results[HolePegTask.HOLE_PEG] as? HolePegResult