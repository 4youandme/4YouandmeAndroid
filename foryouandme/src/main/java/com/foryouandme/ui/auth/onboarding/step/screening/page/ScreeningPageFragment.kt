package com.foryouandme.ui.auth.onboarding.step.screening.page

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.foryouandme.R
import com.foryouandme.ui.auth.onboarding.step.screening.ScreeningSectionFragment
import com.foryouandme.entity.configuration.Configuration
import com.foryouandme.entity.screening.Screening
import com.foryouandme.core.ext.*
import com.foryouandme.core.view.page.EPageType
import kotlinx.android.synthetic.main.screening.*
import kotlinx.android.synthetic.main.screening_page.*

class ScreeningPageFragment : ScreeningSectionFragment(R.layout.screening_page) {

    private val args: ScreeningPageFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screeningAndConfiguration { config, state ->
            setupView()
            applyData(config, state.screening)
        }

    }

    private suspend fun setupView(): Unit =
        evalOnMain {

            screeningFragment().toolbar.showBackSecondaryButton(imageConfiguration)
            {
                startCoroutineAsync {
                    viewModel.back(
                        screeningNavController(),
                        onboardingStepNavController(),
                        authNavController(),
                        rootNavController()
                    )
                }
            }

        }


    private suspend fun applyData(configuration: Configuration, screening: Screening): Unit =
        evalOnMain {

            setStatusBar(configuration.theme.secondaryColor.color())

            root.setBackgroundColor(configuration.theme.secondaryColor.color())

            screeningFragment()
                .showAbort(configuration, configuration.theme.primaryColorEnd.color())

            screening.pages.firstOrNull { it.id == args.id }
                ?.let { data ->
                    page.applyData(
                        configuration,
                        data,
                        EPageType.INFO,
                        { option ->
                            option.fold(
                                {
                                    startCoroutineAsync {
                                        viewModel.questions(
                                            screeningNavController(),
                                            false
                                        )
                                    }
                                },
                                {
                                    startCoroutineAsync {
                                        viewModel.page(
                                            screeningNavController(),
                                            it.id,
                                            false
                                        )
                                    }
                                })
                        },
                        {},
                        { startCoroutineAsync { viewModel.web(rootNavController(), it) } }
                    )
                }

        }
}