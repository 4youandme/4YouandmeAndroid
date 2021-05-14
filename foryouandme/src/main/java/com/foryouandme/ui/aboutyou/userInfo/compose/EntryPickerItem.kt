package com.foryouandme.ui.aboutyou.userInfo.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foryouandme.core.arch.deps.ImageConfiguration
import com.foryouandme.entity.configuration.Configuration
import com.foryouandme.ui.compose.ForYouAndMeTheme
import com.foryouandme.ui.compose.textfield.ForYouAndMeReadOnlyTextField
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.buttons
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.title
import kotlin.math.max

@Composable
fun EntryPickerItem(
    item: EntryItem.Picker,
    isEditable: Boolean,
    configuration: Configuration,
    imageConfiguration: ImageConfiguration,
    onValueSelected: (EntryItem.Picker, EntryItem.Picker.Value) -> Unit = { _, _ -> }
) {

    val dialog = remember { MaterialDialog() }
    dialog.build {

        title(item.name)

        listItemsSingleChoice(
            list = item.values.map { it.name },
            initialSelection = max(item.values.indexOf(item.value), 0)
        ) {
            val value = item.values.getOrNull(it)
            if (value != null)
                onValueSelected(item, value)
        }
        buttons {
            val style =
                MaterialTheme.typography.body1
                .copy(color = configuration.theme.primaryTextColor.value)
            positiveButton("Ok", textStyle = style)
            negativeButton("Cancel", textStyle = style)
        }

    }

    ForYouAndMeReadOnlyTextField(
        value = item.value?.name.orEmpty(),
        label = item.name,
        placeholder = null,
        configuration = configuration,
        trailingIcon = {
            Image(
                painter =
                painterResource(
                    id = if (isEditable) imageConfiguration.entryWrong()
                    else imageConfiguration.entryValid()
                ),
                contentDescription = null,
                colorFilter = ColorFilter.tint(configuration.theme.primaryTextColor.value),
                modifier = Modifier.size(40.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        onClick = { if (isEditable) dialog.show() }
    )
}

@Preview
@Composable
private fun EntryPickerItemPreview() {
    ForYouAndMeTheme {
        EntryPickerItem(
            item = EntryItem.Picker.mock(),
            isEditable = true,
            configuration = Configuration.mock(),
            imageConfiguration = ImageConfiguration.mock()
        )
    }
}