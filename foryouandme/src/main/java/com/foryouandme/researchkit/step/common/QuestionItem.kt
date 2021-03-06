package com.foryouandme.researchkit.step.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.foryouandme.R
import com.foryouandme.databinding.StepQuestionItemBinding
import com.foryouandme.entity.source.ImageSource
import com.foryouandme.entity.source.applyImage
import com.giacomoparisi.recyclerdroid.core.DroidItem
import com.giacomoparisi.recyclerdroid.core.compare
import com.giacomoparisi.recyclerdroid.core.holder.DroidViewHolder
import com.giacomoparisi.recyclerdroid.core.holder.DroidViewHolderFactory
import kotlinx.android.extensions.LayoutContainer

data class QuestionItem(
    val id: String,
    val question: (Context) -> String,
    val questionColor: Int,
    val image: ImageSource?
) : DroidItem<Unit> {

    override fun areTheSame(other: DroidItem<Any>): Boolean =
        other.compare<QuestionItem> { it.id == id }

    override fun getPayload(other: DroidItem<Any>): List<Unit> = emptyList()

    override fun haveTheSameContent(other: DroidItem<Any>): Boolean =
        other.compare<QuestionItem> { it == this }

}

class QuestionViewHolder(
    parent: ViewGroup
) : DroidViewHolder<QuestionItem, Unit>(parent, R.layout.step_question_item), LayoutContainer {

    override val containerView: View = itemView

    override fun bind(item: QuestionItem, position: Int) {

        val binding = StepQuestionItemBinding.bind(itemView)

        item.image?.let { binding.icon.applyImage(it) }
        binding.icon.isVisible = item.image != null

        binding.question.text = item.question(context)
        binding.question.setTextColor(item.questionColor)

    }

    companion object {

        fun factory(): DroidViewHolderFactory =
            DroidViewHolderFactory(
                { QuestionViewHolder(it) },
                { _, item -> item is QuestionItem }
            )

    }

}