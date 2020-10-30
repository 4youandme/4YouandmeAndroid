package com.foryouandme.auth.integration.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.foryouandme.R
import com.foryouandme.auth.integration.SpecialLinkAction
import com.foryouandme.core.entity.configuration.Configuration
import com.foryouandme.core.entity.configuration.HEXColor
import com.foryouandme.core.entity.configuration.HEXGradient
import com.foryouandme.core.entity.configuration.button.button
import com.foryouandme.core.entity.integration.IntegrationApp
import com.foryouandme.core.entity.page.Page
import com.foryouandme.core.ext.evalOnMain
import com.foryouandme.core.ext.html.setHtmlText
import com.foryouandme.core.ext.imageConfiguration
import kotlinx.android.synthetic.main.integration_page_view.view.*


class IntegrationPageView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    init {

        View.inflate(context, R.layout.integration_page_view, this)

    }

    suspend fun applyData(
        configuration: Configuration,
        page: Page,
        nextAction: (Page?) -> Unit,
        specialLinkAction: (SpecialLinkAction) -> Unit,
        externalLinkAction: (String, Page?) -> Unit
    ): Unit =
        evalOnMain {

            title.setHtmlText(page.title, true)
            title.setTextColor(configuration.theme.primaryTextColor.color())

            description.setHtmlText(page.body, true)
            description.setTextColor(configuration.theme.primaryTextColor.color())

            shadow.background =
                HEXGradient.from(
                    HEXColor.transparent(),
                    configuration.theme.primaryTextColor
                ).drawable(0.3f)


            val specialAction = page.specialLinkValue.parseSpecialLink()

            when {
                specialAction != null -> {

                    next.isVisible = false

                    button_1_text.isVisible = true
                    button_1_text.text =
                        page.specialLinkLabel
                            ?: when (specialAction) {
                                is SpecialLinkAction.OpenApp ->
                                    configuration.text.onboarding.integration.openAppDefault
                                is SpecialLinkAction.Download ->
                                    configuration.text.onboarding.integration.downloadButtonDefault
                            }
                    button_1_text.setOnClickListener { specialLinkAction(specialAction) }

                    button_2_text.isVisible = true
                    button_2_text.text =
                        page.link1Label ?: configuration.text.onboarding.integration.nextDefault

                }
                page.externalLinkUrl != null -> {
                    next.isVisible = false

                    button_1_text.isVisible = true
                    button_1_text.text =
                        page.externalLinkLabel
                            ?: configuration.text.onboarding.integration.loginButtonDefault
                    button_1_text.setOnClickListener {
                        externalLinkAction.invoke(page.externalLinkUrl, page.link1)
                    }

                    button_2_text.isVisible = true
                    button_2_text.text =
                        page.link1Label ?: configuration.text.onboarding.integration.nextDefault

                }
                else -> {

                    next.isVisible = true
                    button_1_text.isVisible = false
                    button_2_text.isVisible = false

                }
            }

            next.background =
                button(context.resources, context.imageConfiguration.nextStepSecondary())

            button_1_text.background = button(configuration.theme.primaryColorEnd.color())
            button_1_text.setTextColor(configuration.theme.secondaryColor.color())

            button_2_text.background = button(configuration.theme.secondaryColor.color())
            button_2_text.setTextColor(configuration.theme.primaryColorEnd.color())

            button_2_text.setOnClickListener { nextAction(page.link1) }
            next.setOnClickListener { nextAction(page.link1) }

            page_root.setBackgroundColor(configuration.theme.secondaryColor.color())
            footer.setBackgroundColor(configuration.theme.secondaryColor.color())

        }

    private fun String?.parseSpecialLink(): SpecialLinkAction? =
        when {
            this == null -> null
            contains("open", true) ->
                IntegrationApp.fromIdentifier(this)?.let { SpecialLinkAction.OpenApp(it) }
            contains("download", true) ->
                IntegrationApp.fromIdentifier(this)?.let { SpecialLinkAction.Download(it) }
            else -> null
        }
}