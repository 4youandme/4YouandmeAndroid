package com.foryouandme.researchkit.step.scale

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.foryouandme.R
import com.foryouandme.core.entity.configuration.background.shadow
import com.foryouandme.core.ext.evalOnMain
import com.foryouandme.core.ext.startCoroutineAsync
import com.foryouandme.researchkit.result.SingleIntAnswerResult
import com.foryouandme.researchkit.skip.isInOptionalRange
import com.foryouandme.researchkit.step.StepFragment
import com.foryouandme.researchkit.utils.applyImage
import kotlinx.android.synthetic.main.step_scale.*
import org.threeten.bp.ZonedDateTime
import kotlin.math.absoluteValue

class ScaleStepFragment : StepFragment(R.layout.step_scale) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCoroutineAsync {

            val step =
                viewModel.getStepByIndexAs<ScaleStep>(indexArg())

            step?.let { applyData(it) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun applyData(
        step: ScaleStep
    ): Unit =

        evalOnMain {

            val start = ZonedDateTime.now()

            root.setBackgroundColor(step.backgroundColor)

            step.image?.let { icon.applyImage(it) }
            icon.isVisible = step.image != null

            question.text = step.question(requireContext())
            question.setTextColor(step.questionColor)

            value.text = step.minValue.toString()

            slider.progressTintList = ColorStateList.valueOf(step.progressColor)
            slider.min = 0
            slider.max = (step.maxValue.minus(step.minValue)).absoluteValue.div(step.interval)
            slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

                    value.text = i.times(step.interval).plus(step.minValue).toString()

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            shadow.background = shadow(step.shadowColor)

            button.applyImage(step.buttonImage)
            button.setOnClickListener {

                startCoroutineAsync {

                    viewModel.addResult(

                        SingleIntAnswerResult(
                            step.identifier,
                            start,
                            ZonedDateTime.now(),
                            step.questionId,
                            slider.progress.times(step.interval).plus(step.minValue)
                        )

                    )

                    checkSkip(step)

                }
            }

        }

    private suspend fun checkSkip(step: ScaleStep): Unit =
        evalOnMain {

            val skip = step.skips.firstOrNull()

            val value = slider.progress + step.minValue

            if (skip != null && isInOptionalRange(value, skip.min, skip.max)) skipTo(skip.stepId)
            else next()

        }

}