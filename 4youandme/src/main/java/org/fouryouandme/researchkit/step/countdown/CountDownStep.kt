package org.fouryouandme.researchkit.step.countdown

import android.content.Context
import org.fouryouandme.researchkit.step.Step

class CountDownStep(
    identifier: String,
    val backgroundColor: Int,
    val title: (Context) -> String,
    val titleColor: Int,
    val description: (Context) -> String,
    val descriptionColor: Int,
    val seconds: Int,
    val counterColor: Int,
    val counterProgressColor: Int
) : Step(identifier, { CountDownStepFragment() })