package com.foryouandme.core.ext

import android.view.View
import androidx.appcompat.widget.Toolbar
import com.foryouandme.core.arch.deps.ImageConfiguration

fun Toolbar.showBackButton(
    imageConfiguration: ImageConfiguration,
    back: () -> Unit
) {
    setNavigationIcon(imageConfiguration.back())
    setNavigationOnClickListener { back() }
}

fun Toolbar.showBackSecondaryButton(
    imageConfiguration: ImageConfiguration,
    back: () -> Unit
) {
    setNavigationIcon(imageConfiguration.backSecondary())
    setNavigationOnClickListener { back() }
}

fun Toolbar.showCloseButton(
    imageConfiguration: ImageConfiguration,
    back: () -> Unit
) {
    setNavigationIcon(imageConfiguration.close())
    setNavigationOnClickListener { back() }
}

fun Toolbar.showCloseSecondaryButton(
    imageConfiguration: ImageConfiguration,
    back: () -> Unit
) {
    setNavigationIcon(imageConfiguration.closeSecondary())
    setNavigationOnClickListener { back() }
}

fun Toolbar.removeBackButton(): Unit {

    navigationIcon = null
}

fun Toolbar.show(): Unit {

    visibility = View.VISIBLE
}

fun Toolbar.hide(): Unit {

    visibility = View.INVISIBLE
}

