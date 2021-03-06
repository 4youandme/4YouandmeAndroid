package com.foryouandme.ui.dailysurveytime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.foryouandme.core.arch.android.BaseFragment
import com.foryouandme.databinding.DailySurveyTimeBinding
import com.foryouandme.ui.dailysurveytime.compose.DailySurveyTimePage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailySurveyTimeFragment : BaseFragment() {

    private val viewModel: DailySurveyTimeViewModel by viewModels()

    private val binding: DailySurveyTimeBinding?
        get() = view?.let { DailySurveyTimeBinding.bind(it) }

    @ExperimentalAnimationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                DailySurveyTimePage(
                    viewModel = viewModel,
                    onBack = { navigator.back(rootNavController()) }
                )
            }
        }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.stateUpdate
            .unwrapEvent(name)
            .onEach {
                when (it) {
                    DailySurveyTimeStateUpdate.GetUserSettings ->
                        applyTime()
                    DailySurveyTimeStateUpdate.SaveUserSettings ->
                        navigator.back(rootNavController())
                }
            }
            .observeIn(this)

        viewModel.loading
            .unwrapEvent(name)
            .onEach {
                when (it.task) {
                    DailySurveyTimeLoading.GetUserSettings ->
                        binding?.loading?.setVisibility(it.active, false)
                    DailySurveyTimeLoading.SaveUserSettings ->
                        binding?.loading?.setVisibility(it.active)
                }
            }
            .observeIn(this)

        viewModel.error
            .unwrapEvent(name)
            .onEach {
                when (it.cause) {
                    DailySurveyTimeError.GetUserSettings ->
                        binding?.error?.setError(it.error, configuration) {
                            viewModel.execute(DailySurveyTimeStateEvent.GetUserSettings)
                        }
                    DailySurveyTimeError.SaveUserSettings ->
                        binding?.error?.setError(it.error, configuration)
                }
            }
            .observeIn(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyConfiguration()
        if (viewModel.state.userSettings == null)
            viewModel.execute(DailySurveyTimeStateEvent.GetUserSettings)
        else
            applyTime()

    }

    override fun onConfigurationChange() {
        super.onConfigurationChange()
        applyConfiguration()
    }

    private fun applyConfiguration() {

        val viewBinding = binding
        val config = configuration

        if (viewBinding != null && config != null) {

            setStatusBar(config.theme.primaryColorStart.color())

            viewBinding.root.setBackgroundColor(config.theme.secondaryColor.color())

            viewBinding.toolbar.setBackgroundColor(config.theme.primaryColorStart.color())
            viewBinding.toolbar.showBackButton(imageConfiguration) {
                navigator.back(rootNavController())
            }

            viewBinding.title.setTextColor(config.theme.secondaryColor.color())
            viewBinding.title.text = config.text.profile.fifthItem

            viewBinding.description.setTextColor(config.theme.primaryTextColor.color())
            viewBinding.description.text = config.text.profile.dailySurveyTimeDescription

            viewBinding.timePicker.setIs24HourView(false)

            // TODO: move to configuration
            viewBinding.saveButton.text = "Save"
            viewBinding.saveButton.setTextColor(config.theme.secondaryTextColor.color())
            viewBinding.saveButton.background = button(config.theme.primaryColorStart.color())
            viewBinding.saveButton.setOnClickListener {

                val timePicker = binding?.timePicker

                if (timePicker != null) {

                    val localTime = LocalTime.of(timePicker.hour, timePicker.minute)
                    viewModel.execute(DailySurveyTimeStateEvent.SaveUserSettings(localTime))

                }

            }
        }
    }

    private fun applyTime() {

        val time = viewModel.state.userSettings?.dailySurveyTime
        val viewBinding = binding

        if (viewBinding != null && time != null) {
            viewBinding.timePicker.hour = time.hour
            viewBinding.timePicker.minute = time.minute
        }

    }*/

}