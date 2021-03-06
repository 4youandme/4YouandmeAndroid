package com.foryouandme.researchkit.step.number

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.foryouandme.researchkit.step.StepFragment
import com.foryouandme.researchkit.step.number.compose.NumberRangePickerPage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NumberRangePickerStepFragment : StepFragment() {

    private val viewModel: NumberRangePickerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val step = taskViewModel.getStepByIndexAs<NumberRangePickerStep>(indexArg())
        if (step != null) viewModel.execute(NumberPickerAction.SetStep(step))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                NumberRangePickerPage(
                    numberRangePickerViewModel = viewModel,
                    onNext = {
                        if (it != null) addResult(it)
                        next()
                    },
                    onSkip = { result, target ->
                        if (result != null) addResult(result)
                        skipTo(target)
                    }
                )
            }
        }

}