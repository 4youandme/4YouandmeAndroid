package com.foryouandme.ui.auth.onboarding.step.consent.review.info

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foryouandme.R
import com.foryouandme.ui.auth.onboarding.step.consent.review.ConsentReviewSectionFragment
import com.foryouandme.entity.configuration.Configuration
import com.foryouandme.entity.configuration.HEXColor
import com.foryouandme.entity.configuration.HEXGradient
import com.foryouandme.entity.configuration.button.button
import com.foryouandme.core.ext.evalOnMain
import com.foryouandme.core.ext.setStatusBar
import com.foryouandme.core.ext.startCoroutineAsync
import com.giacomoparisi.recyclerdroid.core.DroidItem
import com.giacomoparisi.recyclerdroid.core.adapter.StableDroidAdapter
import kotlinx.android.synthetic.main.consent_review_info.*

class ConsentReviewInfoFragment : ConsentReviewSectionFragment(R.layout.consent_review_info) {

    private val adapter: StableDroidAdapter by lazy {
        StableDroidAdapter(
            ConsentReviewHeaderViewHolder.factory(),
            ConsentReviewPageViewHolder.factory()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        consentReviewAndConfiguration { config, state ->

            setupView()
            applyConfiguration(config)
            applyItems(state.items)

        }

    }

    private suspend fun setupView(): Unit =
        evalOnMain {

            recycler_view.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recycler_view.adapter = adapter

            disagree.setOnClickListener {
                startCoroutineAsync { viewModel.disagree(consentReviewNavController()) }
            }
            agree.setOnClickListener {
                startCoroutineAsync { viewModel.optIns(consentNavController()) }
            }

        }

    private suspend fun applyConfiguration(
        configuration: Configuration
    ): Unit =
        evalOnMain {

            setStatusBar(configuration.theme.secondaryColor.color())

            root.setBackgroundColor(configuration.theme.secondaryColor.color())

            shadow.background =
                HEXGradient.from(
                    HEXColor.transparent(),
                    configuration.theme.primaryTextColor
                ).drawable(0.1f)

            agree.text = configuration.text.onboarding.agreeButton
            agree.setTextColor(configuration.theme.secondaryColor.color())
            agree.background =
                button(configuration.theme.primaryColorEnd.color())

            disagree.text = configuration.text.onboarding.disagreeButton
            disagree.setTextColor(configuration.theme.primaryColorEnd.color())
            disagree.background =
                button(configuration.theme.secondaryColor.color())
        }

    private suspend fun applyItems(items: List<DroidItem<Any>>): Unit =
        evalOnMain {

            adapter.submitList(items)

        }
}