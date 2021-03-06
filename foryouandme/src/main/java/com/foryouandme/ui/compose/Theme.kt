package com.foryouandme.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.foryouandme.core.arch.LazyData
import com.foryouandme.entity.configuration.Configuration
import com.foryouandme.ui.compose.error.Error
import com.foryouandme.ui.compose.loading.Loading

@Composable
fun ForYouAndMeTheme(content: @Composable () -> Unit) {

    val localConfiguration = LocalConfiguration.current
    val dimensions =
        if (localConfiguration.screenWidthDp <= 360) smallDimensions
        else w360Dimensions

    ProvideDimens(dimensions = dimensions) {
        MaterialTheme(
            content = content,
            typography = ForYouAndMeTypography
        )
    }

}

@Composable
fun ForYouAndMeTheme(
    configuration: LazyData<Configuration>,
    onConfigurationError: () -> Unit = {},
    content: @Composable (Configuration) -> Unit = {}
) {

    val localConfiguration = LocalConfiguration.current
    val dimensions =
        if (localConfiguration.screenWidthDp <= 360) smallDimensions
        else w360Dimensions

    ProvideDimens(dimensions = dimensions) {
        MaterialTheme(
            content = {
                ConfigurationContent(
                    configuration,
                    onConfigurationError,
                    content
                )
            },
            typography = ForYouAndMeTypography
        )
    }

}

@Composable
fun ConfigurationContent(
    configuration: LazyData<Configuration>,
    onConfigurationError: () -> Unit,
    content: @Composable (Configuration) -> Unit
) {
    ForYouAndMeTheme {

        when (configuration) {
            is LazyData.Data ->
                content(configuration.value)
            is LazyData.Error ->
                Error(
                    error = configuration.error,
                    configuration = null,
                    modifier = Modifier.fillMaxSize(),
                    retry = onConfigurationError
                )
            is LazyData.Loading ->
                Loading(
                    configuration = null,
                    isVisible = true,
                    modifier = Modifier.fillMaxSize()
                )
            LazyData.Empty -> {

            }
        }

    }
}

object ForYouAndMeTheme {

    val dimens: Dimensions
        @Composable
        get() = LocalAppDimens.current

}

val Dimens: Dimensions
    @Composable
    get() = ForYouAndMeTheme.dimens



