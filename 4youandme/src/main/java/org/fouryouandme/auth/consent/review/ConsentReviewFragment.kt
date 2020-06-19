package org.fouryouandme.auth.consent.review

import android.os.Bundle
import android.view.View
import arrow.core.Option
import arrow.core.extensions.fx
import kotlinx.android.synthetic.main.consent_review.*
import org.fouryouandme.R
import org.fouryouandme.core.arch.android.BaseFragment
import org.fouryouandme.core.arch.android.getFactory
import org.fouryouandme.core.arch.android.viewModelFactory
import org.fouryouandme.core.entity.configuration.Configuration
import org.fouryouandme.core.entity.consent.review.ConsentReview
import org.fouryouandme.core.ext.IORuntime
import org.fouryouandme.core.ext.navigator

class ConsentReviewFragment : BaseFragment<ConsentReviewViewModel>(R.layout.consent_review) {

    override val viewModel: ConsentReviewViewModel by lazy {
        viewModelFactory(
            this,
            getFactory { ConsentReviewViewModel(navigator, IORuntime) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.stateLiveData()
            .observeEvent {
                when (it) {
                    is ConsentReviewStateUpdate.Initialization ->
                        applyConfiguration(it.configuration, it.consentReview)
                }
            }

        viewModel.loadingLiveData()
            .observeEvent { loading.setVisibility(it.active) }

        viewModel.errorLiveData()
            .observeEvent {
                when (it.cause) {
                    ConsentReviewError.Initialization ->
                        error.setError(it.error) { viewModel.initialize(rootNavController()) }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Option.fx { !viewModel.state().configuration to !viewModel.state().consentReview }
            .fold(
                { viewModel.initialize(rootNavController()) },
                { applyConfiguration(it.first, it.second) }
            )
    }

    private fun applyConfiguration(
        configuration: Configuration,
        consentReview: ConsentReview
    ): Unit {

        title.text = consentReview.title
        title.setTextColor(configuration.theme.primaryTextColor.color())

        body.text = consentReview.body
        body.setTextColor(configuration.theme.fourthTextColor.color())

        agree.text = configuration.text.onboarding.onboardingAgreeButton
        agree.setTextColor(configuration.theme.secondaryColor.color())

        disagree.text = configuration.text.onboarding.onboardingDisagreeButton
        disagree.setTextColor(configuration.theme.secondaryColor.color())
    }
}