package com.foryouandme.researchkit.step.introduction.list

import android.view.View
import android.view.ViewGroup
import com.foryouandme.R
import com.foryouandme.databinding.StepIntroductionItemBinding
import com.giacomoparisi.recyclerdroid.core.DroidItem
import com.giacomoparisi.recyclerdroid.core.holder.DroidViewHolder
import com.giacomoparisi.recyclerdroid.core.holder.DroidViewHolderFactory
import com.giacomoparisi.recyclerdroid.core.compare
import kotlinx.android.extensions.LayoutContainer

data class IntroductionItem(
    val title: String,
    val titleColor: Int,
    val description: String,
    val descriptionColor: Int
) : DroidItem<Unit> {

    override fun areTheSame(other: DroidItem<Any>): Boolean =
        compare<IntroductionItem> {
            it.title == title &&
                    it.description == description
        }

    override fun getPayload(other: DroidItem<Any>): List<Unit> = emptyList()

    override fun haveTheSameContent(other: DroidItem<Any>): Boolean =
        compare<IntroductionItem> {
            it.title == title &&
                    it.description == description
        }

}

class IntroductionViewHolder(
    parent: ViewGroup
) : DroidViewHolder<IntroductionItem, Unit>(
    parent,
    { layoutInflater, viewGroup, b ->
        layoutInflater.inflate(
            R.layout.step_introduction_item,
            viewGroup,
            b
        )
    }
), LayoutContainer {

    override val containerView: View = itemView

    override fun bind(item: IntroductionItem, position: Int) {

        val binding = StepIntroductionItemBinding.bind(itemView)

        binding.title.text = item.title
        binding.title.setTextColor(item.titleColor)

        binding.description.text = item.description
        binding.description.setTextColor(item.descriptionColor)

    }

    companion object {

        fun factory(): DroidViewHolderFactory =
            DroidViewHolderFactory(
                { IntroductionViewHolder(it) },
                { _, item -> item is IntroductionItem }
            )

    }

}