package org.fouryouandme.researchkit.step.date

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.step_date.*
import org.fouryouandme.R
import org.fouryouandme.core.entity.configuration.background.shadow
import org.fouryouandme.core.ext.evalOnMain
import org.fouryouandme.core.ext.startCoroutineAsync
import org.fouryouandme.researchkit.result.SingleAnswerResult
import org.fouryouandme.researchkit.step.StepFragment
import org.fouryouandme.researchkit.utils.applyImage
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class DatePickerStepFragment : StepFragment(R.layout.step_date) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCoroutineAsync {

            val step =
                viewModel.getStepByIndexAs<DatePickerStep>(indexArg())

            step?.let { applyData(it) }
        }
    }

    private suspend fun applyData(
        step: DatePickerStep
    ): Unit =

        evalOnMain {
            val start = ZonedDateTime.now()

            root.setBackgroundColor(step.backgroundColor)

            step.minDate?.let { date_picker.minDate = it }
            step.maxDate?.let { date_picker.maxDate = it }

            step.image?.let { icon.applyImage(it) }
            icon.isVisible = step.image != null

            question.text = step.question(requireContext())
            question.setTextColor(step.questionColor)

            shadow.background = shadow(step.shadowColor)

            button.applyImage(step.buttonImage)
            button.setOnClickListener {
                startCoroutineAsync {

                    viewModel.addResult(

                        SingleAnswerResult(
                            step.identifier,
                            start,
                            ZonedDateTime.now(),
                            step.questionId,
                            getFormattedSelectedDate()
                        )

                    )

                    next()
                }
            }

        }

    private fun getFormattedSelectedDate(): String {

        val year = date_picker.year
        val month = date_picker.month
        val day = date_picker.dayOfMonth

        val date =
            LocalDate.of(
                year,
                month + 1,  // +1 because Android starts to count months from 0
                day
            )

        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}