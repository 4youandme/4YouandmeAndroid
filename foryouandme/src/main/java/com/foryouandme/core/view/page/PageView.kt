package com.foryouandme.core.view.page

import android.content.Context
import android.graphics.BitmapFactory
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.foryouandme.R
import com.foryouandme.core.cases.analytics.AnalyticsUseCase.logEvent
import com.foryouandme.core.ext.*
import com.foryouandme.core.ext.html.setHtmlText
import com.foryouandme.core.view.page.EPageType.*
import com.foryouandme.databinding.PageBinding
import com.foryouandme.domain.usecase.analytics.AnalyticsEvent
import com.foryouandme.domain.usecase.analytics.EAnalyticsProvider
import com.foryouandme.entity.configuration.Configuration
import com.foryouandme.entity.configuration.HEXColor
import com.foryouandme.entity.configuration.HEXGradient
import com.foryouandme.entity.configuration.button.button
import com.foryouandme.entity.page.Page
import com.foryouandme.entity.page.PageRef
import com.giacomoparisi.spandroid.SpanDroid
import com.giacomoparisi.spandroid.spanList

class PageView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    init {

        View.inflate(context, R.layout.page, this)

    }

    private val binding: PageBinding?
        get() = catchToNull { PageBinding.bind(getChildAt(0)) }

    fun applyData(
        configuration: Configuration,
        page: Page,
        pageType: EPageType,
        action1: (PageRef?) -> Unit,
        action2: ((PageRef?) -> Unit)? = null,
        extraStringAction: ((String) -> Unit)? = null,
        extraPageAction: ((PageRef) -> Unit)? = null,
        specialStringAction: ((String) -> Unit)? = null,
        specialStringPageAction: ((String, PageRef?) -> Unit)? = null
    ) {

        setUpButtons(page, pageType, configuration, action1, action2)

        setUpImage(page, pageType)

        setUpTitleDescription(page, pageType, configuration)

        setUpExtraAction(page, extraPageAction, extraStringAction, configuration)

        setUpShadow(configuration)

        setUpButtonsClick(page, action2, specialStringAction, specialStringPageAction)

        setUpBackgrounds(configuration)

    }

    private fun setUpImage(page: Page, EPageType: EPageType) {

        val viewBinding = binding

        if (viewBinding != null) {

            val params = viewBinding.icon.layoutParams

            params.height = if (EPageType == INFO) 60.dpToPx() else 100.dpToPx()
            params.width = if (EPageType == INFO) 60.dpToPx() else 100.dpToPx()

            viewBinding.icon.layoutParams = params

            val decodedString = page.image?.let { Base64.decode(it, Base64.DEFAULT) }
            val decodedByte =
                decodedString?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }

            decodedByte?.let { viewBinding.icon.setImageBitmap(it) }

        }

    }

    private fun setUpTitleDescription(
        page: Page,
        pageType: EPageType,
        configuration: Configuration
    ) {

        val viewBinding = binding

        if (viewBinding != null) {

            viewBinding.title.setHtmlText(page.title, true)
            viewBinding.title.setTextColor(configuration.theme.primaryTextColor.color())

            viewBinding.description.setHtmlText(page.body, true)
            viewBinding.description.setTextColor(configuration.theme.primaryTextColor.color())

            viewBinding.description.gravity =
                when (pageType) {
                    INFO -> Gravity.START
                    FAILURE -> Gravity.CENTER
                    SUCCESS -> Gravity.CENTER
                }

        }

    }

    private fun setUpExtraAction(
        page: Page,
        extraPageAction: ((PageRef) -> Unit)?,
        extraStringAction: ((String) -> Unit)?,
        configuration: Configuration
    ) {

        val viewBinding = binding

        if (viewBinding != null) {

            when {

                extraPageAction != null && page.linkModalValue != null -> {
                    viewBinding.external.text = page.linkModalLabel.orEmpty()
                    viewBinding.external.setTextColor(configuration.theme.primaryColorEnd.color())
                    viewBinding.external.isVisible = true
                    viewBinding.external.setOnClickListener {

                        startCoroutineAsync {
                            context.injector
                                .analyticsModule()
                                .logEvent(
                                    AnalyticsEvent.ScreenViewed.LearnMore,
                                    EAnalyticsProvider.ALL
                                )
                        }

                        extraPageAction(page.linkModalValue)

                    }
                }

                extraStringAction != null && page.externalLinkUrl != null -> {
                    viewBinding.external.text = page.externalLinkLabel.orEmpty()
                    viewBinding.external.setTextColor(configuration.theme.primaryColorEnd.color())
                    viewBinding.external.isVisible = true
                    viewBinding.external.setOnClickListener {

                        startCoroutineAsync {
                            context.injector
                                .analyticsModule()
                                .logEvent(
                                    AnalyticsEvent.ScreenViewed.LearnMore,
                                    EAnalyticsProvider.ALL
                                )
                        }

                        extraStringAction(page.externalLinkUrl)
                    }
                }

                else -> viewBinding.external.isVisible = false

            }

        }

    }

    private fun setUpShadow(configuration: Configuration) {

        binding?.shadow?.background =
            HEXGradient.from(HEXColor.transparent(), configuration.theme.primaryTextColor)
                .drawable(0.3f)

    }

    private fun setUpButtons(
        page: Page,
        pageType: EPageType,
        configuration: Configuration,
        action1: (PageRef?) -> Unit,
        action2: ((PageRef?) -> Unit)?,
    ) {

        val viewBinding = binding

        if (viewBinding != null) {

            viewBinding.action1TextSecondary.background =
                button(configuration.theme.secondaryColor.color())
            viewBinding.action1Text.background = button(configuration.theme.primaryColorEnd.color())
            viewBinding.specialAction.background =
                button(configuration.theme.primaryColorEnd.color())
            viewBinding.action1.background =
                button(
                    context.resources,
                    when (pageType) {
                        INFO -> context.imageConfiguration.nextStepSecondary()
                        FAILURE -> context.imageConfiguration.previousStepSecondary()
                        SUCCESS -> context.imageConfiguration.nextStepSecondary()
                    }
                )

            viewBinding.action1Text.setTextColor(configuration.theme.secondaryColor.color())
            viewBinding.action1TextSecondary.setTextColor(configuration.theme.primaryColorEnd.color())
            viewBinding.action2Text.setTextColor(configuration.theme.fourthTextColor.color())
            viewBinding.specialAction.setTextColor(configuration.theme.secondaryColor.color())

            viewBinding.action1Text.text = page.link1Label
            viewBinding.action1TextSecondary.text =
                page.link1Label ?: configuration.text.onboarding.integration.nextDefault
            viewBinding.action2Text.text =
                SpanDroid()
                    .append(
                        page.link2Label.orEmpty(),
                        spanList(context) { custom(UnderlineSpan()) }
                    )
                    .toSpannableString()


            viewBinding.action1.setOnClickListener { action1(page.link1) }
            viewBinding.action1Text.setOnClickListener { action1(page.link1) }
            viewBinding.action1TextSecondary.setOnClickListener { action1(page.link1) }
            viewBinding.action2Text.setOnClickListener { action2?.invoke(page.link2) }


        }

    }

    private fun setUpButtonsClick(
        page: Page,
        action2: ((PageRef?) -> Unit)?,
        specialStringAction: ((String) -> Unit)?,
        specialStringPageAction: ((String, PageRef?) -> Unit)?
    ) {

        val viewBinding = binding

        if (viewBinding != null) {
            when {

                page.specialLinkValue != null && specialStringAction != null -> {

                    viewBinding.action1.isVisible = false
                    viewBinding.action1Text.isVisible = false
                    viewBinding.action1TextSecondary.isVisible = true
                    viewBinding.specialAction.isVisible = true

                    viewBinding.specialAction.setOnClickListener { specialStringAction(page.specialLinkValue) }
                    viewBinding.specialAction.text = page.specialLinkLabel.orEmpty()

                }

                page.externalLinkUrl != null && specialStringPageAction != null -> {

                    viewBinding.action1.isVisible = false
                    viewBinding.action1Text.isVisible = false
                    viewBinding.action1TextSecondary.isVisible = true
                    viewBinding.specialAction.isVisible = true

                    viewBinding.specialAction.setOnClickListener {
                        specialStringPageAction(page.externalLinkUrl, page.link1)
                    }
                    viewBinding.specialAction.text = page.specialLinkLabel.orEmpty()

                }

                page.link1Label == null -> {

                    viewBinding.action1.isVisible = true
                    viewBinding.action1Text.isVisible = false
                    viewBinding.action1TextSecondary.isVisible = false
                    viewBinding.specialAction.isVisible = false

                }

                else -> {

                    viewBinding.action1.isVisible = false
                    viewBinding.action1Text.isVisible = true
                    viewBinding.action1TextSecondary.isVisible = false
                    viewBinding.specialAction.isVisible = false

                }

            }

            if (page.link2Label == null)
                viewBinding.action2Text.isVisible = false
            else
                viewBinding.action2Text.isVisible = action2 != null

        }

    }


    private fun setUpBackgrounds(configuration: Configuration) {

        val viewBinding = binding

        if (viewBinding != null) {
            viewBinding.pageRoot.setBackgroundColor(configuration.theme.secondaryColor.color())
            viewBinding.footer.setBackgroundColor(configuration.theme.secondaryColor.color())

        }

    }

}