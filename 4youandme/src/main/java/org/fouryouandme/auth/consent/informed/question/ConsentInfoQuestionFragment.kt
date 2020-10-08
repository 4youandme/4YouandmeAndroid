package org.fouryouandme.auth.consent.informed.question

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arrow.core.getOrElse
import arrow.core.toOption
import com.giacomoparisi.recyclerdroid.core.adapter.StableDroidAdapter
import kotlinx.android.synthetic.main.consent_info.*
import kotlinx.android.synthetic.main.consent_info_question.*
import org.fouryouandme.R
import org.fouryouandme.auth.consent.informed.ConsentInfoSectionFragment
import org.fouryouandme.auth.consent.informed.ConsentInfoStateUpdate
import org.fouryouandme.core.entity.configuration.Configuration
import org.fouryouandme.core.entity.configuration.HEXColor
import org.fouryouandme.core.entity.configuration.HEXGradient
import org.fouryouandme.core.entity.configuration.button.button
import org.fouryouandme.core.entity.consent.informed.ConsentInfo
import org.fouryouandme.core.ext.*
import org.fouryouandme.core.ext.html.setHtmlText

class ConsentInfoQuestionFragment :
    ConsentInfoSectionFragment(R.layout.consent_info_question) {

    private val args: ConsentInfoQuestionFragmentArgs by navArgs()

    private val adapter: StableDroidAdapter by lazy {
        StableDroidAdapter(
            ConsentAnswerViewHolder.factory {
                startCoroutineAsync { viewModel.answer(args.index, it.answer.id) }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.stateLiveData()
            .observeEvent(name()) { update ->
                when (update) {
                    is ConsentInfoStateUpdate.Questions ->
                        startCoroutineAsync {
                            viewModel.getAnswers(args.index)?.let { applyAnswer(it) }
                        }
                }
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        consentInfoAndConfiguration { config, state ->

            setupView()
            applyData(config, state.consentInfo)
            if (adapter.itemCount <= 0)
                viewModel.getAnswers(args.index)?.let { applyAnswer(it) }

        }
    }

    suspend fun setupView(): Unit =
        evalOnMain {

            consentInfoFragment().toolbar.removeBackButton()

            recycler_view.layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    RecyclerView.VERTICAL,
                    false
                )
            recycler_view.adapter = adapter

            next.background = button(resources, imageConfiguration.signUpNextStep())
            next.setOnClickListener {

                startCoroutineAsync {
                    viewModel.nextQuestion(
                        consentInfoNavController(),
                        rootNavController(),
                        args.index
                    )
                }
            }
        }

    private suspend fun applyData(configuration: Configuration, consentInfo: ConsentInfo): Unit =
        evalOnMain {

            setStatusBar(configuration.theme.primaryColorStart.color())

            root.background =
                HEXGradient.from(
                    configuration.theme.primaryColorStart,
                    configuration.theme.primaryColorEnd
                ).drawable()

            consentInfoFragment()
                .showAbort(
                    configuration,
                    configuration.theme.secondaryColor.color()
                )

            question.setTextColor(configuration.theme.secondaryColor.color())
            question.setHtmlText(
                consentInfo.questions
                    .getOrNull(args.index)
                    .toOption()
                    .map { it.text }
                    .getOrElse { "" },
                true
            )

            shadow.background =
                HEXGradient.from(
                    HEXColor.transparent(),
                    configuration.theme.primaryColorEnd
                ).drawable()

        }

    private suspend fun applyAnswer(answers: List<ConsentAnswerItem>): Unit =
        evalOnMain {

            adapter.submitList(answers)

            next.isEnabled =
                answers.fold(
                    false,
                    { acc, answer -> acc || answer.isSelected }
                )
        }

}