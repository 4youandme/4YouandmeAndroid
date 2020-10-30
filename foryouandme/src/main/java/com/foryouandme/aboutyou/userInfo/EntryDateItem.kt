package com.foryouandme.aboutyou.userInfo

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import com.foryouandme.R
import com.foryouandme.core.arch.deps.ImageConfiguration
import com.foryouandme.core.entity.configuration.Configuration
import com.giacomoparisi.recyclerdroid.core.DroidItem
import com.giacomoparisi.recyclerdroid.core.DroidViewHolder
import com.giacomoparisi.recyclerdroid.core.ViewHolderFactory
import com.giacomoparisi.recyclerdroid.core.compare
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.entry_date_item.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

data class EntryDateItem(
    val id: String,
    val configuration: Configuration,
    val imageConfiguration: ImageConfiguration,
    val name: String,
    val value: LocalDate?,
    val isEditable: Boolean
) : DroidItem<Unit> {

    override fun areTheSame(other: DroidItem<Any>): Boolean =
        other.compare<EntryDateItem> { it.id == id }

    override fun getPayload(other: DroidItem<Any>): List<Unit> = emptyList()

    override fun haveTheSameContent(other: DroidItem<Any>): Boolean =
        other.compare<EntryDateItem> { it == this }

}

class EntryDateViewHolder(
    parent: ViewGroup,
    private val onDateSelected: (EntryDateItem, LocalDate) -> Unit
) : DroidViewHolder<EntryDateItem, Unit>(parent, R.layout.entry_date_item), LayoutContainer {

    override val containerView: View? = itemView

    override fun bind(t: EntryDateItem, position: Int) {

        name.text = t.name
        name.setTextColor(t.configuration.theme.fourthTextColor.color())

        entry.setText(
            t.value?.format(
                DateTimeFormatter.ofPattern(
                    "dd MMMM yyyy",
                    Locale.getDefault()
                )
            ).orEmpty()
        )
        entry.setTextColor(t.configuration.theme.primaryTextColor.color())
        entry.isEnabled = false

        val placeholderDate = t.value ?: LocalDate.now()

        date_button.setOnClickListener {

            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->

                    val date = LocalDate.of(
                        year,
                        month + 1,  // +1 because Android starts to count months from 0
                        dayOfMonth
                    )

                    onDateSelected(t, date)

                },
                placeholderDate.year,
                placeholderDate.monthValue,
                placeholderDate.dayOfMonth
            ).show()
        }

        date_button.isEnabled = t.isEditable

        validation.imageTintList =
            ColorStateList.valueOf(t.configuration.theme.primaryTextColor.color())

        validation.setImageResource(
            if (t.isEditable) t.imageConfiguration.entryWrong()
            else t.imageConfiguration.entryValid()
        )

    }

    companion object {

        fun factory(onDateSelected: (EntryDateItem, LocalDate) -> Unit): ViewHolderFactory =
            ViewHolderFactory(
                { EntryDateViewHolder(it, onDateSelected) },
                { _, item -> item is EntryDateItem }
            )

    }

}