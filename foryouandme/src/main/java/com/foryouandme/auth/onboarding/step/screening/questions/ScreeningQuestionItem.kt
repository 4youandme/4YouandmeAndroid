package com.foryouandme.auth.onboarding.step.screening.questions

import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.foryouandme.R
import com.foryouandme.auth.onboarding.step.screening.questions.EScreeningQuestionPayload.ANSWER
import com.foryouandme.auth.onboarding.step.screening.questions.EScreeningQuestionPayload.NONE
import com.foryouandme.core.entity.configuration.Configuration
import com.foryouandme.core.entity.screening.ScreeningQuestion
import com.giacomoparisi.recyclerdroid.core.DroidItem
import com.giacomoparisi.recyclerdroid.core.DroidViewHolder
import com.giacomoparisi.recyclerdroid.core.ViewHolderFactory
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.screening_question.*

enum class EScreeningQuestionPayload {

    ANSWER,
    NONE

}

data class ScreeningQuestionItem(
    val configuration: Configuration,
    val question: ScreeningQuestion,
    val answer: String? = null
) : DroidItem<EScreeningQuestionPayload> {

    override fun areTheSame(other: DroidItem<Any>): Boolean =
        if (other is ScreeningQuestionItem) question.id == other.question.id else false

    override fun haveTheSameContent(other: DroidItem<Any>): Boolean =
        if (other is ScreeningQuestionItem)
            question.id == other.question.id &&
                    question.text == other.question.text &&
                    question.answers1.id == other.question.answers1.id &&
                    question.answers1.text == other.question.answers1.text &&
                    question.answers2.id == other.question.answers2.id &&
                    question.answers2.text == other.question.answers2.text &&
                    answer.orEmpty() == other.answer.orEmpty()
        else false


    override fun getPayload(other: DroidItem<Any>): List<EScreeningQuestionPayload> {

        val payload =
            mutableListOf<EScreeningQuestionPayload>()

        if (other is ScreeningQuestionItem)
            if (answer.orEmpty() != other.answer.orEmpty())
                payload.add(ANSWER)

        return payload
    }
}

fun ScreeningQuestion.toItem(configuration: Configuration): ScreeningQuestionItem =
    ScreeningQuestionItem(configuration, this)

class ScreeningQuestionViewHolder(
    parent: ViewGroup,
    onAnswer: (ScreeningQuestionItem) -> Unit
) : DroidViewHolder<ScreeningQuestionItem, EScreeningQuestionPayload>(
    parent,
    { layoutInflater, viewGroup, b ->
        layoutInflater.inflate(
            R.layout.screening_question,
            viewGroup,
            b
        )
    }
), LayoutContainer {

    init {

        itemView.findViewById<RadioButton>(R.id.answer_1_button)
            .setOnClickListener {
                onAnswer(item.copy(answer = item.question.answers1.id))
            }

        itemView.findViewById<RadioButton>(R.id.answer_2_button)
            .setOnClickListener {
                onAnswer(item.copy(answer = item.question.answers2.id))
            }
    }

    override fun bind(t: ScreeningQuestionItem, position: Int) {

        question.text = t.question.text
        question.setTextColor(t.configuration.theme.primaryTextColor.color())

        answer_1_text.text = t.question.answers1.text
        answer_1_text.setTextColor(t.configuration.theme.primaryTextColor.color())

        answer_2_text.text = t.question.answers2.text
        answer_2_text.setTextColor(t.configuration.theme.primaryTextColor.color())

        answer_1_button.isChecked = t.question.answers1.id == t.answer.orEmpty()
        answer_2_button.isChecked = t.question.answers2.id == t.answer.orEmpty()

        divider.setBackgroundColor(t.configuration.theme.deactiveColor.color())

    }

    override fun bind(
        t: ScreeningQuestionItem, position: Int,
        payloads: List<EScreeningQuestionPayload>
    ) {
        super.bind(t, position, payloads)

        payloads.forEach {
            when (it) {
                ANSWER -> {
                    answer_1_button.isChecked =
                        t.question.answers1.id == t.answer.orEmpty()
                    answer_2_button.isChecked =
                        t.question.answers2.id == t.answer.orEmpty()
                }
                NONE -> bind(t, position)
            }
        }
    }

    override val containerView: View? = itemView

    companion object {

        fun factory(onAnswer: (ScreeningQuestionItem) -> Unit): ViewHolderFactory =
            ViewHolderFactory(
                { ScreeningQuestionViewHolder(it, onAnswer) },
                { _, droidItem -> droidItem is ScreeningQuestionItem }
            )
    }
}