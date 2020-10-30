package com.foryouandme.researchkit.step.date

import android.content.Context
import com.foryouandme.researchkit.step.Back
import com.foryouandme.researchkit.step.Skip
import com.foryouandme.researchkit.step.Step
import com.foryouandme.researchkit.utils.ImageResource

class DatePickerStep(
    identifier: String,
    back: Back,
    skip: Skip,
    val backgroundColor: Int,
    val image: ImageResource?,
    val questionId: String,
    val question: (Context) -> String,
    val questionColor: Int,
    val shadowColor: Int,
    val buttonImage: ImageResource,
    val minDate: Long?,
    val maxDate: Long?
) : Step(identifier, back, skip, { DatePickerStepFragment() })