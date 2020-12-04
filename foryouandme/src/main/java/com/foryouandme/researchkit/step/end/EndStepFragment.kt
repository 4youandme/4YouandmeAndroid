package com.foryouandme.researchkit.step.end

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.foryouandme.R
import com.foryouandme.entity.configuration.button.button
import com.foryouandme.core.ext.startCoroutineAsync
import com.foryouandme.researchkit.step.StepFragment
import kotlinx.android.synthetic.main.step_end.*
import kotlinx.android.synthetic.main.step_introduction.description
import kotlinx.android.synthetic.main.step_introduction.action_1
import kotlinx.android.synthetic.main.step_introduction.root
import kotlinx.android.synthetic.main.step_introduction.title


class EndStepFragment : StepFragment(R.layout.step_end) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val step =
            viewModel.getStepByIndexAs<EndStep>(indexArg())

        step?.let { applyData(it) }
    }

    private fun applyData(
        step: EndStep
    ): Unit {

        root.setBackgroundColor(step.backgroundColor)

        title.text = step.title(requireContext())
        title.setTextColor(step.titleColor)

        description.text = step.description(requireContext())
        description.setTextColor(step.descriptionColor)

        action_1.background = button(step.buttonColor)
        action_1.text = step.button(requireContext())
        action_1.setTextColor(step.buttonTextColor)
        action_1.setOnClickListener { startCoroutineAsync { viewModel.end() } }

        animationView.addValueCallback(
            KeyPath("Shape Outlines", "**"),
            LottieProperty.COLOR_FILTER,
            {
                PorterDuffColorFilter(
                    step.checkMarkBackgroundColor,
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        )

        animationView.addValueCallback(
            KeyPath("Line Outlines", "**"),
            LottieProperty.COLOR_FILTER,
            {
                PorterDuffColorFilter(
                    step.checkMarkColor,
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        )
    }

}